package ch.fhnw.i4ds.timelineviz.domain;

import org.joda.time.Duration;
import org.joda.time.Instant;

public class TimestampInterval implements Cloneable {
  private Instant startTimestamp;

  private Instant endTimestamp;

  public TimestampInterval() {
  }

  public TimestampInterval(Instant startTimestamp, Instant endTimestamp) {
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
  }

  public Instant getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(Instant startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Instant getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(Instant endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public Duration getDuration() {
    return new Duration(startTimestamp, endTimestamp);
  }

  @Override
  public TimestampInterval clone() {
    try {
      return (TimestampInterval) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endTimestamp == null) ? 0 : endTimestamp.hashCode());
    result = prime * result + ((startTimestamp == null) ? 0 : startTimestamp.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TimestampInterval other = (TimestampInterval) obj;
    if (endTimestamp == null) {
      if (other.endTimestamp != null) {
        return false;
      }
    } else if (!endTimestamp.equals(other.endTimestamp)) {
      return false;
    }
    if (startTimestamp == null) {
      if (other.startTimestamp != null) {
        return false;
      }
    } else if (!startTimestamp.equals(other.startTimestamp)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TimestampInterval [startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp + "]";
  }

}
