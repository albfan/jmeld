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

import java.util.ArrayList;
import java.util.List;

public class SearchHits
{
  private List<SearchHit> searchHits;
  private SearchHit current;

  public SearchHits()
  {
    searchHits = new ArrayList<SearchHit>();
  }

  public void add(SearchHit sh)
  {
    searchHits.add(sh);
    if (getCurrent() == null)
    {
      setCurrent(sh);
    }
  }

  public List<SearchHit> getSearchHits()
  {
    return searchHits;
  }

  public boolean isCurrent(SearchHit sh)
  {
    return sh.equals(getCurrent());
  }

  public SearchHit getCurrent()
  {
    return current;
  }

  private void setCurrent(SearchHit sh)
  {
    current = sh;
  }

  public void next()
  {
    int index;

    index = searchHits.indexOf(getCurrent());
    index++;

    if (index >= searchHits.size())
    {
      index = 0;
    }

    if (index >= 0 && index < searchHits.size())
    {
      setCurrent(searchHits.get(index));
    }
  }

  public void previous()
  {
    int index;

    index = searchHits.indexOf(getCurrent());
    index--;

    if (index < 0)
    {
      index = searchHits.size() - 1;
    }

    if (index >= 0 && index < searchHits.size())
    {
      setCurrent(searchHits.get(index));
    }
  }
}
