package com.joom.mongoplanchecker.core;

import java.util.Objects;

public class Violations {
  public static final Violations EMPTY = new Violations(false, false, 0, 0);

  public final boolean broadcast;
  public final boolean excessRead;
  public final int collscans;
  public final int sorts;

  public Violations(boolean broadcast, boolean excessRead, int collscans, int sorts) {
    this.collscans = collscans;
    this.broadcast = broadcast;
    this.sorts = sorts;
    this.excessRead = excessRead;
  }

  public boolean any() {
    return broadcast || excessRead || collscans > 0 || sorts > 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Violations that = (Violations) o;
    return broadcast == that.broadcast
        && excessRead == that.excessRead
        && collscans == that.collscans
        && sorts == that.sorts;
  }

  @Override
  public int hashCode() {
    return Objects.hash(broadcast, excessRead, collscans, sorts);
  }

  @Override
  public String toString() {
    return "Violations{"
        + "broadcast="
        + broadcast
        + ", excessRead="
        + excessRead
        + ", collscans="
        + collscans
        + ", sorts="
        + sorts
        + '}';
  }

  static class Builder {
    boolean broadcast;
    boolean excessRead;
    int collscans;
    int sorts;

    Violations build() {
      return new Violations(broadcast, excessRead, collscans, sorts);
    }
  }
}
