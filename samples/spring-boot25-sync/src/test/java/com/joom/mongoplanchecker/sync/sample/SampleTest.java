package com.joom.mongoplanchecker.sync.sample;

import static org.junit.jupiter.api.Assertions.*;

import com.joom.mongoplanchecker.core.BadPlanException;
import com.joom.mongoplanchecker.core.PlanChecker;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
class SampleTest {
  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @BeforeAll
  static void startMongo() {
    mongoDBContainer.start();
  }

  @Autowired private PlayerRepository repository;
  @Autowired private PlanChecker checker;

  @BeforeEach
  void setup() {
    repository.insert(
        IntStream.range(0, 10)
            .mapToObj(
                house ->
                    IntStream.range(0, 3)
                        .mapToObj(name -> new GameOfThronesPlayer("House" + house, "Name" + name)))
            .flatMap(Function.identity())
            .collect(Collectors.toList()));
  }

  @Test
  void testCountByName() {
    assertThrows(BadPlanException.class, () -> repository.countByName("Name2"));
  }

  @Test
  void testCountByNameWithIgnore() {
    checker.ignoreCollscan();
    assertEquals(10, repository.countByName("Name2"));
  }

  @Test
  void testCountByHouse() {
    assertEquals(3, repository.countByHouse("House7"));
  }

  @AfterEach
  void testDown() {
    assertFalse(checker.anyIgnores());
    // Have you ever run your tests against production DB?
    repository.deleteAll();
  }
}
