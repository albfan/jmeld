/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.util.node;

public class JMeldNode
    implements Comparable<JMeldNode>
{
  // instance variables:
  private String name;
  private boolean isLeaf;
  private boolean collapsed;

  public JMeldNode(String name, boolean isLeaf)
  {
    this.name = name;
    this.isLeaf = isLeaf;
  }

  public String getName()
  {
    return name;
  }

  public void setLeaf(boolean isLeaf)
  {
    this.isLeaf = isLeaf;
  }

  public boolean isLeaf()
  {
    return isLeaf;
  }

  public boolean isCollapsed()
  {
    return collapsed;
  }

  public void setCollapsed(boolean collapsed)
  {
    this.collapsed = collapsed;
  }

  public long getSize()
  {
    return 0;
  }

  public int compareTo(JMeldNode o)
  {
    return name.compareTo(o.getName());
  }

  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof JMeldNode))
    {
      return false;
    }

    return name.equals(((JMeldNode) o).getName());
  }

  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  public void print()
  {
    System.out.println(name);
  }

  public void resetContent()
  {
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
