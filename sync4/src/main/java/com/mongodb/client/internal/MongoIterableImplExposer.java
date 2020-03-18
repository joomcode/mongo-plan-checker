package com.mongodb.client.internal;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.client.ClientSession;
import com.mongodb.lang.Nullable;

public class MongoIterableImplExposer<TResult> {
  private final MongoIterableImpl<TResult> impl;

  public MongoIterableImplExposer(MongoIterableImpl<TResult> impl) {
    this.impl = impl;
  }

  @Nullable
  public ClientSession getClientSession() {
    return impl.getClientSession();
  }

  public OperationExecutor getExecutor() {
    return impl.getExecutor();
  }

  public ReadPreference getReadPreference() {
    return impl.getReadPreference();
  }

  public ReadConcern getReadConcern() {
    return impl.getReadConcern();
  }
}
