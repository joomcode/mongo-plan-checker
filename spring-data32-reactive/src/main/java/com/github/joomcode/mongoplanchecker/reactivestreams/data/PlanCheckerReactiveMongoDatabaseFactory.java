package com.github.joomcode.mongoplanchecker.reactivestreams.data;

import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.reactivestreams.PlanCheckerMongoDatabase;
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
  public Mono<MongoDatabase> getMongoDatabase() throws DataAccessException {
    return f.getMongoDatabase().map(d -> new PlanCheckerMongoDatabase(d, checker));
  }

  @Override
  public Mono<MongoDatabase> getMongoDatabase(String dbName) throws DataAccessException {
    return f.getMongoDatabase(dbName).map(d -> new PlanCheckerMongoDatabase(d, checker));
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
