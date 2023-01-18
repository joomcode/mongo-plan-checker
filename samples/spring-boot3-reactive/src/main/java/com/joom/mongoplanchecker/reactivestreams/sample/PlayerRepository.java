package com.joom.mongoplanchecker.reactivestreams.sample;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PlayerRepository extends ReactiveMongoRepository<GameOfThronesPlayer, ObjectId> {
  Mono<Long> countByName(String name);

  Mono<Long> countByHouse(String house);
}
