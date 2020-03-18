package com.mongodb.reactivestreams.client.internal;

import com.mongodb.internal.async.client.AsyncFindIterable;
import com.mongodb.reactivestreams.client.FindPublisher;
import java.lang.reflect.Field;

public class FindPublisherImplExposer {

  private static final Field WRAPPED_FIELD;

  static {
    try {
      WRAPPED_FIELD = FindPublisherImpl.class.getDeclaredField("wrapped");
      WRAPPED_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  public static <TResult> AsyncFindIterable<TResult> getAsyncFindIterable(
      FindPublisher<TResult> impl) {
    try {
      return (AsyncFindIterable<TResult>) WRAPPED_FIELD.get(impl);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
