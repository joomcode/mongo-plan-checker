package com.joom.mongoplanchecker.sync;

import com.joom.mongoplanchecker.core.BadPlanException;
import com.joom.mongoplanchecker.core.PlanChecker;
import com.joom.mongoplanchecker.core.Violations;
import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PlanCheckerFindIterable<TResult> implements FindIterable<TResult> {

  private final FindIterable<TResult> iterable;
  private final PlanChecker checker;
  private Bson modifiers;
  private int skip;

  public PlanCheckerFindIterable(FindIterable<TResult> iterable, PlanChecker checker) {
    this.iterable = Objects.requireNonNull(iterable);
    this.checker = Objects.requireNonNull(checker);
  }

  @Override
  public FindIterable<TResult> modifiers(Bson modifiers) {
    this.modifiers = modifiers;
    return this;
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

  @SuppressWarnings("deprecation")
  private void check() {
    Document plan = (Document) iterable.modifiers(new Document("$explain", true)).first();
    Violations violations = checker.getViolations(plan, skip);
    if (violations.any()) {
      throw new BadPlanException(plan, violations);
    }
    iterable.modifiers(modifiers);
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
  @SuppressWarnings("deprecation")
  public void forEach(Block<? super TResult> block) {
    check();
    iterable.forEach(block);
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
  @SuppressWarnings("deprecation")
  public FindIterable<TResult> maxScan(long maxScan) {
    iterable.maxScan(maxScan);
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

  @Override
  @SuppressWarnings("deprecation")
  public FindIterable<TResult> snapshot(boolean snapshot) {
    iterable.snapshot(snapshot);
    return this;
  }
}
