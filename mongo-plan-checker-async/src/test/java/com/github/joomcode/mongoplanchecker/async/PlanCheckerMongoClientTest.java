package com.github.joomcode.mongoplanchecker.async;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.github.joomcode.mongoplanchecker.testutil.AbstractMongoTest;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClients;
import com.mongodb.client.result.DeleteResult;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("deprecation")
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
    collection = mongoClient.getDatabase("test").getCollection("testSync");
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    collection.insertMany(
        IntStream.range(0, 1_000)
            .mapToObj(
                id ->
                    new Document("foo", "bar")
                        .append("_id", id)
                        .append("id", id)
                        .append("id100", id / 100))
            .collect(Collectors.toList()),
        (result, t) -> {
          error.set(t);
          notifyLock(lock);
        });
    waitLock(lock, error);

    collection.createIndex(
        new Document("id", 1),
        (result, t) -> {
          error.set(t);
          notifyLock(lock);
        });
    waitLock(lock, error);
    collection.createIndex(
        new Document("id100", 1),
        (result, t) -> {
          error.set(t);
          notifyLock(lock);
        });
    waitLock(lock, error);
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
  void testFindFilter() {
    this.<Document>testMethodFilter(
        callback -> collection.find(new Document("foo", "bar")).first(callback));
  }

  @Test
  void testRemoveOneFilter() {
    this.<DeleteResult>testMethodFilter(
        callback -> collection.deleteOne(new Document("foo", "bar"), callback));
  }

  @Test
  void testRemoveManyFilter() {
    this.<DeleteResult>testMethodFilter(
        callback -> collection.deleteMany(new Document("foo", "bar"), callback));
  }

  @Test
  void testCountFilter() {
    this.<Long>testMethodFilter(callback -> collection.count(new Document("foo", "bar"), callback));
  }

  private <T> void testMethodFilter(Consumer<SingleResultCallback<T>> tester) {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    SingleResultCallback<T> callback =
        (result, t) -> {
          error.set(t);
          notifyLock(lock);
        };

    tester.accept(callback);

    BadPlanException exception = assertThrows(BadPlanException.class, () -> waitLock(lock, error));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testCountFilterGood() throws Throwable {
    AtomicBoolean lock = new AtomicBoolean(true);
    AtomicReference<Throwable> error = new AtomicReference<>();
    AtomicLong result = new AtomicLong();
    collection.count(
        new Document("id100", 3),
        (res, t) -> {
          error.set(t);
          result.set(res);
          notifyLock(lock);
        });
    waitLock(lock, error);
    assertEquals(100L, result.get());
  }
}
