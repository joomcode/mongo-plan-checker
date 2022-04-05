package com.joom.mongoplanchecker.sync;

import static com.mongodb.client.model.ReplaceOptions.createReplaceOptions;

import com.joom.mongoplanchecker.core.BadPlanException;
import com.joom.mongoplanchecker.core.Nullable;
import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.core.Violations;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class PlanCheckerMongoCollection<TDocument> implements MongoCollection<TDocument> {

  private final MongoCollection<TDocument> collection;
  private final PlanChecker checker;

  public PlanCheckerMongoCollection(MongoCollection<TDocument> collection, PlanChecker checker) {
    this.collection = collection;
    this.checker = checker;
  }

  @SuppressWarnings("deprecation")
  private void check(
      Bson filter, @Nullable Bson hint, @Nullable Bson sort, @Nullable Collation collation) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      return;
    }
    Document plan =
        (Document)
            collection
                .find(filter)
                .hint(hint)
                .sort(sort)
                .collation(collation)
                .modifiers(new Document("$explain", true))
                .first();
    Violations violations = checker.getViolations(plan);
    if (violations.any()) {
      throw new BadPlanException(plan, violations);
    }
  }

  @SuppressWarnings("deprecation")
  private void check(
      ClientSession clientSession,
      Bson filter,
      @Nullable Bson hint,
      @Nullable Bson sort,
      @Nullable Collation collation) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      return;
    }
    Document plan =
        (Document)
            collection
                .find(clientSession, filter)
                .hint(hint)
                .sort(sort)
                .collation(collation)
                .modifiers(new Document("$explain", true))
                .first();
    Violations violations = checker.getViolations(plan);
    if (violations.any()) {
      throw new BadPlanException(plan, violations);
    }
  }

  @Override
  public <TResult> PlanCheckerFindIterable<TResult> find(Bson filter, Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(collection.find(filter, tResultClass), checker);
  }

  @Override
  public <TResult> PlanCheckerFindIterable<TResult> find(
      ClientSession clientSession, Bson filter, Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(
        collection.find(clientSession, filter, tResultClass), checker);
  }

  @Override
  public UpdateResult replaceOne(
      Bson filter, TDocument replacement, ReplaceOptions replaceOptions) {
    check(filter, null, null, replaceOptions.getCollation());
    return collection.replaceOne(filter, replacement, replaceOptions);
  }

  @Override
  public MongoNamespace getNamespace() {
    return collection.getNamespace();
  }

  @Override
  public Class<TDocument> getDocumentClass() {
    return collection.getDocumentClass();
  }

  @Override
  public CodecRegistry getCodecRegistry() {
    return collection.getCodecRegistry();
  }

  @Override
  public ReadPreference getReadPreference() {
    return collection.getReadPreference();
  }

  @Override
  public WriteConcern getWriteConcern() {
    return collection.getWriteConcern();
  }

  @Override
  public ReadConcern getReadConcern() {
    return collection.getReadConcern();
  }

  @Override
  public <NewTDocument> PlanCheckerMongoCollection<NewTDocument> withDocumentClass(
      Class<NewTDocument> clazz) {
    return new PlanCheckerMongoCollection<>(collection.withDocumentClass(clazz), checker);
  }

  @Override
  public PlanCheckerMongoCollection<TDocument> withCodecRegistry(CodecRegistry codecRegistry) {
    return new PlanCheckerMongoCollection<>(collection.withCodecRegistry(codecRegistry), checker);
  }

  @Override
  public PlanCheckerMongoCollection<TDocument> withReadPreference(ReadPreference readPreference) {
    return new PlanCheckerMongoCollection<>(collection.withReadPreference(readPreference), checker);
  }

  @Override
  public PlanCheckerMongoCollection<TDocument> withWriteConcern(WriteConcern writeConcern) {
    return new PlanCheckerMongoCollection<>(collection.withWriteConcern(writeConcern), checker);
  }

  @Override
  public PlanCheckerMongoCollection<TDocument> withReadConcern(ReadConcern readConcern) {
    return new PlanCheckerMongoCollection<>(collection.withReadConcern(readConcern), checker);
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count() {
    return collection.count();
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count(Bson filter) {
    check(filter, null, null, null);
    return collection.count(filter);
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count(Bson filter, CountOptions options) {
    // TODO hintString is not supported
    check(filter, options.getHint(), null, null);
    return collection.count(filter, options);
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count(ClientSession clientSession) {
    return collection.count(clientSession);
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count(ClientSession clientSession, Bson filter) {
    check(clientSession, filter, null, null, null);
    return collection.count(clientSession, filter);
  }

  @Override
  @SuppressWarnings("deprecation")
  public long count(ClientSession clientSession, Bson filter, CountOptions options) {
    // TODO hintString is not supported
    check(filter, options.getHint(), null, null);
    return collection.count(clientSession, filter, options);
  }

  @Override
  public long countDocuments() {
    return collection.countDocuments();
  }

  @Override
  public long countDocuments(Bson filter) {
    check(filter, null, null, null);
    return collection.countDocuments(filter);
  }

  @Override
  public long countDocuments(Bson filter, CountOptions options) {
    // TODO hintString is not supported
    check(filter, options.getHint(), null, null);
    return collection.countDocuments(filter, options);
  }

  @Override
  public long countDocuments(ClientSession clientSession) {
    return collection.countDocuments(clientSession);
  }

  @Override
  public long countDocuments(ClientSession clientSession, Bson filter) {
    check(clientSession, filter, null, null, null);
    return collection.countDocuments(clientSession, filter);
  }

  @Override
  public long countDocuments(ClientSession clientSession, Bson filter, CountOptions options) {
    // TODO hintString is not supported
    check(clientSession, filter, options.getHint(), null, null);
    return collection.countDocuments(clientSession, filter, options);
  }

  @Override
  public long estimatedDocumentCount() {
    return collection.estimatedDocumentCount();
  }

  @Override
  public long estimatedDocumentCount(EstimatedDocumentCountOptions options) {
    return collection.estimatedDocumentCount(options);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      String fieldName, Class<TResult> tResultClass) {
    return collection.distinct(fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      String fieldName, Bson filter, Class<TResult> tResultClass) {
    return collection.distinct(fieldName, filter, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      ClientSession clientSession, String fieldName, Class<TResult> tResultClass) {
    return collection.distinct(clientSession, fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      ClientSession clientSession, String fieldName, Bson filter, Class<TResult> tResultClass) {
    return collection.distinct(clientSession, fieldName, filter, tResultClass);
  }

  @Override
  public FindIterable<TDocument> find() {
    return find(new BsonDocument(), collection.getDocumentClass());
  }

  @Override
  public <TResult> FindIterable<TResult> find(Class<TResult> tResultClass) {
    return find(new BsonDocument(), tResultClass);
  }

  @Override
  public FindIterable<TDocument> find(Bson filter) {
    return find(filter, collection.getDocumentClass());
  }

  @Override
  public FindIterable<TDocument> find(ClientSession clientSession) {
    return find(clientSession, new BsonDocument(), collection.getDocumentClass());
  }

  @Override
  public <TResult> FindIterable<TResult> find(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return find(clientSession, new BsonDocument(), tResultClass);
  }

  @Override
  public FindIterable<TDocument> find(ClientSession clientSession, Bson filter) {
    return find(clientSession, filter, collection.getDocumentClass());
  }

  @Override
  public AggregateIterable<TDocument> aggregate(List<? extends Bson> pipeline) {
    return collection.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return collection.aggregate(pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<TDocument> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return collection.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return collection.aggregate(clientSession, pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch() {
    return collection.watch();
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
    return collection.watch(tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(List<? extends Bson> pipeline) {
    return collection.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return collection.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(ClientSession clientSession) {
    return collection.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return collection.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return collection.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return collection.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public MapReduceIterable<TDocument> mapReduce(String mapFunction, String reduceFunction) {
    return collection.mapReduce(mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReduceIterable<TResult> mapReduce(
      String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
    return collection.mapReduce(mapFunction, reduceFunction, tResultClass);
  }

  @Override
  public MapReduceIterable<TDocument> mapReduce(
      ClientSession clientSession, String mapFunction, String reduceFunction) {
    return collection.mapReduce(clientSession, mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReduceIterable<TResult> mapReduce(
      ClientSession clientSession,
      String mapFunction,
      String reduceFunction,
      Class<TResult> tResultClass) {
    return collection.mapReduce(clientSession, mapFunction, reduceFunction, tResultClass);
  }

  @Override
  public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests) {
    return collection.bulkWrite(requests);
  }

  @Override
  public BulkWriteResult bulkWrite(
      List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
    return collection.bulkWrite(requests, options);
  }

  @Override
  public BulkWriteResult bulkWrite(
      ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests) {
    return collection.bulkWrite(clientSession, requests);
  }

  @Override
  public BulkWriteResult bulkWrite(
      ClientSession clientSession,
      List<? extends WriteModel<? extends TDocument>> requests,
      BulkWriteOptions options) {
    return collection.bulkWrite(clientSession, requests, options);
  }

  @Override
  public void insertOne(TDocument tDocument) {
    collection.insertOne(tDocument);
  }

  @Override
  public void insertOne(TDocument tDocument, InsertOneOptions options) {
    collection.insertOne(tDocument, options);
  }

  @Override
  public void insertOne(ClientSession clientSession, TDocument tDocument) {
    collection.insertOne(clientSession, tDocument);
  }

  @Override
  public void insertOne(
      ClientSession clientSession, TDocument tDocument, InsertOneOptions options) {
    collection.insertOne(clientSession, tDocument, options);
  }

  @Override
  public void insertMany(List<? extends TDocument> tDocuments) {
    collection.insertMany(tDocuments);
  }

  @Override
  public void insertMany(List<? extends TDocument> tDocuments, InsertManyOptions options) {
    collection.insertMany(tDocuments, options);
  }

  @Override
  public void insertMany(ClientSession clientSession, List<? extends TDocument> tDocuments) {
    collection.insertMany(clientSession, tDocuments);
  }

  @Override
  public void insertMany(
      ClientSession clientSession,
      List<? extends TDocument> tDocuments,
      InsertManyOptions options) {
    collection.insertMany(clientSession, tDocuments, options);
  }

  @Override
  public DeleteResult deleteOne(Bson filter) {
    check(filter, null, null, null);
    return collection.deleteOne(filter);
  }

  @Override
  public DeleteResult deleteOne(Bson filter, DeleteOptions options) {
    check(filter, null, null, options.getCollation());
    return collection.deleteOne(filter, options);
  }

  @Override
  public DeleteResult deleteOne(ClientSession clientSession, Bson filter) {
    check(clientSession, filter, null, null, null);
    return collection.deleteOne(clientSession, filter);
  }

  @Override
  public DeleteResult deleteOne(ClientSession clientSession, Bson filter, DeleteOptions options) {
    check(clientSession, filter, null, null, options.getCollation());
    return collection.deleteOne(clientSession, filter, options);
  }

  @Override
  public DeleteResult deleteMany(Bson filter) {
    check(filter, null, null, null);
    return collection.deleteMany(filter);
  }

  @Override
  public DeleteResult deleteMany(Bson filter, DeleteOptions options) {
    check(filter, null, null, options.getCollation());
    return collection.deleteMany(filter, options);
  }

  @Override
  public DeleteResult deleteMany(ClientSession clientSession, Bson filter) {
    check(clientSession, filter, null, null, null);
    return collection.deleteMany(clientSession, filter);
  }

  @Override
  public DeleteResult deleteMany(ClientSession clientSession, Bson filter, DeleteOptions options) {
    check(clientSession, filter, null, null, options.getCollation());
    return collection.deleteMany(clientSession, filter, options);
  }

  @Override
  public UpdateResult replaceOne(Bson filter, TDocument replacement) {
    return replaceOne(filter, replacement, new ReplaceOptions());
  }

  @Override
  @SuppressWarnings("deprecation")
  public UpdateResult replaceOne(Bson filter, TDocument replacement, UpdateOptions updateOptions) {
    return replaceOne(filter, replacement, createReplaceOptions(updateOptions));
  }

  @Override
  public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement) {
    return replaceOne(clientSession, filter, replacement, new ReplaceOptions());
  }

  @Override
  @SuppressWarnings("deprecation")
  public UpdateResult replaceOne(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      UpdateOptions updateOptions) {
    return replaceOne(clientSession, filter, replacement, createReplaceOptions(updateOptions));
  }

  @Override
  public UpdateResult replaceOne(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      ReplaceOptions replaceOptions) {
    check(clientSession, filter, null, null, replaceOptions.getCollation());
    return collection.replaceOne(clientSession, filter, replacement, replaceOptions);
  }

  @Override
  public UpdateResult updateOne(Bson filter, Bson update) {
    check(filter, null, null, null);
    return collection.updateOne(filter, update);
  }

  @Override
  public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
    check(filter, null, null, updateOptions.getCollation());
    return collection.updateOne(filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateOne(ClientSession clientSession, Bson filter, Bson update) {
    check(clientSession, filter, null, null, null);
    return collection.updateOne(clientSession, filter, update);
  }

  @Override
  public UpdateResult updateOne(
      ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
    check(clientSession, filter, null, null, updateOptions.getCollation());
    return collection.updateOne(clientSession, filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateOne(Bson filter, List<? extends Bson> update) {
    check(filter, null, null, null);
    return collection.updateOne(filter, update);
  }

  @Override
  public UpdateResult updateOne(
      Bson filter, List<? extends Bson> update, UpdateOptions updateOptions) {
    check(filter, null, null, updateOptions.getCollation());
    return collection.updateOne(filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateOne(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    check(clientSession, filter, null, null, null);
    return collection.updateOne(clientSession, filter, update);
  }

  @Override
  public UpdateResult updateOne(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions updateOptions) {
    check(clientSession, filter, null, null, updateOptions.getCollation());
    return collection.updateOne(clientSession, filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateMany(Bson filter, Bson update) {
    check(filter, null, null, null);
    return collection.updateMany(filter, update);
  }

  @Override
  public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
    check(filter, null, null, updateOptions.getCollation());
    return collection.updateMany(filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateMany(ClientSession clientSession, Bson filter, Bson update) {
    check(clientSession, filter, null, null, null);
    return collection.updateMany(clientSession, filter, update);
  }

  @Override
  public UpdateResult updateMany(
      ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
    check(clientSession, filter, null, null, updateOptions.getCollation());
    return collection.updateMany(clientSession, filter, update, updateOptions);
  }

  @Override
  public UpdateResult updateMany(Bson filter, List<? extends Bson> update) {
    check(filter, null, null, null);
    return collection.updateMany(filter, update);
  }

  @Override
  public UpdateResult updateMany(Bson filter, List<? extends Bson> update, UpdateOptions options) {
    check(filter, null, null, options.getCollation());
    return collection.updateMany(filter, update, options);
  }

  @Override
  public UpdateResult updateMany(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    check(clientSession, filter, null, null, null);
    return collection.updateMany(clientSession, filter, update);
  }

  @Override
  public UpdateResult updateMany(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions updateOptions) {
    check(clientSession, filter, null, null, updateOptions.getCollation());
    return collection.updateMany(clientSession, filter, update);
  }

  @Override
  public TDocument findOneAndDelete(Bson filter) {
    check(filter, null, null, null);
    return collection.findOneAndDelete(filter);
  }

  @Override
  public TDocument findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
    check(filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndDelete(filter, options);
  }

  @Override
  public TDocument findOneAndDelete(ClientSession clientSession, Bson filter) {
    check(clientSession, filter, null, null, null);
    return collection.findOneAndDelete(clientSession, filter);
  }

  @Override
  public TDocument findOneAndDelete(
      ClientSession clientSession, Bson filter, FindOneAndDeleteOptions options) {
    check(clientSession, filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndDelete(clientSession, filter, options);
  }

  @Override
  public TDocument findOneAndReplace(Bson filter, TDocument replacement) {
    check(filter, null, null, null);
    return collection.findOneAndReplace(filter, replacement);
  }

  @Override
  public TDocument findOneAndReplace(
      Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
    check(filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndReplace(filter, replacement, options);
  }

  @Override
  public TDocument findOneAndReplace(
      ClientSession clientSession, Bson filter, TDocument replacement) {
    check(clientSession, filter, null, null, null);
    return collection.findOneAndReplace(clientSession, filter, replacement);
  }

  @Override
  public TDocument findOneAndReplace(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      FindOneAndReplaceOptions options) {
    check(clientSession, filter, null, options.getSort(), null);
    return collection.findOneAndReplace(clientSession, filter, replacement, options);
  }

  @Override
  public TDocument findOneAndUpdate(Bson filter, Bson update) {
    check(filter, null, null, null);
    return collection.findOneAndUpdate(filter, update);
  }

  @Override
  public TDocument findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
    check(filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndUpdate(filter, update, options);
  }

  @Override
  public TDocument findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update) {
    check(clientSession, filter, null, null, null);
    return collection.findOneAndUpdate(clientSession, filter, update);
  }

  @Override
  public TDocument findOneAndUpdate(
      ClientSession clientSession, Bson filter, Bson update, FindOneAndUpdateOptions options) {
    check(clientSession, filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndUpdate(clientSession, filter, update, options);
  }

  @Override
  public TDocument findOneAndUpdate(Bson filter, List<? extends Bson> update) {
    check(filter, null, null, null);
    return collection.findOneAndUpdate(filter, update);
  }

  @Override
  public TDocument findOneAndUpdate(
      Bson filter, List<? extends Bson> update, FindOneAndUpdateOptions options) {
    check(filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndUpdate(filter, update, options);
  }

  @Override
  public TDocument findOneAndUpdate(
      ClientSession clientSession, Bson filter, List<? extends Bson> update) {
    check(clientSession, filter, null, null, null);
    return collection.findOneAndUpdate(clientSession, filter, update);
  }

  @Override
  public TDocument findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      FindOneAndUpdateOptions options) {
    check(clientSession, filter, null, options.getSort(), options.getCollation());
    return collection.findOneAndUpdate(clientSession, filter, update, options);
  }

  @Override
  public void drop() {
    collection.drop();
  }

  @Override
  public void drop(ClientSession clientSession) {
    collection.drop(clientSession);
  }

  @Override
  public String createIndex(Bson keys) {
    return collection.createIndex(keys);
  }

  @Override
  public String createIndex(Bson keys, IndexOptions indexOptions) {
    return collection.createIndex(keys, indexOptions);
  }

  @Override
  public String createIndex(ClientSession clientSession, Bson keys) {
    return collection.createIndex(clientSession, keys);
  }

  @Override
  public String createIndex(ClientSession clientSession, Bson keys, IndexOptions indexOptions) {
    return collection.createIndex(clientSession, keys, indexOptions);
  }

  @Override
  public List<String> createIndexes(List<IndexModel> indexes) {
    return collection.createIndexes(indexes);
  }

  @Override
  public List<String> createIndexes(
      List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
    return collection.createIndexes(indexes, createIndexOptions);
  }

  @Override
  public List<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes) {
    return collection.createIndexes(clientSession, indexes);
  }

  @Override
  public List<String> createIndexes(
      ClientSession clientSession,
      List<IndexModel> indexes,
      CreateIndexOptions createIndexOptions) {
    return collection.createIndexes(clientSession, indexes, createIndexOptions);
  }

  @Override
  public ListIndexesIterable<Document> listIndexes() {
    return collection.listIndexes();
  }

  @Override
  public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
    return collection.listIndexes(tResultClass);
  }

  @Override
  public ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
    return collection.listIndexes(clientSession);
  }

  @Override
  public <TResult> ListIndexesIterable<TResult> listIndexes(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return collection.listIndexes(clientSession, tResultClass);
  }

  @Override
  public void dropIndex(String indexName) {
    collection.dropIndex(indexName);
  }

  @Override
  public void dropIndex(String indexName, DropIndexOptions dropIndexOptions) {
    collection.dropIndex(indexName, dropIndexOptions);
  }

  @Override
  public void dropIndex(Bson keys) {
    collection.dropIndex(keys);
  }

  @Override
  public void dropIndex(Bson keys, DropIndexOptions dropIndexOptions) {
    collection.dropIndex(keys, dropIndexOptions);
  }

  @Override
  public void dropIndex(ClientSession clientSession, String indexName) {
    collection.dropIndex(clientSession, indexName);
  }

  @Override
  public void dropIndex(ClientSession clientSession, Bson keys) {
    collection.dropIndex(clientSession, keys);
  }

  @Override
  public void dropIndex(
      ClientSession clientSession, String indexName, DropIndexOptions dropIndexOptions) {
    collection.dropIndex(clientSession, indexName, dropIndexOptions);
  }

  @Override
  public void dropIndex(ClientSession clientSession, Bson keys, DropIndexOptions dropIndexOptions) {
    collection.dropIndex(clientSession, keys, dropIndexOptions);
  }

  @Override
  public void dropIndexes() {
    collection.dropIndexes();
  }

  @Override
  public void dropIndexes(ClientSession clientSession) {
    collection.dropIndexes(clientSession);
  }

  @Override
  public void dropIndexes(DropIndexOptions dropIndexOptions) {
    collection.dropIndexes(dropIndexOptions);
  }

  @Override
  public void dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions) {
    collection.dropIndexes(clientSession, dropIndexOptions);
  }

  @Override
  public void renameCollection(MongoNamespace newCollectionNamespace) {
    collection.renameCollection(newCollectionNamespace);
  }

  @Override
  public void renameCollection(
      MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
    collection.renameCollection(newCollectionNamespace, renameCollectionOptions);
  }

  @Override
  public void renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace) {
    collection.renameCollection(clientSession, newCollectionNamespace);
  }

  @Override
  public void renameCollection(
      ClientSession clientSession,
      MongoNamespace newCollectionNamespace,
      RenameCollectionOptions renameCollectionOptions) {
    collection.renameCollection(clientSession, newCollectionNamespace, renameCollectionOptions);
  }
}
