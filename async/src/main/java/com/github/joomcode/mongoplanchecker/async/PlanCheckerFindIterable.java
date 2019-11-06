package com.github.joomcode.mongoplanchecker.async;

import static com.github.joomcode.mongoplanchecker.core.PlanChecker.explainModifier;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.client.model.Collation;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.conversions.Bson;

@SuppressWarnings("deprecation")
public class PlanCheckerFindIterable<TDocument> implements FindIterable<TDocument> {
  private final FindIterable<TDocument> it;
  private final PlanChecker checker;
  private Bson modifiers;
  private int skip;

  PlanCheckerFindIterable(FindIterable<TDocument> it, PlanChecker checker) {
    this.it = it;
    this.checker = checker;
  }

  @Override
  public void first(SingleResultCallback<TDocument> callback) {
    check(callback, it -> it.first(callback));
  }

  @Override
  public void forEach(Block<? super TDocument> block, SingleResultCallback<Void> callback) {
    check(callback, it -> it.forEach(block, callback));
  }

  @Override
  public void batchCursor(SingleResultCallback<AsyncBatchCursor<TDocument>> callback) {
    check(callback, it -> it.batchCursor(callback));
  }

  @Override
  public <A extends Collection<? super TDocument>> void into(
      A target, SingleResultCallback<A> callback) {
    check(callback, it -> it.into(target, callback));
  }

  private <T> void check(
      SingleResultCallback<T> callback, Consumer<FindIterable<TDocument>> realOperation) {
    it.modifiers(explainModifier())
        .first(
            (result, t) -> {
              if (t != null) {
                callback.onResult(null, t);
              } else {
                Document plan = (Document) result;
                Violations violations = checker.getViolations(plan, skip);
                if (violations.any()) {
                  callback.onResult(null, new BadPlanException(plan, violations));
                } else {
                  realOperation.accept(it.modifiers(modifiers));
                }
              }
            });
  }

  @Override
  public <U> MongoIterable<U> map(Function<TDocument, U> mapper) {
    // TODO own impl
    return it.map(mapper);
  }

  @Override
  public FindIterable<TDocument> filter(Bson filter) {
    it.filter(filter);
    return this;
  }

  @Override
  public FindIterable<TDocument> limit(int limit) {
    it.limit(limit);
    return this;
  }

  @Override
  public FindIterable<TDocument> skip(int skip) {
    it.skip(skip);
    this.skip = skip;
    return this;
  }

  @Override
  public FindIterable<TDocument> maxTime(long maxTime, TimeUnit timeUnit) {
    it.maxTime(maxTime, timeUnit);
    return this;
  }

  @Override
  public FindIterable<TDocument> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    it.maxAwaitTime(maxAwaitTime, timeUnit);
    return this;
  }

  @Override
  public FindIterable<TDocument> modifiers(Bson modifiers) {
    it.modifiers(modifiers);
    this.modifiers = modifiers;
    return this;
  }

  @Override
  public FindIterable<TDocument> projection(Bson projection) {
    it.projection(projection);
    return this;
  }

  @Override
  public FindIterable<TDocument> sort(Bson sort) {
    it.sort(sort);
    return this;
  }

  @Override
  public FindIterable<TDocument> noCursorTimeout(boolean noCursorTimeout) {
    it.noCursorTimeout(noCursorTimeout);
    return this;
  }

  @Override
  public FindIterable<TDocument> oplogReplay(boolean oplogReplay) {
    it.oplogReplay(oplogReplay);
    return this;
  }

  @Override
  public FindIterable<TDocument> partial(boolean partial) {
    it.partial(partial);
    return this;
  }

  @Override
  public FindIterable<TDocument> cursorType(CursorType cursorType) {
    it.cursorType(cursorType);
    return this;
  }

  @Override
  public FindIterable<TDocument> batchSize(int batchSize) {
    it.batchSize(batchSize);
    return this;
  }

  @Override
  public Integer getBatchSize() {
    return it.getBatchSize();
  }

  @Override
  public FindIterable<TDocument> collation(Collation collation) {
    it.collation(collation);
    return this;
  }

  @Override
  public FindIterable<TDocument> comment(String comment) {
    it.comment(comment);
    return this;
  }

  @Override
  public FindIterable<TDocument> hint(Bson hint) {
    it.hint(hint);
    return this;
  }

  @Override
  public FindIterable<TDocument> max(Bson max) {
    it.max(max);
    return this;
  }

  @Override
  public FindIterable<TDocument> min(Bson min) {
    it.min(min);
    return this;
  }

  @Override
  public FindIterable<TDocument> maxScan(long maxScan) {
    it.maxScan(maxScan);
    return this;
  }

  @Override
  public FindIterable<TDocument> returnKey(boolean returnKey) {
    it.returnKey(returnKey);
    return this;
  }

  @Override
  public FindIterable<TDocument> showRecordId(boolean showRecordId) {
    it.showRecordId(showRecordId);
    return this;
  }

  @Override
  public FindIterable<TDocument> snapshot(boolean snapshot) {
    it.snapshot(snapshot);
    return this;
  }
}
