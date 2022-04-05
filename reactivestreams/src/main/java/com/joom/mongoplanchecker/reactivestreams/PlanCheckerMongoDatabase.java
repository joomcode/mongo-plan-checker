package com.joom.mongoplanchecker.reactivestreams;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import com.mongodb.reactivestreams.client.*;
import java.util.List;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

public class PlanCheckerMongoDatabase implements MongoDatabase {
  private final MongoDatabase db;
  private final PlanChecker checker;

  public PlanCheckerMongoDatabase(MongoDatabase db, PlanChecker checker) {
    this.db = db;
    this.checker = checker;
  }

  @Override
  public String getName() {
    return db.getName();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return db.getCodecRegistry();
  }

  @Override
  public ReadPreference getReadPreference() {
    return db.getReadPreference();
  }

  @Override
  public WriteConcern getWriteConcern() {
    return db.getWriteConcern();
  }

  @Override
  public ReadConcern getReadConcern() {
    return db.getReadConcern();
  }

  @Override
  public PlanCheckerMongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
    db.withCodecRegistry(codecRegistry);
    return this;
  }

  @Override
  public PlanCheckerMongoDatabase withReadPreference(ReadPreference readPreference) {
    db.withReadPreference(readPreference);
    return this;
  }

  @Override
  public PlanCheckerMongoDatabase withWriteConcern(WriteConcern writeConcern) {
    db.withWriteConcern(writeConcern);
    return this;
  }

  @Override
  public PlanCheckerMongoDatabase withReadConcern(ReadConcern readConcern) {
    db.withReadConcern(readConcern);
    return this;
  }

  @Override
  public PlanCheckerMongoCollection<Document> getCollection(String collectionName) {
    return new PlanCheckerMongoCollection<>(db.getCollection(collectionName), checker);
  }

  @Override
  public <TDocument> MongoCollection<TDocument> getCollection(
      String collectionName, Class<TDocument> clazz) {
    return new PlanCheckerMongoCollection<>(db.getCollection(collectionName, clazz), checker);
  }

  @Override
  public Publisher<Document> runCommand(Bson command) {
    return db.runCommand(command);
  }

  @Override
  public Publisher<Document> runCommand(Bson command, ReadPreference readPreference) {
    return db.runCommand(command, readPreference);
  }

  @Override
  public <TResult> Publisher<TResult> runCommand(Bson command, Class<TResult> clazz) {
    return db.runCommand(command, clazz);
  }

  @Override
  public <TResult> Publisher<TResult> runCommand(
      Bson command, ReadPreference readPreference, Class<TResult> clazz) {
    return db.runCommand(command, readPreference, clazz);
  }

  @Override
  public Publisher<Document> runCommand(ClientSession clientSession, Bson command) {
    return db.runCommand(clientSession, command);
  }

  @Override
  public Publisher<Document> runCommand(
      ClientSession clientSession, Bson command, ReadPreference readPreference) {
    return db.runCommand(clientSession, command, readPreference);
  }

  @Override
  public <TResult> Publisher<TResult> runCommand(
      ClientSession clientSession, Bson command, Class<TResult> clazz) {
    return db.runCommand(clientSession, command, clazz);
  }

  @Override
  public <TResult> Publisher<TResult> runCommand(
      ClientSession clientSession,
      Bson command,
      ReadPreference readPreference,
      Class<TResult> clazz) {
    return db.runCommand(clientSession, command, readPreference, clazz);
  }

  @Override
  public Publisher<Success> drop() {
    return db.drop();
  }

  @Override
  public Publisher<Success> drop(ClientSession clientSession) {
    return db.drop(clientSession);
  }

  @Override
  public Publisher<String> listCollectionNames() {
    return db.listCollectionNames();
  }

  @Override
  public Publisher<String> listCollectionNames(ClientSession clientSession) {
    return db.listCollectionNames(clientSession);
  }

  @Override
  public ListCollectionsPublisher<Document> listCollections() {
    return db.listCollections();
  }

  @Override
  public <TResult> ListCollectionsPublisher<TResult> listCollections(Class<TResult> clazz) {
    return db.listCollections(clazz);
  }

  @Override
  public ListCollectionsPublisher<Document> listCollections(ClientSession clientSession) {
    return db.listCollections(clientSession);
  }

  @Override
  public <TResult> ListCollectionsPublisher<TResult> listCollections(
      ClientSession clientSession, Class<TResult> clazz) {
    return db.listCollections(clientSession, clazz);
  }

  @Override
  public Publisher<Success> createCollection(String collectionName) {
    return db.createCollection(collectionName);
  }

  @Override
  public Publisher<Success> createCollection(
      String collectionName, CreateCollectionOptions options) {
    return db.createCollection(collectionName, options);
  }

  @Override
  public Publisher<Success> createCollection(ClientSession clientSession, String collectionName) {
    return db.createCollection(clientSession, collectionName);
  }

  @Override
  public Publisher<Success> createCollection(
      ClientSession clientSession, String collectionName, CreateCollectionOptions options) {
    return db.createCollection(clientSession, collectionName, options);
  }

  @Override
  public Publisher<Success> createView(
      String viewName, String viewOn, List<? extends Bson> pipeline) {
    return db.createView(viewName, viewOn, pipeline);
  }

  @Override
  public Publisher<Success> createView(
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions) {
    return db.createView(viewName, viewOn, pipeline, createViewOptions);
  }

  @Override
  public Publisher<Success> createView(
      ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline) {
    return db.createView(clientSession, viewName, viewOn, pipeline);
  }

  @Override
  public Publisher<Success> createView(
      ClientSession clientSession,
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions) {
    return db.createView(clientSession, viewName, viewOn, pipeline, createViewOptions);
  }

  @Override
  public ChangeStreamPublisher<Document> watch() {
    return db.watch();
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(Class<TResult> tResultClass) {
    return db.watch(tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(List<? extends Bson> pipeline) {
    return db.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(ClientSession clientSession) {
    return db.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return db.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return db.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public AggregatePublisher<Document> aggregate(List<? extends Bson> pipeline) {
    return db.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregatePublisher<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.aggregate(pipeline, tResultClass);
  }

  @Override
  public AggregatePublisher<Document> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return db.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregatePublisher<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.aggregate(clientSession, pipeline, tResultClass);
  }
}
