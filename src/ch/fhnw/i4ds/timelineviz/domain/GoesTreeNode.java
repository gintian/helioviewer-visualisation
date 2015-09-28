package ch.fhnw.i4ds.timelineviz.domain;

import org.joda.time.Instant;

public class GoesTreeNode implements Comparable<GoesTreeNode> {

  private Long id;

  private int level;

  private GoesTreeNode parentNode;

  private TimestampInterval timestampInterval;

  private GoesSxrLeaf minLowChannelLeaf;

  private GoesSxrLeaf maxLowChannelLeaf;

  private GoesSxrLeaf minHighChannelLeaf;

  private GoesSxrLeaf maxHighChannelLeaf;

  private boolean dirty;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public GoesTreeNode getParentNode() {
    return parentNode;
  }

  public void setParentNode(GoesTreeNode parentNode) {
    this.parentNode = parentNode;
  }

  public TimestampInterval getTimestampInterval() {
    return timestampInterval;
  }

  public void setTimestampInterval(TimestampInterval timestampInterval) {
    this.timestampInterval = timestampInterval;
  }

  public GoesSxrLeaf getMinLowChannelLeaf() {
    return minLowChannelLeaf;
  }

  public void setMinLowChannelLeaf(GoesSxrLeaf minLowChannelLeaf) {
    this.minLowChannelLeaf = minLowChannelLeaf;
  }

  public GoesSxrLeaf getMaxLowChannelLeaf() {
    return maxLowChannelLeaf;
  }

  public void setMaxLowChannelLeaf(GoesSxrLeaf maxLowChannelLeaf) {
    this.maxLowChannelLeaf = maxLowChannelLeaf;
  }

  public GoesSxrLeaf getMinHighChannelLeaf() {
    return minHighChannelLeaf;
  }

  public void setMinHighChannelLeaf(GoesSxrLeaf minHighChannelLeaf) {
    this.minHighChannelLeaf = minHighChannelLeaf;
  }

  public GoesSxrLeaf getMaxHighChannelLeaf() {
    return maxHighChannelLeaf;
  }

  public void setMaxHighChannelLeaf(GoesSxrLeaf maxHighChannelLeaf) {
    this.maxHighChannelLeaf = maxHighChannelLeaf;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + level;
    result = prime * result + ((timestampInterval == null) ? 0 : timestampInterval.hashCode());
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
    if (!getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    GoesTreeNode other = (GoesTreeNode) obj;
    if (getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!getId().equals(other.getId())) {
      return false;
    }
    if (getLevel() != other.getLevel()) {
      return false;
    }
    if (getTimestampInterval() == null) {
      if (other.getTimestampInterval() != null) {
        return false;
      }
    } else if (!getTimestampInterval().equals(other.getTimestampInterval())) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "GoesTreeNode [id=" + id + ", level=" + level + ", dirty=" + dirty + ", parentNodeId=" + (parentNode != null ? parentNode.getId() : null) + ", timestampInterval=" + timestampInterval + ", minLowChannelLeaf=" + (minLowChannelLeaf != null ? minLowChannelLeaf : null) + ", maxLowChannelLeaf=" + (maxLowChannelLeaf != null ? maxLowChannelLeaf : null) + ", minHighChannelLeaf=" + (minHighChannelLeaf != null ? minHighChannelLeaf : null) + ", maxHighChannelLeaf=" + (minHighChannelLeaf != null ? maxHighChannelLeaf : null) + "]";
  }

  @Override
  public int compareTo(GoesTreeNode o) {
    Instant thisStartTimestamp = getTimestampInterval().getStartTimestamp();
    Instant otherStartTimestamp = o.getTimestampInterval().getStartTimestamp();

    if ((thisStartTimestamp != null && otherStartTimestamp == null) || (thisStartTimestamp != null) && thisStartTimestamp.isBefore(otherStartTimestamp)) {
      return -1;
    } else if ((thisStartTimestamp == null && otherStartTimestamp == null) || (thisStartTimestamp != null) && thisStartTimestamp.isEqual(otherStartTimestamp)) {
      return 0;
    } else {
      return 1;
    }
  }
}
