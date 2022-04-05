package com.joom.mongoplanchecker.reactivestreams.data;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.reactivestreams.PlanCheckerMongoDatabase;
import com.mongodb.ClientSessionOptions;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;

public class PlanCheckerReactiveMongoDatabaseFactory implements ReactiveMongoDatabaseFactory {
  private final ReactiveMongoDatabaseFactory f;
  private final PlanChecker checker;

  public PlanCheckerReactiveMongoDatabaseFactory(
      ReactiveMongoDatabaseFactory f, PlanChecker checker) {
    this.f = f;
    this.checker = checker;
  }

  @Override
  public MongoDatabase getMongoDatabase() throws DataAccessException {
    return new PlanCheckerMongoDatabase(f.getMongoDatabase(), checker);
  }

  @Override
  public MongoDatabase getMongoDatabase(String dbName) throws DataAccessException {
    return new PlanCheckerMongoDatabase(f.getMongoDatabase(dbName), checker);
  }

  @Override
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return f.getExceptionTranslator();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return f.getCodecRegistry();
  }

  @Override
  public Mono<ClientSession> getSession(ClientSessionOptions options) {
    return f.getSession(options);
  }

  @Override
  public PlanCheckerReactiveMongoDatabaseFactory withSession(ClientSession session) {
    return new PlanCheckerReactiveMongoDatabaseFactory(f.withSession(session), checker);
  }
}
