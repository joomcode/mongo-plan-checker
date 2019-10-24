package com.github.joomcode.mongoplanchecker.reactivestreams;

import static com.github.joomcode.mongoplanchecker.core.PlanChecker.explainModifier;

import com.github.joomcode.mongoplanchecker.core.Nullable;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.*;
import java.util.List;
import java.util.function.Supplier;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

public class PlanCheckerMongoCollection<TDocument> implements MongoCollection<TDocument> {
  private final MongoCollection<TDocument> c;
  private final PlanChecker checker;

  public PlanCheckerMongoCollection(MongoCollection<TDocument> c, PlanChecker checker) {
    this.c = c;
    this.checker = checker;
  }

  @Override
  public MongoNamespace getNamespace() {
    return c.getNamespace();
  }

  @Override
  public Class<TDocument> getDocumentClass() {
    return c.getDocumentClass();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return c.getCodecRegistry();
  }

  @Override
  public ReadPreference getReadPreference() {
    return c.getReadPreference();
  }

  @Override
  public WriteConcern getWriteConcern() {
    return c.getWriteConcern();
  }

  @Override
  public ReadConcern getReadConcern() {
    return c.getReadConcern();
  }

  @Override
  public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> clazz) {
    return new PlanCheckerMongoCollection<>(c.withDocumentClass(clazz), checker);
  }

  @Override
  public MongoCollection<TDocument> withCodecRegistry(CodecRegistry codecRegistry) {
    return new PlanCheckerMongoCollection<>(c.withCodecRegistry(codecRegistry), checker);
  }

  @Override
  public MongoCollection<TDocument> withReadPreference(ReadPreference readPreference) {
    return new PlanCheckerMongoCollection<>(c.withReadPreference(readPreference), checker);
  }

  @Override
  public MongoCollection<TDocument> withWriteConcern(WriteConcern writeConcern) {
    return new PlanCheckerMongoCollection<>(c.withWriteConcern(writeConcern), checker);
  }

  @Override
  public MongoCollection<TDocument> withReadConcern(ReadConcern readConcern) {
    return new PlanCheckerMongoCollection<>(c.withReadConcern(readConcern), checker);
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count() {
    return c.count();
  }

  private <T> Publisher<T> check(Bson filter, Supplier<Publisher<T>> realPublisher) {
    return check(filter, null, null, realPublisher);
  }

  private <T> Publisher<T> check(
      Bson filter, Collation collation, Supplier<Publisher<T>> realPublisher) {
    return check(filter, null, collation, realPublisher);
  }

  @SuppressWarnings("deprecation")
  private <T> Publisher<T> check(
      Bson filter,
      @Nullable Bson hint,
      @Nullable Collation collation,
      Supplier<Publisher<T>> realPublisher) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      return realPublisher.get();
    }

    return mainSubscriber ->
        c.find(filter)
            .hint(hint)
            .collation(collation)
            .modifiers(explainModifier())
            .first()
            .subscribe(
                new PlanCheckingSubscriber<>(
                    mainSubscriber, checker, () -> realPublisher.get().subscribe(mainSubscriber)));
  }

  private <T> Publisher<T> check(
      ClientSession clientSession, Bson filter, Supplier<Publisher<T>> realPublisher) {
    return check(clientSession, filter, null, null, realPublisher);
  }

  private <T> Publisher<T> check(
      ClientSession clientSession,
      Bson filter,
      Collation collation,
      Supplier<Publisher<T>> realPublisher) {
    return check(clientSession, filter, null, collation, realPublisher);
  }

  @SuppressWarnings("deprecation")
  private <T> Publisher<T> check(
      ClientSession clientSession,
      Bson filter,
      @Nullable Bson hint,
      @Nullable Collation collation,
      Supplier<Publisher<T>> realPublisher) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      return realPublisher.get();
    }

    return mainSubscriber ->
        c.find(clientSession, filter)
            .hint(hint)
            .collation(collation)
            .modifiers(explainModifier())
            .first()
            .subscribe(
                new PlanCheckingSubscriber<>(
                    mainSubscriber, checker, () -> realPublisher.get().subscribe(mainSubscriber)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count(Bson filter) {
    return check(filter, () -> c.count(filter));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count(Bson filter, CountOptions options) {
    return check(filter, options.getHint(), options.getCollation(), () -> c.count(filter, options));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count(ClientSession clientSession) {
    return c.count(clientSession);
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count(ClientSession clientSession, Bson filter) {
    return check(clientSession, filter, () -> c.count(clientSession, filter));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<Long> count(ClientSession clientSession, Bson filter, CountOptions options) {
    return check(
        clientSession,
        filter,
        options.getHint(),
        options.getCollation(),
        () -> c.count(clientSession, filter, options));
  }

  @Override
  public Publisher<Long> estimatedDocumentCount() {
    return c.estimatedDocumentCount();
  }

  @Override
  public Publisher<Long> estimatedDocumentCount(EstimatedDocumentCountOptions options) {
    return c.estimatedDocumentCount(options);
  }

  @Override
  public Publisher<Long> countDocuments() {
    return c.countDocuments();
  }

  @Override
  public Publisher<Long> countDocuments(Bson filter) {
    return check(filter, () -> c.countDocuments(filter));
  }

  @Override
  public Publisher<Long> countDocuments(Bson filter, CountOptions options) {
    return check(
        filter, options.getHint(), options.getCollation(), () -> c.countDocuments(filter, options));
  }

  @Override
  public Publisher<Long> countDocuments(ClientSession clientSession) {
    return c.countDocuments(clientSession);
  }

  @Override
  public Publisher<Long> countDocuments(ClientSession clientSession, Bson filter) {
    return check(clientSession, filter, () -> c.countDocuments(clientSession, filter));
  }

  @Override
  public Publisher<Long> countDocuments(
      ClientSession clientSession, Bson filter, CountOptions options) {
    return check(
        clientSession,
        filter,
        options.getHint(),
        options.getCollation(),
        () -> c.countDocuments(clientSession, filter, options));
  }

  @Override
  public <TResult> DistinctPublisher<TResult> distinct(
      String fieldName, Class<TResult> tResultClass) {
    return c.distinct(fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctPublisher<TResult> distinct(
      String fieldName, Bson filter, Class<TResult> tResultClass) {
    return c.distinct(fieldName, filter, tResultClass);
  }

  @Override
  public <TResult> DistinctPublisher<TResult> distinct(
      ClientSession clientSession, String fieldName, Class<TResult> tResultClass) {
    return c.distinct(clientSession, fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctPublisher<TResult> distinct(
      ClientSession clientSession, String fieldName, Bson filter, Class<TResult> tResultClass) {
    return c.distinct(clientSession, fieldName, filter, tResultClass);
  }

  @Override
  public FindPublisher<TDocument> find() {
    return new PlanCheckerFindPublisher<>(c.find(), checker);
  }

  @Override
  public <TResult> PlanCheckerFindPublisher<TResult> find(Class<TResult> clazz) {
    return new PlanCheckerFindPublisher<>(c.find(clazz), checker);
  }

  @Override
  public PlanCheckerFindPublisher<TDocument> find(Bson filter) {
    return new PlanCheckerFindPublisher<>(c.find(filter), checker);
  }

  @Override
  public <TResult> PlanCheckerFindPublisher<TResult> find(Bson filter, Class<TResult> clazz) {
    return new PlanCheckerFindPublisher<>(c.find(filter, clazz), checker);
  }

  @Override
  public PlanCheckerFindPublisher<TDocument> find(ClientSession clientSession) {
    return new PlanCheckerFindPublisher<>(c.find(clientSession), checker);
  }

  @Override
  public <TResult> PlanCheckerFindPublisher<TResult> find(
      ClientSession clientSession, Class<TResult> clazz) {
    return new PlanCheckerFindPublisher<>(c.find(clientSession, clazz), checker);
  }

  @Override
  public PlanCheckerFindPublisher<TDocument> find(ClientSession clientSession, Bson filter) {
    return new PlanCheckerFindPublisher<>(c.find(clientSession, filter), checker);
  }

  @Override
  public <TResult> PlanCheckerFindPublisher<TResult> find(
      ClientSession clientSession, Bson filter, Class<TResult> clazz) {
    return new PlanCheckerFindPublisher<>(c.find(clientSession, filter, clazz), checker);
  }

  @Override
  public AggregatePublisher<Document> aggregate(List<? extends Bson> pipeline) {
    return c.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregatePublisher<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> clazz) {
    return c.aggregate(pipeline, clazz);
  }

  @Override
  public AggregatePublisher<Document> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return c.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregatePublisher<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> clazz) {
    return c.aggregate(clientSession, pipeline, clazz);
  }

  @Override
  public ChangeStreamPublisher<Document> watch() {
    return c.watch();
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(Class<TResult> tResultClass) {
    return c.watch(tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(List<? extends Bson> pipeline) {
    return c.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(ClientSession clientSession) {
    return c.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return c.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamPublisher<Document> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return c.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamPublisher<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public MapReducePublisher<Document> mapReduce(String mapFunction, String reduceFunction) {
    return c.mapReduce(mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReducePublisher<TResult> mapReduce(
      String mapFunction, String reduceFunction, Class<TResult> clazz) {
    return c.mapReduce(mapFunction, reduceFunction, clazz);
  }

  @Override
  public MapReducePublisher<Document> mapReduce(
      ClientSession clientSession, String mapFunction, String reduceFunction) {
    return c.mapReduce(clientSession, mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReducePublisher<TResult> mapReduce(
      ClientSession clientSession,
      String mapFunction,
      String reduceFunction,
      Class<TResult> clazz) {
    return c.mapReduce(clientSession, mapFunction, reduceFunction, clazz);
  }

  @Override
  public Publisher<BulkWriteResult> bulkWrite(
      List<? extends WriteModel<? extends TDocument>> requests) {
    return c.bulkWrite(requests);
  }

  @Override
  public Publisher<BulkWriteResult> bulkWrite(
      List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
    return c.bulkWrite(requests, options);
  }

  @Override
  public Publisher<BulkWriteResult> bulkWrite(
      ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests) {
    return c.bulkWrite(clientSession, requests);
  }

  @Override
  public Publisher<BulkWriteResult> bulkWrite(
      ClientSession clientSession,
      List<? extends WriteModel<? extends TDocument>> requests,
      BulkWriteOptions options) {
    return c.bulkWrite(clientSession, requests, options);
  }

  @Override
  public Publisher<Success> insertOne(TDocument tDocument) {
    return c.insertOne(tDocument);
  }

  @Override
  public Publisher<Success> insertOne(TDocument tDocument, InsertOneOptions options) {
    return c.insertOne(tDocument, options);
  }

  @Override
  public Publisher<Success> insertOne(ClientSession clientSession, TDocument tDocument) {
    return c.insertOne(clientSession, tDocument);
  }

  @Override
  public Publisher<Success> insertOne(
      ClientSession clientSession, TDocument tDocument, InsertOneOptions options) {
    return c.insertOne(clientSession, tDocument, options);
  }

  @Override
  public Publisher<Success> insertMany(List<? extends TDocument> tDocuments) {
    return c.insertMany(tDocuments);
  }

  @Override
  public Publisher<Success> insertMany(
      List<? extends TDocument> tDocuments, InsertManyOptions options) {
    return c.insertMany(tDocuments, options);
  }

  @Override
  public Publisher<Success> insertMany(
      ClientSession clientSession, List<? extends TDocument> tDocuments) {
    return c.insertMany(clientSession, tDocuments);
  }

  @Override
  public Publisher<Success> insertMany(
      ClientSession clientSession,
      List<? extends TDocument> tDocuments,
      InsertManyOptions options) {
    return c.insertMany(clientSession, tDocuments, options);
  }

  @Override
  public Publisher<DeleteResult> deleteOne(Bson filter) {
    return check(filter, () -> c.deleteOne(filter));
  }

  @Override
  public Publisher<DeleteResult> deleteOne(Bson filter, DeleteOptions options) {
    return check(filter, options.getCollation(), () -> c.deleteOne(filter, options));
  }

  @Override
  public Publisher<DeleteResult> deleteOne(ClientSession clientSession, Bson filter) {
    return check(clientSession, filter, () -> c.deleteOne(clientSession, filter));
  }

  @Override
  public Publisher<DeleteResult> deleteOne(
      ClientSession clientSession, Bson filter, DeleteOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.deleteOne(clientSession, filter, options));
  }

  @Override
  public Publisher<DeleteResult> deleteMany(Bson filter) {
    return check(filter, () -> c.deleteMany(filter));
  }

  @Override
  public Publisher<DeleteResult> deleteMany(Bson filter, DeleteOptions options) {
    return check(filter, options.getCollation(), () -> c.deleteMany(filter, options));
  }

  @Override
  public Publisher<DeleteResult> deleteMany(ClientSession clientSession, Bson filter) {
    return check(clientSession, filter, () -> c.deleteMany(clientSession, filter));
  }

  @Override
  public Publisher<DeleteResult> deleteMany(
      ClientSession clientSession, Bson filter, DeleteOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.deleteMany(clientSession, filter, options));
  }

  @Override
  public Publisher<UpdateResult> replaceOne(Bson filter, TDocument replacement) {
    return check(filter, () -> c.replaceOne(filter, replacement));
  }

  @Override
  public Publisher<UpdateResult> replaceOne(
      Bson filter, TDocument replacement, ReplaceOptions options) {
    return check(filter, options.getCollation(), () -> c.replaceOne(filter, replacement, options));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<UpdateResult> replaceOne(
      Bson filter, TDocument replacement, UpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.replaceOne(filter, replacement, options));
  }

  @Override
  public Publisher<UpdateResult> replaceOne(
      ClientSession clientSession, Bson filter, TDocument replacement) {
    return check(clientSession, filter, () -> c.replaceOne(clientSession, filter, replacement));
  }

  @Override
  public Publisher<UpdateResult> replaceOne(
      ClientSession clientSession, Bson filter, TDocument replacement, ReplaceOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.replaceOne(clientSession, filter, replacement, options));
  }

  @Override
  @SuppressWarnings("deprecation")
  public Publisher<UpdateResult> replaceOne(
      ClientSession clientSession, Bson filter, TDocument replacement, UpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.replaceOne(clientSession, filter, replacement, options));
  }

  @Override
  public Publisher<UpdateResult> updateOne(Bson filter, Bson update) {
    return check(filter, () -> c.updateOne(filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateOne(Bson filter, Bson update, UpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.updateOne(filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateOne(ClientSession clientSession, Bson filter, Bson update) {
    return check(clientSession, filter, () -> c.updateOne(clientSession, filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateOne(Bson filter, List<? extends Bson> update) {
    return check(filter, () -> c.updateOne(filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateOne(
      Bson filter, List<? extends Bson> update, UpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.updateOne(filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateOne(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    return check(clientSession, filter, () -> c.updateOne(clientSession, filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateOne(
      ClientSession clientSession, Bson filter, Bson update, UpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.updateOne(clientSession, filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateOne(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.updateOne(clientSession, filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateMany(Bson filter, Bson update) {
    return check(filter, () -> c.updateMany(filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateMany(Bson filter, Bson update, UpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.updateMany(filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateMany(ClientSession clientSession, Bson filter, Bson update) {
    return check(clientSession, filter, () -> c.updateMany(clientSession, filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateMany(
      ClientSession clientSession, Bson filter, Bson update, UpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.updateMany(clientSession, filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateMany(Bson filter, List<? extends Bson> update) {
    return check(filter, () -> c.updateMany(filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateMany(
      Bson filter, List<? extends Bson> update, UpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.updateMany(filter, update, options));
  }

  @Override
  public Publisher<UpdateResult> updateMany(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    return check(clientSession, filter, () -> c.updateMany(clientSession, filter, update));
  }

  @Override
  public Publisher<UpdateResult> updateMany(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.updateMany(clientSession, filter, update, options));
  }

  @Override
  public Publisher<TDocument> findOneAndDelete(Bson filter) {
    return check(filter, () -> c.findOneAndDelete(filter));
  }

  @Override
  public Publisher<TDocument> findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
    return check(filter, options.getCollation(), () -> c.findOneAndDelete(filter, options));
  }

  @Override
  public Publisher<TDocument> findOneAndDelete(ClientSession clientSession, Bson filter) {
    return check(clientSession, filter, () -> c.findOneAndDelete(clientSession, filter));
  }

  @Override
  public Publisher<TDocument> findOneAndDelete(
      ClientSession clientSession, Bson filter, FindOneAndDeleteOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.findOneAndDelete(clientSession, filter, options));
  }

  @Override
  public Publisher<TDocument> findOneAndReplace(Bson filter, TDocument replacement) {
    return check(filter, () -> c.findOneAndReplace(filter, replacement));
  }

  @Override
  public Publisher<TDocument> findOneAndReplace(
      Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
    return check(
        filter, options.getCollation(), () -> c.findOneAndReplace(filter, replacement, options));
  }

  @Override
  public Publisher<TDocument> findOneAndReplace(
      ClientSession clientSession, Bson filter, TDocument replacement) {
    return check(
        clientSession, filter, () -> c.findOneAndReplace(clientSession, filter, replacement));
  }

  @Override
  public Publisher<TDocument> findOneAndReplace(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      FindOneAndReplaceOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.findOneAndReplace(clientSession, filter, replacement, options));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(Bson filter, Bson update) {
    return check(filter, () -> c.findOneAndUpdate(filter, update));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      Bson filter, Bson update, FindOneAndUpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.findOneAndUpdate(filter, update, options));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      ClientSession clientSession, Bson filter, Bson update) {
    return check(clientSession, filter, () -> c.findOneAndUpdate(clientSession, filter, update));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      ClientSession clientSession, Bson filter, Bson update, FindOneAndUpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.findOneAndUpdate(clientSession, filter, update, options));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(Bson filter, List<? extends Bson> update) {
    return check(filter, () -> c.findOneAndUpdate(filter, update));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      Bson filter, List<? extends Bson> update, FindOneAndUpdateOptions options) {
    return check(filter, options.getCollation(), () -> c.findOneAndUpdate(filter, update, options));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    return check(clientSession, filter, () -> c.findOneAndUpdate(clientSession, filter, update));
  }

  @Override
  public Publisher<TDocument> findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      FindOneAndUpdateOptions options) {
    return check(
        clientSession,
        filter,
        options.getCollation(),
        () -> c.findOneAndUpdate(clientSession, filter, update, options));
  }

  @Override
  public Publisher<Success> drop() {
    return c.drop();
  }

  @Override
  public Publisher<Success> drop(ClientSession clientSession) {
    return c.drop(clientSession);
  }

  @Override
  public Publisher<String> createIndex(Bson key) {
    return c.createIndex(key);
  }

  @Override
  public Publisher<String> createIndex(Bson key, IndexOptions options) {
    return c.createIndex(key, options);
  }

  @Override
  public Publisher<String> createIndex(ClientSession clientSession, Bson key) {
    return c.createIndex(clientSession, key);
  }

  @Override
  public Publisher<String> createIndex(
      ClientSession clientSession, Bson key, IndexOptions options) {
    return c.createIndex(clientSession, key, options);
  }

  @Override
  public Publisher<String> createIndexes(List<IndexModel> indexes) {
    return c.createIndexes(indexes);
  }

  @Override
  public Publisher<String> createIndexes(
      List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
    return c.createIndexes(indexes, createIndexOptions);
  }

  @Override
  public Publisher<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes) {
    return c.createIndexes(clientSession, indexes);
  }

  @Override
  public Publisher<String> createIndexes(
      ClientSession clientSession,
      List<IndexModel> indexes,
      CreateIndexOptions createIndexOptions) {
    return c.createIndexes(clientSession, indexes, createIndexOptions);
  }

  @Override
  public ListIndexesPublisher<Document> listIndexes() {
    return c.listIndexes();
  }

  @Override
  public <TResult> ListIndexesPublisher<TResult> listIndexes(Class<TResult> clazz) {
    return c.listIndexes(clazz);
  }

  @Override
  public ListIndexesPublisher<Document> listIndexes(ClientSession clientSession) {
    return c.listIndexes(clientSession);
  }

  @Override
  public <TResult> ListIndexesPublisher<TResult> listIndexes(
      ClientSession clientSession, Class<TResult> clazz) {
    return c.listIndexes(clientSession, clazz);
  }

  @Override
  public Publisher<Success> dropIndex(String indexName) {
    return c.dropIndex(indexName);
  }

  @Override
  public Publisher<Success> dropIndex(Bson keys) {
    return c.dropIndex(keys);
  }

  @Override
  public Publisher<Success> dropIndex(String indexName, DropIndexOptions dropIndexOptions) {
    return c.dropIndex(indexName, dropIndexOptions);
  }

  @Override
  public Publisher<Success> dropIndex(Bson keys, DropIndexOptions dropIndexOptions) {
    return c.dropIndex(keys, dropIndexOptions);
  }

  @Override
  public Publisher<Success> dropIndex(ClientSession clientSession, String indexName) {
    return c.dropIndex(clientSession, indexName);
  }

  @Override
  public Publisher<Success> dropIndex(ClientSession clientSession, Bson keys) {
    return c.dropIndex(clientSession, keys);
  }

  @Override
  public Publisher<Success> dropIndex(
      ClientSession clientSession, String indexName, DropIndexOptions dropIndexOptions) {
    return c.dropIndex(clientSession, indexName, dropIndexOptions);
  }

  @Override
  public Publisher<Success> dropIndex(
      ClientSession clientSession, Bson keys, DropIndexOptions dropIndexOptions) {
    return c.dropIndex(clientSession, keys, dropIndexOptions);
  }

  @Override
  public Publisher<Success> dropIndexes() {
    return c.dropIndexes();
  }

  @Override
  public Publisher<Success> dropIndexes(DropIndexOptions dropIndexOptions) {
    return c.dropIndexes(dropIndexOptions);
  }

  @Override
  public Publisher<Success> dropIndexes(ClientSession clientSession) {
    return c.dropIndexes(clientSession);
  }

  @Override
  public Publisher<Success> dropIndexes(
      ClientSession clientSession, DropIndexOptions dropIndexOptions) {
    return c.dropIndexes(clientSession, dropIndexOptions);
  }

  @Override
  public Publisher<Success> renameCollection(MongoNamespace newCollectionNamespace) {
    return c.renameCollection(newCollectionNamespace);
  }

  @Override
  public Publisher<Success> renameCollection(
      MongoNamespace newCollectionNamespace, RenameCollectionOptions options) {
    return c.renameCollection(newCollectionNamespace, options);
  }

  @Override
  public Publisher<Success> renameCollection(
      ClientSession clientSession, MongoNamespace newCollectionNamespace) {
    return c.renameCollection(clientSession, newCollectionNamespace);
  }

  @Override
  public Publisher<Success> renameCollection(
      ClientSession clientSession,
      MongoNamespace newCollectionNamespace,
      RenameCollectionOptions options) {
    return c.renameCollection(clientSession, newCollectionNamespace, options);
  }
}
