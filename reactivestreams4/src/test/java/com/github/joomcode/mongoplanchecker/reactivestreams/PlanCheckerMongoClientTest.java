package com.github.joomcode.mongoplanchecker.reactivestreams;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.github.joomcode.mongoplanchecker.testutil.AbstractMongoTest;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class PlanCheckerMongoClientTest extends AbstractMongoTest {

  private static PlanCheckerMongoClient mongoClient;
  private static PlanCheckerMongoCollection<Document> collection;

  @BeforeAll
  static void setup() throws Throwable {
    mongoClient =
        new PlanCheckerMongoClient(
            MongoClients.create(
                MongoClientSettings.builder()
                    .applyToClusterSettings(
                        builder ->
                            builder.hosts(
                                singletonList(
                                    new ServerAddress(
                                        MONGO.getContainerIpAddress(),
                                        MONGO.getMappedPort(MONGO_PORT)))))
                    .build()),
            new PlanChecker());
    collection = mongoClient.getDatabase("test").getCollection("testreactive");
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection
        .insertMany(
            IntStream.range(0, 1_000)
                .mapToObj(
                    id ->
                        new Document("foo", "bar")
                            .append("_id", id)
                            .append("id", id)
                            .append("id100", id / 100))
                .collect(Collectors.toList()))
        .subscribe(new SingleSubscriber<>(error, lock));
    waitLock(lock, error);

    collection.createIndex(new Document("id", 1)).subscribe(new SingleSubscriber<>(error, lock));
    waitLock(lock, error);
    collection.createIndex(new Document("id100", 1)).subscribe(new SingleSubscriber<>(error, lock));
    waitLock(lock, error);
  }

  public static class SingleSubscriber<T> implements Subscriber<T> {
    private final AtomicReference<Throwable> error;
    private final AtomicReference<T> value;
    private final AtomicBoolean lock;

    public SingleSubscriber(
        AtomicReference<Throwable> error, AtomicReference<T> value, AtomicBoolean lock) {
      this.error = error;
      this.value = value;
      this.lock = lock;
    }

    private SingleSubscriber(AtomicReference<Throwable> error, AtomicBoolean lock) {
      this.error = error;
      this.lock = lock;
      this.value = null;
    }

    @Override
    public void onSubscribe(Subscription s) {
      s.request(1);
    }

    @Override
    public void onNext(T t) {
      if (value != null) {
        value.set(t);
      }
    }

    @Override
    public void onError(Throwable t) {
      error.set(t);
    }

    @Override
    public void onComplete() {
      notifyLock(lock);
    }
  }

  private static void notifyLock(AtomicBoolean lock) {
    synchronized (lock) {
      lock.set(false);
      lock.notifyAll();
    }
  }

  private static void waitLock(AtomicBoolean lock, AtomicReference<Throwable> error)
      throws Throwable {
    synchronized (lock) {
      while (lock.get()) {
        lock.wait();
      }
    }
    if (error.get() != null) {
      throw error.get();
    }
    lock.set(true);
  }

  @AfterAll
  static void tearDown() {
    mongoClient.close();
  }

  @Test
  void testCountFilter() {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection
        .countDocuments(new Document("foo", "bar"))
        .subscribe(new SingleSubscriber<>(error, lock));

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testCountFilterGood() throws Throwable {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    AtomicReference<Long> value = new AtomicReference<>();
    collection
        .countDocuments(new Document("id", 333))
        .subscribe(new SingleSubscriber<>(error, value, lock));

    waitLock(lock, error);
    assertEquals(1L, value.get());
  }

  @Test
  void testFindFilter() {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection.find(new Document("foo", "bar")).subscribe(new SingleSubscriber<>(error, lock));

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindFirst() {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection
        .find(new Document("foo", "bar"))
        .first()
        .subscribe(new SingleSubscriber<>(error, lock));

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindFirstGood() throws Throwable {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    AtomicReference<Document> value = new AtomicReference<>();
    collection
        .find(new Document("id", 333))
        .first()
        .subscribe(new SingleSubscriber<>(error, value, lock));

    waitLock(lock, error);
    assertNotNull(value.get());
  }

  @Test
  void testFindFirstSort() {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection
        .find(new Document("id", 333))
        .sort(Sorts.ascending("id100"))
        .first()
        .subscribe(new SingleSubscriber<>(error, lock));

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 0, 1), exception.getViolations());
  }

  @Test
  void testDeleteManyFilter() {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection
        .deleteMany(new Document("foo", "bar"))
        .subscribe(new SingleSubscriber<>(error, lock));

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }
}
