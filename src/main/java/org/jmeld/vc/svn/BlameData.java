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

import org.jmeld.vc.*;

import javax.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "blame")
public class BlameData
    implements BlameIF
{
  @XmlElement(name = "target")
  private List<Target> targetList;

  public BlameData()
  {
    targetList = new ArrayList<Target>();
  }

  public List<Target> getTargetList()
  {
    return targetList;
  }

  @XmlAccessorType(XmlAccessType.NONE)
  static class Target
      implements BlameIF.TargetIF
  {
    @XmlAttribute
    private String path;
    @XmlElement(name = "entry")
    private List<Entry> entryList;

    public Target()
    {
    }

    public String getPath()
    {
      return path;
    }

    public List<Entry> getEntryList()
    {
      return entryList;
    }
  }

  static class Entry
      implements BlameIF.EntryIF
  {
    @XmlAttribute(name = "line-number")
    private Integer lineNumber;
    @XmlElement
    private Commit commit;

    public Entry()
    {
    }

    public Integer getLineNumber()
    {
      return lineNumber;
    }

    public Commit getCommit()
    {
      return commit;
    }
  }

  static class Commit
      implements BlameIF.CommitIF
  {
    @XmlAttribute
    private Integer revision;
    @XmlElement
    private String author;
    @XmlElement
    private String date;

    public Commit()
    {
    }

    public Integer getRevision()
    {
      return revision;
    }

    public String getAuthor()
    {
      return author;
    }

    public String getDate()
    {
      return date;
    }
  }
}
