package com.joom.mongoplanchecker.sync.sample;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<GameOfThronesPlayer, ObjectId> {
  Long countByName(String name);

  Long countByHouse(String house);
}
