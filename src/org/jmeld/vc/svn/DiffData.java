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
package org.jmeld.vc.svn;

import org.jmeld.diff.*;
import org.jmeld.vc.*;

import java.util.*;

public class DiffData
    implements DiffIF
{
  private List<Target> targetList;

  public DiffData()
  {
    targetList = new ArrayList<Target>();
  }

  public void addTarget(String path, JMRevision revision)
  {
    targetList.add(new Target(path, revision));
  }

  public List<Target> getTargetList()
  {
    return targetList;
  }

  static class Target
      implements DiffIF.TargetIF
  {
    private String path;
    private JMRevision revision;

    public Target(String path, JMRevision revision)
    {
      this.path = path;
      this.revision = revision;
    }

    public String getPath()
    {
      return path;
    }

    public JMRevision getRevision()
    {
      return revision;
    }
  }
}
