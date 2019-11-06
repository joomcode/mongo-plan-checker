package com.github.joomcode.mongoplanchecker.async;

import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import java.util.List;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

@SuppressWarnings("deprecation")
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
  public MongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
    return new PlanCheckerMongoDatabase(db.withCodecRegistry(codecRegistry), checker);
  }

  @Override
  public MongoDatabase withReadPreference(ReadPreference readPreference) {
    return new PlanCheckerMongoDatabase(db.withReadPreference(readPreference), checker);
  }

  @Override
  public MongoDatabase withWriteConcern(WriteConcern writeConcern) {
    return new PlanCheckerMongoDatabase(db.withWriteConcern(writeConcern), checker);
  }

  @Override
  public MongoDatabase withReadConcern(ReadConcern readConcern) {
    return new PlanCheckerMongoDatabase(db.withReadConcern(readConcern), checker);
  }

  @Override
  public PlanCheckerMongoCollection<Document> getCollection(String collectionName) {
    return new PlanCheckerMongoCollection<>(db.getCollection(collectionName), checker);
  }

  @Override
  public <TDocument> MongoCollection<TDocument> getCollection(
      String collectionName, Class<TDocument> tDocumentClass) {
    return new PlanCheckerMongoCollection<>(
        db.getCollection(collectionName, tDocumentClass), checker);
  }

  @Override
  public void runCommand(Bson command, SingleResultCallback<Document> callback) {
    db.runCommand(command, callback);
  }

  @Override
  public void runCommand(
      Bson command, ReadPreference readPreference, SingleResultCallback<Document> callback) {
    db.runCommand(command, readPreference, callback);
  }

  @Override
  public <TResult> void runCommand(
      Bson command, Class<TResult> tResultClass, SingleResultCallback<TResult> callback) {
    db.runCommand(command, tResultClass, callback);
  }

  @Override
  public <TResult> void runCommand(
      Bson command,
      ReadPreference readPreference,
      Class<TResult> tResultClass,
      SingleResultCallback<TResult> callback) {
    db.runCommand(command, readPreference, tResultClass, callback);
  }

  @Override
  public void runCommand(
      ClientSession clientSession, Bson command, SingleResultCallback<Document> callback) {
    db.runCommand(clientSession, command, callback);
  }

  @Override
  public void runCommand(
      ClientSession clientSession,
      Bson command,
      ReadPreference readPreference,
      SingleResultCallback<Document> callback) {
    db.runCommand(clientSession, command, readPreference, callback);
  }

  @Override
  public <TResult> void runCommand(
      ClientSession clientSession,
      Bson command,
      Class<TResult> tResultClass,
      SingleResultCallback<TResult> callback) {
    db.runCommand(clientSession, command, tResultClass, callback);
  }

  @Override
  public <TResult> void runCommand(
      ClientSession clientSession,
      Bson command,
      ReadPreference readPreference,
      Class<TResult> tResultClass,
      SingleResultCallback<TResult> callback) {
    db.runCommand(clientSession, command, readPreference, tResultClass, callback);
  }

  @Override
  public void drop(SingleResultCallback<Void> callback) {
    db.drop(callback);
  }

  @Override
  public void drop(ClientSession clientSession, SingleResultCallback<Void> callback) {
    db.drop(clientSession, callback);
  }

  @Override
  public MongoIterable<String> listCollectionNames() {
    return db.listCollectionNames();
  }

  @Override
  public MongoIterable<String> listCollectionNames(ClientSession clientSession) {
    return db.listCollectionNames(clientSession);
  }

  @Override
  public ListCollectionsIterable<Document> listCollections() {
    return db.listCollections();
  }

  @Override
  public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> tResultClass) {
    return db.listCollections(tResultClass);
  }

  @Override
  public ListCollectionsIterable<Document> listCollections(ClientSession clientSession) {
    return db.listCollections(clientSession);
  }

  @Override
  public <TResult> ListCollectionsIterable<TResult> listCollections(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return db.listCollections(clientSession, tResultClass);
  }

  @Override
  public void createCollection(String collectionName, SingleResultCallback<Void> callback) {
    db.createCollection(collectionName, callback);
  }

  @Override
  public void createCollection(
      String collectionName, CreateCollectionOptions options, SingleResultCallback<Void> callback) {
    db.createCollection(collectionName, options, callback);
  }

  @Override
  public void createCollection(
      ClientSession clientSession, String collectionName, SingleResultCallback<Void> callback) {
    db.createCollection(clientSession, collectionName, callback);
  }

  @Override
  public void createCollection(
      ClientSession clientSession,
      String collectionName,
      CreateCollectionOptions options,
      SingleResultCallback<Void> callback) {
    db.createCollection(clientSession, collectionName, options, callback);
  }

  @Override
  public void createView(
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      SingleResultCallback<Void> callback) {
    db.createView(viewName, viewOn, pipeline, callback);
  }

  @Override
  public void createView(
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions,
      SingleResultCallback<Void> callback) {
    db.createView(viewName, viewOn, pipeline, createViewOptions, callback);
  }

  @Override
  public void createView(
      ClientSession clientSession,
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      SingleResultCallback<Void> callback) {
    db.createView(clientSession, viewName, viewOn, pipeline, callback);
  }

  @Override
  public void createView(
      ClientSession clientSession,
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions,
      SingleResultCallback<Void> callback) {
    db.createView(clientSession, viewName, viewOn, pipeline, createViewOptions, callback);
  }

  @Override
  public ChangeStreamIterable<Document> watch() {
    return db.watch();
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
    return db.watch(tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
    return db.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
    return db.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return db.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return db.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
    return db.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.aggregate(pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<Document> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return db.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return db.aggregate(clientSession, pipeline, tResultClass);
  }
}
