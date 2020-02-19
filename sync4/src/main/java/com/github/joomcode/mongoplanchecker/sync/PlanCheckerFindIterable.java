package com.github.joomcode.mongoplanchecker.sync;

import com.github.joomcode.mongoplanchecker.core.BadPlanException;
import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.github.joomcode.mongoplanchecker.core.Violations;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.internal.MongoIterableImpl;
import com.mongodb.client.internal.MongoIterableImplExposer;
import com.mongodb.client.model.Collation;
import com.mongodb.internal.operation.FindOperation;
import com.mongodb.internal.operation.ReadOperation;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

public class PlanCheckerFindIterable<TResult> implements FindIterable<TResult> {

  private final FindIterable<TResult> iterable;
  private final PlanChecker checker;
  private int skip;

  public PlanCheckerFindIterable(FindIterable<TResult> iterable, PlanChecker checker) {
    this.iterable = iterable;
    this.checker = checker;
  }

  @Override
  public FindIterable<TResult> skip(int skip) {
    this.skip = skip;
    iterable.skip(skip);
    return this;
  }

  @Override
  public MongoCursor<TResult> iterator() {
    check();
    return iterable.iterator();
  }

  @Override
  public MongoCursor<TResult> cursor() {
    check();
    return iterable.cursor();
  }

  void check() {
    if (!(this.iterable instanceof MongoIterableImpl)) {
      throw new IllegalStateException("");
    }
    MongoIterableImpl<TResult> iterableImpl = (MongoIterableImpl<TResult>) this.iterable;
    MongoIterableImplExposer<TResult> exposer = new MongoIterableImplExposer<>(iterableImpl);

    ReadOperation<BsonDocument> explainOperation =
        ((FindOperation<TResult>) iterableImpl.asReadOperation()).asExplainableOperation();
    BsonDocument plan =
        exposer
            .getExecutor()
            .execute(
                explainOperation,
                exposer.getReadPreference(),
                exposer.getReadConcern(),
                exposer.getClientSession());

    Violations violations = checker.getViolations(plan, skip);
    if (violations.any()) {
      throw new BadPlanException(plan, violations);
    }
  }

  @Override
  public TResult first() {
    check();
    return iterable.first();
  }

  @Override
  public <U> MongoIterable<U> map(Function<TResult, U> mapper) {
    // TODO own impl
    return iterable.map(mapper);
  }

  @Override
  public <A extends Collection<? super TResult>> A into(A target) {
    check();
    return iterable.into(target);
  }

  @Override
  public FindIterable<TResult> filter(Bson filter) {
    iterable.filter(filter);
    return this;
  }

  @Override
  public FindIterable<TResult> limit(int limit) {
    iterable.limit(limit);
    return this;
  }

  @Override
  public FindIterable<TResult> maxTime(long maxTime, TimeUnit timeUnit) {
    iterable.maxTime(maxTime, timeUnit);
    return this;
  }

  @Override
  public FindIterable<TResult> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    iterable.maxAwaitTime(maxAwaitTime, timeUnit);
    return this;
  }

  @Override
  public FindIterable<TResult> projection(Bson projection) {
    iterable.projection(projection);
    return this;
  }

  @Override
  public FindIterable<TResult> sort(Bson sort) {
    iterable.sort(sort);
    return this;
  }

  @Override
  public FindIterable<TResult> noCursorTimeout(boolean noCursorTimeout) {
    iterable.noCursorTimeout(noCursorTimeout);
    return this;
  }

  @Override
  public FindIterable<TResult> oplogReplay(boolean oplogReplay) {
    iterable.oplogReplay(oplogReplay);
    return this;
  }

  @Override
  public FindIterable<TResult> partial(boolean partial) {
    iterable.partial(partial);
    return this;
  }

  @Override
  public FindIterable<TResult> cursorType(CursorType cursorType) {
    iterable.cursorType(cursorType);
    return this;
  }

  @Override
  public FindIterable<TResult> batchSize(int batchSize) {
    iterable.batchSize(batchSize);
    return this;
  }

  @Override
  public FindIterable<TResult> collation(Collation collation) {
    iterable.collation(collation);
    return this;
  }

  @Override
  public FindIterable<TResult> comment(String comment) {
    iterable.comment(comment);
    return this;
  }

  @Override
  public FindIterable<TResult> hint(Bson hint) {
    iterable.hint(hint);
    return this;
  }

  @Override
  public FindIterable<TResult> hintString(String hint) {
    iterable.hintString(hint);
    return this;
  }

  @Override
  public FindIterable<TResult> max(Bson max) {
    iterable.max(max);
    return this;
  }

  @Override
  public FindIterable<TResult> min(Bson min) {
    iterable.min(min);
    return this;
  }

  @Override
  public FindIterable<TResult> returnKey(boolean returnKey) {
    iterable.returnKey(returnKey);
    return this;
  }

  @Override
  public FindIterable<TResult> showRecordId(boolean showRecordId) {
    iterable.showRecordId(showRecordId);
    return this;
  }
}
