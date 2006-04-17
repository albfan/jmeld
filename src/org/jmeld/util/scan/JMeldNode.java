package org.jmeld.util.scan;

import java.io.*;
import java.util.*;

public class JMeldNode
       implements Comparable<JMeldNode>
{
  // class variables:
  public static final char EQUAL = 'E';
  public static final char ADDED = 'A';
  public static final char CHANGED = 'C';
  public static final char DELETED = 'D';

  // instance variables:
  private String  name;
  private boolean isLeaf;
  private char    state;

  public JMeldNode(String name, boolean isLeaf)
  {
    this.name = name;
    this.isLeaf = isLeaf;

    state = EQUAL;
  }

  public String getName()
  {
    return name;
  }

  public boolean isLeaf()
  {
    return isLeaf;
  }

  public void setState(char state)
  {
    this.state = state;
  }

  public char getState()
  {
    return state;
  }

  public int compareTo(JMeldNode o)
  {
    return name.compareTo(o.getName());
  }

  public boolean equals(Object o)
  {
    return name.equals(((JMeldNode) o).getName());
  }

  public int hashCode()
  {
    return name.hashCode();
  }

  public void print()
  {
    System.out.println(name + (state != 0 ? (" [" + state + "]") : ""));
  }

  public boolean contentEquals(JMeldNode node)
  {
    return true;
  }

  public String toString()
  {
    return getName();
  }
}
