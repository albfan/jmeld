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
package org.jmeld.diff;

public class JMChunk
{
  private int anchor;
  private int size;

  public JMChunk(int anchor, int size)
  {
    this.anchor = anchor;
    this.size = size;
  }

  void setAnchor(int anchor)
  {
    this.anchor = anchor;
  }

  public int getAnchor()
  {
    return anchor;
  }

  void setSize(int size)
  {
    this.size = size;
  }

  public int getSize()
  {
    return size;
  }

  public boolean equals(Object o)
  {
    JMChunk c;

    if (!(o instanceof JMChunk))
    {
      return false;
    }

    c = (JMChunk) o;

    return c.size == size && c.anchor == anchor;
  }

  public String toString()
  {
    return "anchor=" + anchor + ",size=" + size;
  }
}
