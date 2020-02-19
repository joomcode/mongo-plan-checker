package com.mongodb.internal.async.client;

import com.mongodb.internal.async.AsyncBatchCursor;
import com.mongodb.internal.operation.AsyncReadOperation;
import com.mongodb.internal.operation.FindOperation;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.internal.FindPublisherImplExposer;
import com.mongodb.reactivestreams.client.internal.Publishers;
import org.bson.BsonDocument;
import org.reactivestreams.Subscriber;

public class Explainer {

  public static <TResult> void explain(FindPublisher<TResult> p, Subscriber<BsonDocument> s) {
    AsyncMongoIterableImpl<TResult> impl =
        (AsyncMongoIterableImpl<TResult>) FindPublisherImplExposer.getAsyncFindIterable(p);
    AsyncReadOperation<AsyncBatchCursor<TResult>> readOperation = impl.asAsyncReadOperation();
    FindOperation<TResult> findOPeration = (FindOperation<TResult>) readOperation;
    AsyncReadOperation<BsonDocument> explainOperation =
        (AsyncReadOperation<BsonDocument>) findOPeration.asExplainableOperation();

    Publishers.<BsonDocument>publish(
            callback ->
                impl.getExecutor()
                    .execute(
                        explainOperation,
                        impl.getReadPreference(),
                        impl.getReadConcern(),
                        impl.getClientSession(),
                        callback))
        .subscribe(s);
  }
}
