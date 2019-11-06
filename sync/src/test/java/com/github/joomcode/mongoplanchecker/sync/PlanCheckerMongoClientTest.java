package com.github.joomcode.mongoplanchecker.sync;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.github.joomcode.mongoplanchecker.testutil.AbstractMongoTest;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Sorts;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class PlanCheckerMongoClientTest extends AbstractMongoTest {
  private static PlanCheckerMongoClient mongoClient;
  private static MongoCollection<Document> collection;

  @BeforeAll
  static void setup() {
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
    collection.insertMany(
        IntStream.range(0, 1_000)
            .mapToObj(
                id ->
                    new Document("foo", "bar")
                        .append("_id", id)
                        .append("id", id)
                        .append("id100", id / 100))
            .collect(Collectors.toList()));
    collection.createIndex(new Document("id", 1));
    collection.createIndex(new Document("id100", 1));
  }

  @AfterAll
  static void tearDown() {
    mongoClient.close();
  }

  @Test
  void testFindFilterGood() {
    assertNotNull(collection.find(new Document("id", 333)).first());
  }

  @Test
  void testSkipGood() {
    assertNotNull(collection.find(new Document("id100", 333 / 100)).skip(99).first());
  }

  @Test
  void testExcessReadFind() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection
                    .find(new Document("id", 333).append("id100", 333 / 100))
                    .hint(new Document("id100", 1))
                    .first());
    assertEquals(new Violations(false, true, 0, 0), exception.getViolations());
  }

  @Test
  void testFind() {
    BadPlanException exception =
        assertThrows(BadPlanException.class, () -> collection.find().first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindClass() {
    BadPlanException exception =
        assertThrows(BadPlanException.class, () -> collection.find(Document.class).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class, () -> collection.find(new Document("foo", "bar")).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindFilterClass() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.find(new Document("foo", "bar"), Document.class).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindSession() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class, () -> collection.find(mongoClient.startSession()).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindSessionFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.find(mongoClient.startSession(), new Document("foo", "bar")).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindSessionFilterClass() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection
                    .find(mongoClient.startSession(), new Document("foo", "bar"), Document.class)
                    .first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindSessionClass() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.find(mongoClient.startSession(), Document.class).first());
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testReplaceOneFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.replaceOne(new Document("foo", "bar"), new Document("foo", "bar2")));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testFindOneAndReplaceFilterSort() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.findOneAndReplace(
                    new Document("id", "7"),
                    new Document("foo", "bar2"),
                    new FindOneAndReplaceOptions().sort(Sorts.ascending("absent"))));
    assertEquals(new Violations(false, false, 0, 1), exception.getViolations());
  }

  @Test
  void testDeleteOneFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class, () -> collection.deleteOne(new Document("foo", "bar")));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testDeleteManyFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.deleteMany(new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testDeleteOneSessionFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.deleteOne(mongoClient.startSession(), new Document("foo", "bar")));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testDeleteManySessionFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.deleteMany(
                    mongoClient.startSession(), new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  @SuppressWarnings("deprecation")
  void testCountFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.count(new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testCountDocumentsFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () -> collection.countDocuments(new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  @SuppressWarnings("deprecation")
  void testCountFilterHint() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.count(
                    new Document("id", 333).append("id100", 333 / 100),
                    new CountOptions().hint(new Document("id100", 1))));
    assertEquals(new Violations(false, true, 0, 0), exception.getViolations());
  }

  @Test
  void testCountDocumentsFilterHint() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.countDocuments(
                    new Document("id", 333).append("id100", 333 / 100),
                    new CountOptions().hint(new Document("id100", 1))));
    assertEquals(new Violations(false, true, 0, 0), exception.getViolations());
  }

  @Test
  @SuppressWarnings("deprecation")
  void testSessionCountFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.count(
                    mongoClient.startSession(), new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  void testSessionCountDocumentsFilter() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.countDocuments(
                    mongoClient.startSession(), new Document(new Document("foo", "bar"))));
    assertEquals(new Violations(false, false, 1, 0), exception.getViolations());
  }

  @Test
  @SuppressWarnings("deprecation")
  void testSessionCountFilterHint() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.count(
                    mongoClient.startSession(),
                    new Document("id", 333).append("id100", 333 / 100),
                    new CountOptions().hint(new Document("id100", 1))));
    assertEquals(new Violations(false, true, 0, 0), exception.getViolations());
  }

  @Test
  void testSessionCountDocumentsFilterHint() {
    BadPlanException exception =
        assertThrows(
            BadPlanException.class,
            () ->
                collection.countDocuments(
                    mongoClient.startSession(),
                    new Document("id", 333).append("id100", 333 / 100),
                    new CountOptions().hint(new Document("id100", 1))));
    assertEquals(new Violations(false, true, 0, 0), exception.getViolations());
  }
}
