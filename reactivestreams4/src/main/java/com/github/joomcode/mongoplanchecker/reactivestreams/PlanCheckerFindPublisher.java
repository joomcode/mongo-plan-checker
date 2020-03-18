package com.github.joomcode.mongoplanchecker.reactivestreams;

import com.github.joomcode.mongoplanchecker.core.PlanChecker;
import com.mongodb.CursorType;
import com.mongodb.client.model.Collation;
import com.mongodb.internal.async.client.Explainer;
import com.mongodb.reactivestreams.client.FindPublisher;
import java.util.concurrent.TimeUnit;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PlanCheckerFindPublisher<TResult> implements FindPublisher<TResult> {
  private final FindPublisher<TResult> p;
  private final PlanChecker checker;
  private int skip;

  public PlanCheckerFindPublisher(FindPublisher<TResult> p, PlanChecker checker) {
    this.p = p;
    this.checker = checker;
  }

  @Override
  public Publisher<TResult> first() {
    return mainSubscriber ->
        Explainer.explain(
            p,
            new PlanCheckingSubscriber<>(
                mainSubscriber, skip, checker, () -> p.first().subscribe(mainSubscriber)));
  }

  @Override
  public void subscribe(Subscriber<? super TResult> mainSubscriber) {
    Explainer.explain(
        p,
        new PlanCheckingSubscriber<>(
            mainSubscriber, skip, checker, () -> p.subscribe(mainSubscriber)));
  }

  @Override
  public PlanCheckerFindPublisher<TResult> filter(Bson filter) {
    p.filter(filter);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> limit(int limit) {
    p.limit(limit);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> skip(int skip) {
    p.skip(skip);
    this.skip = skip;
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> maxTime(long maxTime, TimeUnit timeUnit) {
    p.maxTime(maxTime, timeUnit);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
    p.maxAwaitTime(maxAwaitTime, timeUnit);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> projection(Bson projection) {
    p.projection(projection);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> sort(Bson sort) {
    p.sort(sort);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> noCursorTimeout(boolean noCursorTimeout) {
    p.noCursorTimeout(noCursorTimeout);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> oplogReplay(boolean oplogReplay) {
    p.oplogReplay(oplogReplay);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> partial(boolean partial) {
    p.partial(partial);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> cursorType(CursorType cursorType) {
    p.cursorType(cursorType);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> collation(Collation collation) {
    p.collation(collation);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> comment(String comment) {
    p.comment(comment);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> hint(Bson hint) {
    p.hint(hint);
    return this;
  }

  @Override
  public FindPublisher<TResult> hintString(String hint) {
    p.hintString(hint);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> max(Bson max) {
    p.max(max);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> min(Bson min) {
    p.min(min);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> returnKey(boolean returnKey) {
    p.returnKey(returnKey);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> showRecordId(boolean showRecordId) {
    p.showRecordId(showRecordId);
    return this;
  }

  @Override
  public PlanCheckerFindPublisher<TResult> batchSize(int batchSize) {
    p.batchSize(batchSize);
    return this;
  }
}
