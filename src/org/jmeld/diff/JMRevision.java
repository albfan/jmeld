package org.jmeld.diff;

import java.util.*;

public class JMRevision
{
  private Object[]      org;
  private Object[]      rev;
  private List<JMDelta> deltas;

  public JMRevision(
    Object[] org,
    Object[] rev)
  {
    this.org = org;
    this.rev = rev;

    deltas = new ArrayList<JMDelta>();
  }

  public void add(JMDelta delta)
  {
    deltas.add(delta);
    delta.setRevision(this);
  }

  public List<JMDelta> getDeltas()
  {
    return deltas;
  }

  public int getOrgSize()
  {
    return org.length;
  }

  public int getRevSize()
  {
    return rev.length;
  }

  public String getOriginalString(JMChunk chunk)
  {
    return getObjects(org, chunk);
  }

  public String getRevisedString(JMChunk chunk)
  {
    return getObjects(rev, chunk);
  }

  private String getObjects(
    Object[] objects,
    JMChunk  chunk)
  {
    Object[]     result;
    StringBuffer sb;
    int          end;

    if (chunk.getSize() <= 0)
    {
      return "";
    }

    sb = new StringBuffer();
    end = chunk.getAnchor() + chunk.getSize();
    for (int offset = chunk.getAnchor(); offset < end; offset++)
    {
      sb.append(objects[offset].toString());
    }

    return sb.toString();
  }
}
