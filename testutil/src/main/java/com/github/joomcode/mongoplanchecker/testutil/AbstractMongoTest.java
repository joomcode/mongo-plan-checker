package com.github.joomcode.mongoplanchecker.testutil;

import static java.util.Collections.singletonList;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoNotPrimaryException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

public class AbstractMongoTest {
  protected static final int MONGO_PORT = 27017;

  @Container
  protected static final GenericContainer MONGO =
      new GenericContainer("mongo:4.2").withExposedPorts(MONGO_PORT).withCommand("--replSet rs0");

  @BeforeAll
  static void setupMongoContainer() throws IOException, InterruptedException {
    MONGO.execInContainer("/bin/bash", "-c", "mongo --eval 'rs.initiate()'");
    waitTillMongoAcceptWrites();
  }

  private static final long WAIT_TIMEOUT = TimeUnit.SECONDS.toNanos(10);

  private static void waitTillMongoAcceptWrites() throws InterruptedException {
    long start = System.nanoTime();
    try (MongoClient client =
        MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings(
                    builder ->
                        builder.hosts(
                            singletonList(
                                new ServerAddress(
                                    MONGO.getContainerIpAddress(),
                                    MONGO.getMappedPort(MONGO_PORT)))))
                .build())) {
      MongoCollection<Document> collection =
          client.getDatabase("test").getCollection("connectionTest");
      while (true) {
        try {
          collection.insertOne(new Document("foo", "bar"));
        } catch (MongoNotPrimaryException e) {
          if (System.nanoTime() - start > WAIT_TIMEOUT) {
            throw new RuntimeException("Mongo do not want to become primary", e);
          }
          Thread.sleep(10);
          continue;
        }
        break;
      }
    }
  }
}
