package com.joom.mongoplanchecker.sync.data;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.sync.PlanCheckerMongoDatabase;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;

public class PlanCheckerMongoDatabaseFactory implements MongoDatabaseFactory {
  private final MongoDatabaseFactory f;
  private final PlanChecker checker;

  public PlanCheckerMongoDatabaseFactory(MongoDatabaseFactory f, PlanChecker checker) {
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
  public ClientSession getSession(ClientSessionOptions options) {
    return f.getSession(options);
  }

  @Override
  public MongoDatabaseFactory withSession(ClientSession session) {
    return new PlanCheckerMongoDatabaseFactory(f.withSession(session), checker);
  }
}
