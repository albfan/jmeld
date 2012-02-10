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
package org.jmeld.ui.search;

public class SearchHit
{
  int line;
  int fromOffset;
  int toOffset;
  int size;

  public SearchHit(int line, int offset, int size)
  {
    this.line = line;
    this.fromOffset = offset;
    this.size = size;
    this.toOffset = offset + size;
  }

  public int getLine()
  {
    return line;
  }

  public int getFromOffset()
  {
    return fromOffset;
  }

  public int getSize()
  {
    return size;
  }

  public int getToOffset()
  {
    return toOffset;
  }

  public boolean equals(Object o)
  {
    SearchHit sh;

    if (!(o instanceof SearchHit))
    {
      return false;
    }

    sh = (SearchHit) o;

    return (sh.getFromOffset() == getFromOffset() && sh.getToOffset() == getToOffset());
  }
}
