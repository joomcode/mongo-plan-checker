package com.github.joomcode.mongoplanchecker.async;

import static com.github.joomcode.mongoplanchecker.core.PlanChecker.explainModifier;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.Nullable;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.*;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

@SuppressWarnings("deprecation")
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
  public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(
      Class<NewTDocument> newDocumentClass) {
    return new PlanCheckerMongoCollection<>(c.withDocumentClass(newDocumentClass), checker);
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
  public void count(SingleResultCallback<Long> callback) {
    c.count(callback);
  }

  private <T> void check(
      Bson filter,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(filter, null, null, callback, realOperation);
  }

  private <T> void check(
      Bson filter,
      @Nullable Bson hint,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(filter, hint, null, callback, realOperation);
  }

  private <T> void check(
      Bson filter,
      @Nullable Collation collation,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(filter, null, collation, callback, realOperation);
  }

  private <T> void check(
      Bson filter,
      @Nullable Bson hint,
      @Nullable Collation collation,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      realOperation.accept(c);
      return;
    }
    c.find(filter)
        .hint(hint)
        .collation(collation)
        .modifiers(explainModifier())
        .first(
            (result, t) -> {
              if (t != null) {
                callback.onResult(null, t);
              } else {
                Document plan = (Document) result;
                Violations violations = checker.getViolations(plan);
                if (violations.any()) {
                  callback.onResult(null, new BadPlanException(plan, violations));
                } else {
                  realOperation.accept(c);
                }
              }
            });
  }

  private <T> void check(
      ClientSession clientSession,
      Bson filter,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(clientSession, filter, null, null, callback, realOperation);
  }

  private <T> void check(
      ClientSession clientSession,
      Bson filter,
      @Nullable Bson hint,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(clientSession, filter, hint, null, callback, realOperation);
  }

  private <T> void check(
      ClientSession clientSession,
      Bson filter,
      @Nullable Collation collation,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    check(clientSession, filter, null, collation, callback, realOperation);
  }

  private <T> void check(
      ClientSession clientSession,
      Bson filter,
      @Nullable Bson hint,
      @Nullable Collation collation,
      SingleResultCallback<T> callback,
      Consumer<MongoCollection<TDocument>> realOperation) {
    if (filter.toBsonDocument(Document.class, getCodecRegistry()).isEmpty()) {
      realOperation.accept(c);
      return;
    }
    c.find(clientSession, filter)
        .hint(hint)
        .collation(collation)
        .modifiers(explainModifier())
        .first(
            (result, t) -> {
              if (t != null) {
                callback.onResult(null, t);
              } else {
                Document plan = (Document) result;
                Violations violations = checker.getViolations(plan);
                if (violations.any()) {
                  callback.onResult(null, new BadPlanException(plan, violations));
                } else {
                  realOperation.accept(c);
                }
              }
            });
  }

  @Override
  public void count(Bson filter, SingleResultCallback<Long> callback) {
    check(filter, callback, c -> c.count(filter, callback));
  }

  @Override
  public void count(Bson filter, CountOptions options, SingleResultCallback<Long> callback) {
    check(filter, options.getHint(), callback, c -> c.count(filter, options, callback));
  }

  @Override
  public void count(ClientSession clientSession, SingleResultCallback<Long> callback) {
    c.count(clientSession, callback);
  }

  @Override
  public void count(ClientSession clientSession, Bson filter, SingleResultCallback<Long> callback) {
    check(clientSession, filter, callback, c -> c.count(clientSession, filter, callback));
  }

  @Override
  public void count(
      ClientSession clientSession,
      Bson filter,
      CountOptions options,
      SingleResultCallback<Long> callback) {
    check(
        clientSession,
        filter,
        options.getHint(),
        callback,
        c -> c.count(clientSession, filter, options, callback));
  }

  @Override
  public void countDocuments(SingleResultCallback<Long> callback) {
    c.countDocuments(callback);
  }

  @Override
  public void countDocuments(ClientSession clientSession, SingleResultCallback<Long> callback) {
    c.countDocuments(clientSession, callback);
  }

  @Override
  public void countDocuments(Bson filter, SingleResultCallback<Long> callback) {
    check(filter, callback, c -> c.countDocuments(filter, callback));
  }

  @Override
  public void countDocuments(
      Bson filter, CountOptions options, SingleResultCallback<Long> callback) {
    check(filter, options.getHint(), callback, c -> c.countDocuments(filter, options, callback));
  }

  @Override
  public void countDocuments(
      ClientSession clientSession, Bson filter, SingleResultCallback<Long> callback) {
    check(clientSession, filter, callback, c -> c.countDocuments(clientSession, filter, callback));
  }

  @Override
  public void countDocuments(
      ClientSession clientSession,
      Bson filter,
      CountOptions options,
      SingleResultCallback<Long> callback) {
    check(
        clientSession,
        filter,
        options.getHint(),
        callback,
        c -> c.countDocuments(clientSession, filter, options, callback));
  }

  @Override
  public void estimatedDocumentCount(SingleResultCallback<Long> callback) {
    c.estimatedDocumentCount(callback);
  }

  @Override
  public void estimatedDocumentCount(
      EstimatedDocumentCountOptions options, SingleResultCallback<Long> callback) {
    c.estimatedDocumentCount(options, callback);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      String fieldName, Class<TResult> tResultClass) {
    return c.distinct(fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      String fieldName, Bson filter, Class<TResult> tResultClass) {
    return c.distinct(fieldName, filter, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      ClientSession clientSession, String fieldName, Class<TResult> tResultClass) {
    return c.distinct(clientSession, fieldName, tResultClass);
  }

  @Override
  public <TResult> DistinctIterable<TResult> distinct(
      ClientSession clientSession, String fieldName, Bson filter, Class<TResult> tResultClass) {
    return c.distinct(clientSession, fieldName, filter, tResultClass);
  }

  @Override
  public FindIterable<TDocument> find() {
    return new PlanCheckerFindIterable<>(c.find(), checker);
  }

  @Override
  public <TResult> FindIterable<TResult> find(Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(c.find(tResultClass), checker);
  }

  @Override
  public FindIterable<TDocument> find(Bson filter) {
    return new PlanCheckerFindIterable<>(c.find(filter), checker);
  }

  @Override
  public <TResult> FindIterable<TResult> find(Bson filter, Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(c.find(filter, tResultClass), checker);
  }

  @Override
  public FindIterable<TDocument> find(ClientSession clientSession) {
    return new PlanCheckerFindIterable<>(c.find(clientSession), checker);
  }

  @Override
  public <TResult> FindIterable<TResult> find(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(c.find(clientSession, tResultClass), checker);
  }

  @Override
  public FindIterable<TDocument> find(ClientSession clientSession, Bson filter) {
    return new PlanCheckerFindIterable<>(c.find(clientSession, filter), checker);
  }

  @Override
  public <TResult> FindIterable<TResult> find(
      ClientSession clientSession, Bson filter, Class<TResult> tResultClass) {
    return new PlanCheckerFindIterable<>(c.find(clientSession, filter, tResultClass), checker);
  }

  @Override
  public AggregateIterable<TDocument> aggregate(List<? extends Bson> pipeline) {
    return c.aggregate(pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.aggregate(pipeline, tResultClass);
  }

  @Override
  public AggregateIterable<TDocument> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return c.aggregate(clientSession, pipeline);
  }

  @Override
  public <TResult> AggregateIterable<TResult> aggregate(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.aggregate(clientSession, pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch() {
    return c.watch();
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
    return c.watch(tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(List<? extends Bson> pipeline) {
    return c.watch(pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.watch(pipeline, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(ClientSession clientSession) {
    return c.watch(clientSession);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return c.watch(clientSession, tResultClass);
  }

  @Override
  public ChangeStreamIterable<TDocument> watch(
      ClientSession clientSession, List<? extends Bson> pipeline) {
    return c.watch(clientSession, pipeline);
  }

  @Override
  public <TResult> ChangeStreamIterable<TResult> watch(
      ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
    return c.watch(clientSession, pipeline, tResultClass);
  }

  @Override
  public MapReduceIterable<TDocument> mapReduce(String mapFunction, String reduceFunction) {
    return c.mapReduce(mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReduceIterable<TResult> mapReduce(
      String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
    return c.mapReduce(mapFunction, reduceFunction, tResultClass);
  }

  @Override
  public MapReduceIterable<TDocument> mapReduce(
      ClientSession clientSession, String mapFunction, String reduceFunction) {
    return c.mapReduce(clientSession, mapFunction, reduceFunction);
  }

  @Override
  public <TResult> MapReduceIterable<TResult> mapReduce(
      ClientSession clientSession,
      String mapFunction,
      String reduceFunction,
      Class<TResult> tResultClass) {
    return c.mapReduce(clientSession, mapFunction, reduceFunction, tResultClass);
  }

  @Override
  public void bulkWrite(
      List<? extends WriteModel<? extends TDocument>> requests,
      SingleResultCallback<BulkWriteResult> callback) {
    c.bulkWrite(requests, callback);
  }

  @Override
  public void bulkWrite(
      List<? extends WriteModel<? extends TDocument>> requests,
      BulkWriteOptions options,
      SingleResultCallback<BulkWriteResult> callback) {
    c.bulkWrite(requests, options, callback);
  }

  @Override
  public void bulkWrite(
      ClientSession clientSession,
      List<? extends WriteModel<? extends TDocument>> requests,
      SingleResultCallback<BulkWriteResult> callback) {
    c.bulkWrite(clientSession, requests, callback);
  }

  @Override
  public void bulkWrite(
      ClientSession clientSession,
      List<? extends WriteModel<? extends TDocument>> requests,
      BulkWriteOptions options,
      SingleResultCallback<BulkWriteResult> callback) {
    c.bulkWrite(clientSession, requests, options, callback);
  }

  @Override
  public void insertOne(TDocument tDocument, SingleResultCallback<Void> callback) {
    c.insertOne(tDocument, callback);
  }

  @Override
  public void insertOne(
      TDocument tDocument, InsertOneOptions options, SingleResultCallback<Void> callback) {
    c.insertOne(tDocument, options, callback);
  }

  @Override
  public void insertOne(
      ClientSession clientSession, TDocument tDocument, SingleResultCallback<Void> callback) {
    c.insertOne(clientSession, tDocument, callback);
  }

  @Override
  public void insertOne(
      ClientSession clientSession,
      TDocument tDocument,
      InsertOneOptions options,
      SingleResultCallback<Void> callback) {
    c.insertOne(clientSession, tDocument, options, callback);
  }

  @Override
  public void insertMany(
      List<? extends TDocument> tDocuments, SingleResultCallback<Void> callback) {
    c.insertMany(tDocuments, callback);
  }

  @Override
  public void insertMany(
      List<? extends TDocument> tDocuments,
      InsertManyOptions options,
      SingleResultCallback<Void> callback) {
    c.insertMany(tDocuments, options, callback);
  }

  @Override
  public void insertMany(
      ClientSession clientSession,
      List<? extends TDocument> tDocuments,
      SingleResultCallback<Void> callback) {
    c.insertMany(clientSession, tDocuments, callback);
  }

  @Override
  public void insertMany(
      ClientSession clientSession,
      List<? extends TDocument> tDocuments,
      InsertManyOptions options,
      SingleResultCallback<Void> callback) {
    c.insertMany(clientSession, tDocuments, options, callback);
  }

  @Override
  public void deleteOne(Bson filter, SingleResultCallback<DeleteResult> callback) {
    check(filter, callback, c -> c.deleteOne(filter, callback));
  }

  @Override
  public void deleteOne(
      Bson filter, DeleteOptions options, SingleResultCallback<DeleteResult> callback) {
    check(filter, options.getCollation(), callback, c -> c.deleteOne(filter, options, callback));
  }

  @Override
  public void deleteOne(
      ClientSession clientSession, Bson filter, SingleResultCallback<DeleteResult> callback) {
    check(clientSession, filter, callback, c -> c.deleteOne(clientSession, filter, callback));
  }

  @Override
  public void deleteOne(
      ClientSession clientSession,
      Bson filter,
      DeleteOptions options,
      SingleResultCallback<DeleteResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.deleteOne(clientSession, filter, options, callback));
  }

  @Override
  public void deleteMany(Bson filter, SingleResultCallback<DeleteResult> callback) {
    check(filter, callback, c -> c.deleteMany(filter, callback));
  }

  @Override
  public void deleteMany(
      Bson filter, DeleteOptions options, SingleResultCallback<DeleteResult> callback) {
    check(filter, options.getCollation(), callback, c -> c.deleteMany(filter, options, callback));
  }

  @Override
  public void deleteMany(
      ClientSession clientSession, Bson filter, SingleResultCallback<DeleteResult> callback) {
    check(clientSession, filter, callback, c -> c.deleteMany(clientSession, filter, callback));
  }

  @Override
  public void deleteMany(
      ClientSession clientSession,
      Bson filter,
      DeleteOptions options,
      SingleResultCallback<DeleteResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.deleteMany(clientSession, filter, options, callback));
  }

  @Override
  public void replaceOne(
      Bson filter, TDocument replacement, SingleResultCallback<UpdateResult> callback) {
    check(filter, callback, c -> c.replaceOne(filter, replacement, callback));
  }

  @Override
  public void replaceOne(
      Bson filter,
      TDocument replacement,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.replaceOne(filter, replacement, options, callback));
  }

  @Override
  public void replaceOne(
      Bson filter,
      TDocument replacement,
      ReplaceOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.replaceOne(filter, replacement, options, callback));
  }

  @Override
  public void replaceOne(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.replaceOne(clientSession, filter, replacement, callback));
  }

  @Override
  public void replaceOne(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.replaceOne(clientSession, filter, replacement, options, callback));
  }

  @Override
  public void replaceOne(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      ReplaceOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.replaceOne(clientSession, filter, replacement, options, callback));
  }

  @Override
  public void updateOne(Bson filter, Bson update, SingleResultCallback<UpdateResult> callback) {
    check(filter, callback, c -> c.updateOne(filter, update, callback));
  }

  @Override
  public void updateOne(
      Bson filter,
      Bson update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.updateOne(filter, update, options, callback));
  }

  @Override
  public void updateOne(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession, filter, callback, c -> c.updateOne(clientSession, filter, update, callback));
  }

  @Override
  public void updateOne(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.updateOne(clientSession, filter, update, options, callback));
  }

  @Override
  public void updateOne(
      Bson filter, List<? extends Bson> update, SingleResultCallback<UpdateResult> callback) {
    check(filter, callback, c -> c.updateOne(filter, update, callback));
  }

  @Override
  public void updateOne(
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.updateOne(filter, update, options, callback));
  }

  @Override
  public void updateOne(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession, filter, callback, c -> c.updateOne(clientSession, filter, update, callback));
  }

  @Override
  public void updateOne(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.updateOne(clientSession, filter, update, options, callback));
  }

  @Override
  public void updateMany(Bson filter, Bson update, SingleResultCallback<UpdateResult> callback) {
    check(filter, callback, c -> c.updateMany(filter, update, callback));
  }

  @Override
  public void updateMany(
      Bson filter,
      Bson update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.updateMany(filter, update, options, callback));
  }

  @Override
  public void updateMany(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.updateMany(clientSession, filter, update, callback));
  }

  @Override
  public void updateMany(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.updateMany(clientSession, filter, update, options, callback));
  }

  @Override
  public void updateMany(
      Bson filter, List<? extends Bson> update, SingleResultCallback<UpdateResult> callback) {
    check(filter, callback, c -> c.updateMany(filter, update, callback));
  }

  @Override
  public void updateMany(
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.updateMany(filter, update, options, callback));
  }

  @Override
  public void updateMany(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.updateMany(clientSession, filter, update, callback));
  }

  @Override
  public void updateMany(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      UpdateOptions options,
      SingleResultCallback<UpdateResult> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.updateMany(clientSession, filter, update, options, callback));
  }

  @Override
  public void findOneAndDelete(Bson filter, SingleResultCallback<TDocument> callback) {
    check(filter, callback, c -> c.findOneAndDelete(filter, callback));
  }

  @Override
  public void findOneAndDelete(
      Bson filter, FindOneAndDeleteOptions options, SingleResultCallback<TDocument> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndDelete(filter, options, callback));
  }

  @Override
  public void findOneAndDelete(
      ClientSession clientSession, Bson filter, SingleResultCallback<TDocument> callback) {
    check(
        clientSession, filter, callback, c -> c.findOneAndDelete(clientSession, filter, callback));
  }

  @Override
  public void findOneAndDelete(
      ClientSession clientSession,
      Bson filter,
      FindOneAndDeleteOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndDelete(clientSession, filter, options, callback));
  }

  @Override
  public void findOneAndReplace(
      Bson filter, TDocument replacement, SingleResultCallback<TDocument> callback) {
    check(filter, callback, c -> c.findOneAndReplace(filter, replacement, callback));
  }

  @Override
  public void findOneAndReplace(
      Bson filter,
      TDocument replacement,
      FindOneAndReplaceOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndReplace(filter, replacement, options, callback));
  }

  @Override
  public void findOneAndReplace(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.findOneAndReplace(clientSession, filter, replacement, callback));
  }

  @Override
  public void findOneAndReplace(
      ClientSession clientSession,
      Bson filter,
      TDocument replacement,
      FindOneAndReplaceOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndReplace(clientSession, filter, replacement, options, callback));
  }

  @Override
  public void findOneAndUpdate(Bson filter, Bson update, SingleResultCallback<TDocument> callback) {
    check(filter, callback, c -> c.findOneAndUpdate(filter, update, callback));
  }

  @Override
  public void findOneAndUpdate(
      Bson filter,
      Bson update,
      FindOneAndUpdateOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndUpdate(filter, update, options, callback));
  }

  @Override
  public void findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.findOneAndUpdate(clientSession, filter, update, callback));
  }

  @Override
  public void findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      Bson update,
      FindOneAndUpdateOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndUpdate(clientSession, filter, update, options, callback));
  }

  @Override
  public void findOneAndUpdate(
      Bson filter, List<? extends Bson> update, SingleResultCallback<TDocument> callback) {
    check(filter, callback, c -> c.findOneAndUpdate(filter, update, callback));
  }

  @Override
  public void findOneAndUpdate(
      Bson filter,
      List<? extends Bson> update,
      FindOneAndUpdateOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndUpdate(filter, update, options, callback));
  }

  @Override
  public void findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        callback,
        c -> c.findOneAndUpdate(clientSession, filter, update, callback));
  }

  @Override
  public void findOneAndUpdate(
      ClientSession clientSession,
      Bson filter,
      List<? extends Bson> update,
      FindOneAndUpdateOptions options,
      SingleResultCallback<TDocument> callback) {
    check(
        clientSession,
        filter,
        options.getCollation(),
        callback,
        c -> c.findOneAndUpdate(clientSession, filter, update, options, callback));
  }

  @Override
  public void drop(SingleResultCallback<Void> callback) {
    c.drop(callback);
  }

  @Override
  public void drop(ClientSession clientSession, SingleResultCallback<Void> callback) {
    c.drop(clientSession, callback);
  }

  @Override
  public void createIndex(Bson key, SingleResultCallback<String> callback) {
    c.createIndex(key, callback);
  }

  @Override
  public void createIndex(Bson key, IndexOptions options, SingleResultCallback<String> callback) {
    c.createIndex(key, options, callback);
  }

  @Override
  public void createIndex(
      ClientSession clientSession, Bson key, SingleResultCallback<String> callback) {
    c.createIndex(clientSession, key, callback);
  }

  @Override
  public void createIndex(
      ClientSession clientSession,
      Bson key,
      IndexOptions options,
      SingleResultCallback<String> callback) {
    c.createIndex(clientSession, key, options, callback);
  }

  @Override
  public void createIndexes(List<IndexModel> indexes, SingleResultCallback<List<String>> callback) {
    c.createIndexes(indexes, callback);
  }

  @Override
  public void createIndexes(
      List<IndexModel> indexes,
      CreateIndexOptions createIndexOptions,
      SingleResultCallback<List<String>> callback) {
    c.createIndexes(indexes, createIndexOptions, callback);
  }

  @Override
  public void createIndexes(
      ClientSession clientSession,
      List<IndexModel> indexes,
      SingleResultCallback<List<String>> callback) {
    c.createIndexes(clientSession, indexes, callback);
  }

  @Override
  public void createIndexes(
      ClientSession clientSession,
      List<IndexModel> indexes,
      CreateIndexOptions createIndexOptions,
      SingleResultCallback<List<String>> callback) {
    c.createIndexes(clientSession, indexes, createIndexOptions, callback);
  }

  @Override
  public ListIndexesIterable<Document> listIndexes() {
    return c.listIndexes();
  }

  @Override
  public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
    return c.listIndexes(tResultClass);
  }

  @Override
  public ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
    return c.listIndexes(clientSession);
  }

  @Override
  public <TResult> ListIndexesIterable<TResult> listIndexes(
      ClientSession clientSession, Class<TResult> tResultClass) {
    return c.listIndexes(clientSession, tResultClass);
  }

  @Override
  public void dropIndex(String indexName, SingleResultCallback<Void> callback) {
    c.dropIndex(indexName, callback);
  }

  @Override
  public void dropIndex(
      String indexName, DropIndexOptions dropIndexOptions, SingleResultCallback<Void> callback) {
    c.dropIndex(indexName, dropIndexOptions, callback);
  }

  @Override
  public void dropIndex(Bson keys, SingleResultCallback<Void> callback) {
    c.dropIndex(keys, callback);
  }

  @Override
  public void dropIndex(
      Bson keys, DropIndexOptions dropIndexOptions, SingleResultCallback<Void> callback) {
    c.dropIndex(keys, dropIndexOptions, callback);
  }

  @Override
  public void dropIndex(
      ClientSession clientSession, String indexName, SingleResultCallback<Void> callback) {
    c.dropIndex(clientSession, indexName, callback);
  }

  @Override
  public void dropIndex(
      ClientSession clientSession,
      String indexName,
      DropIndexOptions dropIndexOptions,
      SingleResultCallback<Void> callback) {
    c.dropIndex(clientSession, indexName, dropIndexOptions, callback);
  }

  @Override
  public void dropIndex(
      ClientSession clientSession, Bson keys, SingleResultCallback<Void> callback) {
    c.dropIndex(clientSession, keys, callback);
  }

  @Override
  public void dropIndex(
      ClientSession clientSession,
      Bson keys,
      DropIndexOptions dropIndexOptions,
      SingleResultCallback<Void> callback) {
    c.dropIndex(clientSession, keys, dropIndexOptions, callback);
  }

  @Override
  public void dropIndexes(SingleResultCallback<Void> callback) {
    c.dropIndexes(callback);
  }

  @Override
  public void dropIndexes(DropIndexOptions dropIndexOptions, SingleResultCallback<Void> callback) {
    c.dropIndexes(dropIndexOptions, callback);
  }

  @Override
  public void dropIndexes(ClientSession clientSession, SingleResultCallback<Void> callback) {
    c.dropIndexes(clientSession, callback);
  }

  @Override
  public void dropIndexes(
      ClientSession clientSession,
      DropIndexOptions dropIndexOptions,
      SingleResultCallback<Void> callback) {
    c.dropIndexes(clientSession, dropIndexOptions, callback);
  }

  @Override
  public void renameCollection(
      MongoNamespace newCollectionNamespace, SingleResultCallback<Void> callback) {
    c.renameCollection(newCollectionNamespace, callback);
  }

  @Override
  public void renameCollection(
      MongoNamespace newCollectionNamespace,
      RenameCollectionOptions options,
      SingleResultCallback<Void> callback) {
    c.renameCollection(newCollectionNamespace, options, callback);
  }

  @Override
  public void renameCollection(
      ClientSession clientSession,
      MongoNamespace newCollectionNamespace,
      SingleResultCallback<Void> callback) {
    c.renameCollection(clientSession, newCollectionNamespace, callback);
  }

  @Override
  public void renameCollection(
      ClientSession clientSession,
      MongoNamespace newCollectionNamespace,
      RenameCollectionOptions options,
      SingleResultCallback<Void> callback) {
    c.renameCollection(clientSession, newCollectionNamespace, options, callback);
  }
}
