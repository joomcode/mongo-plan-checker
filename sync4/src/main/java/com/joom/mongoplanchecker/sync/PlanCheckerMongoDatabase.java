package com.joom.mongoplanchecker.sync;

import com.joom.mongoplanchecker.core.PlanChecker;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import java.util.List;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class PlanCheckerMongoDatabase implements MongoDatabase {
  private final MongoDatabase database;
  private final PlanChecker checker;

  public PlanCheckerMongoDatabase(MongoDatabase database, PlanChecker checker) {
    this.database = database;
    this.checker = checker;
  }

  @Override
  public String getName() {
    return database.getName();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return database.getCodecRegistry();
  }

  @Override
  public ReadPreference getReadPreference() {
    return database.getReadPreference();
  }

  @Override
  public WriteConcern getWriteConcern() {
    return database.getWriteConcern();
  }

  @Override
  public ReadConcern getReadConcern() {
    return database.getReadConcern();
  }

  @Override
  public PlanCheckerMongoDatabase withCodecRegistry(CodecRegistry codecRegistry) {
    return new PlanCheckerMongoDatabase(database.withCodecRegistry(codecRegistry), checker);
  }

  @Override
  public PlanCheckerMongoDatabase withReadPreference(ReadPreference readPreference) {
    return new PlanCheckerMongoDatabase(database.withReadPreference(readPreference), checker);
  }

  @Override
  public PlanCheckerMongoDatabase withWriteConcern(WriteConcern writeConcern) {
    return new PlanCheckerMongoDatabase(database.withWriteConcern(writeConcern), checker);
  }

  @Override
  public PlanCheckerMongoDatabase withReadConcern(ReadConcern readConcern) {
    return new PlanCheckerMongoDatabase(database.withReadConcern(readConcern), checker);
  }

  @Override
  public PlanCheckerMongoCollection<Document> getCollection(String collectionName) {
    return new PlanCheckerMongoCollection<>(database.getCollection(collectionName), checker);
  }

  @Override
  public <TDocument> PlanCheckerMongoCollection<TDocument> getCollection(
      String collectionName, Class<TDocument> tDocumentClass) {
    return new PlanCheckerMongoCollection<>(
        database.getCollection(collectionName, tDocumentClass), checker);
  }

  @Override
  public Document runCommand(Bson command) {
    return database.runCommand(command);
  }

  @Override
  public Document runCommand(Bson command, ReadPreference readPreference) {
    return database.runCommand(command, readPreference);
  }

  @Override
  public <TResult> TResult runCommand(Bson command, Class<TResult> tResultClass) {
    return database.runCommand(command, tResultClass);
  }

  @Override
  public <TResult> TResult runCommand(
      Bson command, ReadPreference readPreference, Class<TResult> tResultClass) {
    return database.runCommand(command, readPreference, tResultClass);
  }

  @Override
  public Document runCommand(ClientSession clientSession, Bson command) {
    return database.runCommand(clientSession, command);
  }

  @Override
  public Document runCommand(
      ClientSession clientSession, Bson command, ReadPreference readPreference) {
    return database.runCommand(clientSession, command, readPreference);
  }

  @Override
  public <TResult> TResult runCommand(
      ClientSession clientSession, Bson command, Class<TResult> tResultClass) {
    return database.runCommand(clientSession, command, tResultClass);
  }

  @Override
  public <TResult> TResult runCommand(
      ClientSession clientSession,
      Bson command,
      ReadPreference readPreference,
      Class<TResult> tResultClass) {
    return database.runCommand(clientSession, command, readPreference, tResultClass);
  }

  @Override
  public void drop() {
    database.drop();
  }

  @Override
  public void drop(ClientSession clientSession) {
    database.drop(clientSession);
  }

  @Override
  public MongoIterable<String> listCollectionNames() {
    return database.listCollectionNames();
  }

  @Override
  public ListCollectionsIterable<Document> listCollections() {
    return database.listCollections();
  }

  @Override
  public <TResult> ListCollectionsIterable<TResult> listCollections(Class<TResult> tResultClass) {
    return database.listCollections(tResultClass);
  }

  @Override
  public MongoIterable<String> listCollectionNames(ClientSession clientSession) {
    return database.listCollectionNames(clientSession);
  }

  @Override
  public ListCollectionsIterable<Document> listCollections(ClientSession clientSession) {
    return database.listCollections(clientSession);
  }

  @Override
  public <TResult> ListCollectionsIterable<TResult> listCollections(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return database.listCollections(clientSession, tResultClass);
  }

  @Override
  public void createCollection(String collectionName) {
    database.createCollection(collectionName);
  }

  @Override
  public void createCollection(
      String collectionName, CreateCollectionOptions createCollectionOptions) {
    database.createCollection(collectionName, createCollectionOptions);
  }

  @Override
  public void createCollection(ClientSession clientSession, String collectionName) {
    database.createCollection(clientSession, collectionName);
  }

  @Override
  public void createCollection(
      ClientSession clientSession,
      String collectionName,
      CreateCollectionOptions createCollectionOptions) {
    database.createCollection(clientSession, collectionName, createCollectionOptions);
  }

  @Override
  public void createView(String viewName, String viewOn, List<? extends Bson> pipeline) {
    database.createView(viewName, viewOn, pipeline);
  }

  @Override
  public void createView(
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions) {
    database.createView(viewName, viewOn, pipeline, createViewOptions);
  }

  @Override
  public void createView(
      ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline) {
    database.createView(clientSession, viewName, viewOn, pipeline);
  }

  @Override
  public void createView(
      ClientSession clientSession,
      String viewName,
      String viewOn,
      List<? extends Bson> pipeline,
      CreateViewOptions createViewOptions) {
    database.createView(clientSession, viewName, viewOn, pipeline, createViewOptions);
  }

  @Override
  public ChangeStreamIterable<Document> watch() {
    return database.watch();
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
    return database.watch(tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(List<? extends Bson> pipeline) {
    return database.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return database.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(ClientSession clientSession) {
    return database.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return database.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<Document> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return database.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return database.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
    return database.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return database.aggregate(pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<Document> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return database.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return database.aggregate(clientSession, pipeline, tResultClass);
  }
}
