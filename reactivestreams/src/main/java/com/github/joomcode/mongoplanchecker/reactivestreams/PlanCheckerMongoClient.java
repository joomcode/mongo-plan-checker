package com.github.joomcode.mongoplanchecker.reactivestreams;

import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.mongodb.ClientSessionOptions;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.reactivestreams.client.*;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

public class PlanCheckerMongoClient implements MongoClient {

  private final MongoClient client;
  private final PlanChecker checker;

  public PlanCheckerMongoClient(MongoClient client, PlanChecker checker) {
    this.client = client;
    this.checker = checker;
  }

  @Override
  public PlanCheckerMongoDatabase getDatabase(String s) {
    return new PlanCheckerMongoDatabase(client.getDatabase(s), checker);
  }

  @Override
  public void close() {
    client.close();
  }

  @Override
  @SuppressWarnings("deprecation")
  public MongoClientSettings getSettings() {
    return client.getSettings();
  }

  @Override
  public Publisher<String> listDatabaseNames() {
    return client.listDatabaseNames();
  }

  @Override
  public Publisher<String> listDatabaseNames(ClientSession clientSession) {
    return client.listDatabaseNames(clientSession);
  }

  @Override
  public ListDatabasesPublisher<Document> listDatabases() {
    return client.listDatabases();
  }

  @Override
  public <TResult> ListDatabasesPublisher<TResult> listDatabases(Class<TResult> aClass) {
    return client.listDatabases(aClass);
  }

  @Override
  public ListDatabasesPublisher<Document> listDatabases(ClientSession clientSession) {
    return client.listDatabases(clientSession);
  }

  @Override
  public <TResult> ListDatabasesPublisher<TResult> listDatabases(
      ClientSession clientSession, Class<TResult> aClass) {
    return client.listDatabases(clientSession, aClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch() {
    return client.watch();
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(Class<TResult> aClass) {
    return client.watch(aClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(List<? extends Bson> list) {
    return client.watch(list);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      List<? extends Bson> list, Class<TResult> aClass) {
    return client.watch(list, aClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(ClientSession clientSession) {
    return client.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, Class<TResult> aClass) {
    return client.watch(clientSession, aClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(
      ClientSession clientSession, List<? extends Bson> list) {
    return client.watch(clientSession, list);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, List<? extends Bson> list, Class<TResult> aClass) {
    return client.watch(clientSession, list, aClass);
  }

  @Override
  public Publisher<ClientSession> startSession() {
    return client.startSession();
  }

  @Override
  public Publisher<ClientSession> startSession(ClientSessionOptions clientSessionOptions) {
    return client.startSession(clientSessionOptions);
  }
}
