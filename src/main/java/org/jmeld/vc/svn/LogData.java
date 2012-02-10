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

import javax.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "log")
public class LogData
{
  @XmlElement(name = "logentry")
  private List<Entry> entryList;

  public LogData()
  {
    entryList = new ArrayList<Entry>();
  }

  public List<Entry> getEntryList()
  {
    return entryList;
  }

  @XmlAccessorType(XmlAccessType.NONE)
  static class Entry
  {
    @XmlAttribute
    private Integer revision;
    @XmlElement
    private String author;
    @XmlElement
    private Date date;
    @XmlElementWrapper(name = "paths")
    @XmlElement(name = "path")
    private List<Path> pathList;
    @XmlElement
    private String msg;

    public Entry()
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

    public Date getDate()
    {
      return date;
    }

    public String getMsg()
    {
      return msg;
    }

    public List<Path> getPathList()
    {
      return pathList;
    }
  }

  static class Path
  {
    @XmlAttribute
    private String action;
    @XmlAttribute(name = "copyfrom-path")
    private String copyFromPath;
    @XmlAttribute(name = "copyfrom-rev")
    private Integer copyFromRev;
    private String pathName;

    public Path()
    {
    }

    public String getAction()
    {
      return action;
    }

    @XmlValue
    public String getPathName()
    {
      return pathName;
    }

    public void setPathName(String pathName)
    {
      if (pathName != null)
      {
        pathName = pathName.trim();
      }

      this.pathName = pathName;
    }
  }
}
