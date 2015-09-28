package ch.fhnw.i4ds.timelineviz.domain;

import org.joda.time.Instant;

public class GoesSxrLeaf implements Comparable<GoesSxrLeaf> {

  private Long id;

  private GoesTreeNode parentNode;

  private Instant timestamp;

  private float lowChannel;

  private float highChannel;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GoesTreeNode getParentNode() {
    return parentNode;
  }

  public void setParentNode(GoesTreeNode parentNode) {
    this.parentNode = parentNode;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public float getLowChannel() {
    return lowChannel;
  }

  public void setLowChannel(float lowChannel) {
    this.lowChannel = lowChannel;
  }

  public float getHighChannel() {
    return highChannel;
  }

  public void setHighChannel(float highChannel) {
    this.highChannel = highChannel;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
    GoesSxrLeaf other = (GoesSxrLeaf) obj;
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    } else if (!timestamp.isEqual(other.timestamp)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "GoesSxrLeaf [id=" + id + ", parentNodeId=" + (parentNode != null ? parentNode.getId() : null) + ", timestamp=" + timestamp + ", lowChannel=" + lowChannel + ", highChannel=" + highChannel + "]";
  }

  @Override
  public int compareTo(GoesSxrLeaf o) {
    if ((getTimestamp() != null && o.getTimestamp() == null) || (getTimestamp() != null) && getTimestamp().isBefore(o.getTimestamp())) {
      return -1;
    } else if ((getTimestamp() == null && o.getTimestamp() == null) || (getTimestamp() != null) && getTimestamp().isEqual(o.getTimestamp())) {
      return 0;
    } else {
      return 1;
    }
  }

}
