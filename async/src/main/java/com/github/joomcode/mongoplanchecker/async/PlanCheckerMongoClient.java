package com.github.joomcode.mongoplanchecker.async;

import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.mongodb.ClientSessionOptions;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

@SuppressWarnings("deprecation")
public class PlanCheckerMongoClient implements MongoClient {

  private final MongoClient client;
  private final PlanChecker checker;

  public PlanCheckerMongoClient(MongoClient client, PlanChecker checker) {
    this.client = client;
    this.checker = checker;
  }

  @Override
  public void startSession(SingleResultCallback<ClientSession> callback) {
    client.startSession(callback);
  }

  @Override
  public void startSession(
      ClientSessionOptions options, SingleResultCallback<ClientSession> callback) {
    client.startSession(options, callback);
  }

  @Override
  public PlanCheckerMongoDatabase getDatabase(String name) {
    return new PlanCheckerMongoDatabase(client.getDatabase(name), checker);
  }

  @Override
  public void close() {
    client.close();
  }

  @Override
  public MongoClientSettings getSettings() {
    return client.getSettings();
  }

  @Override
  public MongoIterable<String> listDatabaseNames() {
    return client.listDatabaseNames();
  }

  @Override
  public MongoIterable<String> listDatabaseNames(ClientSession clientSession) {
    return client.listDatabaseNames(clientSession);
  }

  @Override
  public ListDatabasesIterable<Document> listDatabases() {
    return client.listDatabases();
  }

  @Override
  public ListDatabasesIterable<Document> listDatabases(ClientSession clientSession) {
    return client.listDatabases(clientSession);
  }

  @Override
  public <TResult> ListDatabasesIterable<TResult> listDatabases(Class<TResult> tResultClass) {
    return client.listDatabases(tResultClass);
  }

  @Override
  public <TResult> ListDatabasesIterable<TResult> listDatabases(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return client.listDatabases(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch() {
    return client.watch();
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
    return client.watch(tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
    return client.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return client.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
    return client.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return client.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return client.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return client.watch(clientSession, pipeline, tResultClass);
  }
}
