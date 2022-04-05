package com.joom.mongoplanchecker.sync.data;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.sync.PlanCheckerMongoDatabase;
import com.mongodb.ClientSessionOptions;
import com.mongodb.DB;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;

public class PlanCheckerMongoDbFactory implements MongoDbFactory {
  private final MongoDbFactory f;
  private final PlanChecker checker;

  public PlanCheckerMongoDbFactory(MongoDbFactory f, PlanChecker checker) {
    this.f = f;
    this.checker = checker;
  }

  @Override
  public MongoDatabase getDb() throws DataAccessException {
    return new PlanCheckerMongoDatabase(f.getDb(), checker);
  }

  @Override
  public MongoDatabase getDb(String dbName) throws DataAccessException {
    return new PlanCheckerMongoDatabase(f.getDb(dbName), checker);
  }

  @Override
  public PersistenceExceptionTranslator getExceptionTranslator() {
    return f.getExceptionTranslator();
  }

  @Override
  @SuppressWarnings("deprecation")
  public DB getLegacyDb() {
    return f.getLegacyDb();
  }

  @Override
  public ClientSession getSession(ClientSessionOptions options) {
    return f.getSession(options);
  }

  @Override
  public MongoDbFactory withSession(ClientSession session) {
    return new PlanCheckerMongoDbFactory(f.withSession(session), checker);
  }
}
