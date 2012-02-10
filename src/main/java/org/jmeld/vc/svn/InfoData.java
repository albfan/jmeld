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
@XmlRootElement(name = "info")
public class InfoData
{
  @XmlElement(name = "entry")
  private List<Entry> entryList;

  public InfoData()
  {
    entryList = new ArrayList<Entry>();
  }

  public List<Entry> getEntryList()
  {
    return entryList;
  }

  static class Entry
  {
    @XmlAttribute
    private String path;
    @XmlAttribute
    private String dir;
    @XmlAttribute
    private String file;
    @XmlAttribute
    private Integer revision;
    @XmlElement
    private String url;
    @XmlElement
    private Repository repository;
    @XmlElement(name = "wc-info")
    private WcInfo wcInfo;
    @XmlElement
    private Commit commit;
    @XmlElement
    private Lock lock;

    public Entry()
    {
    }

    public String getDir()
    {
      return dir;
    }

    public String getFile()
    {
      return file;
    }

    public String getPath()
    {
      return path;
    }

    public Integer getRevision()
    {
      return revision;
    }

    public String getUrl()
    {
      return url;
    }

    public Repository getRepository()
    {
      return repository;
    }

    public WcInfo getWcInfo()
    {
      return wcInfo;
    }

    public Commit getCommit()
    {
      return commit;
    }
  }

  static class Repository
  {
    @XmlElement
    private String root;
    @XmlElement
    private String uuid;

    public Repository()
    {
    }

    public String getRoot()
    {
      return root;
    }

    public String getUUID()
    {
      return uuid;
    }
  }

  static class WcInfo
  {
    @XmlElement
    private String schedule;
    @XmlElement(name = "copy-from-url")
    private String copyFromUrl;
    @XmlElement(name = "copy-from-rev")
    private String copyFromRev;
    @XmlElement(name = "text-updated")
    private Date textUpdated;
    @XmlElement(name = "prop-updated")
    private Date propUpdated;
    @XmlElement
    private String checksum;
    @XmlElement
    private Confict conflict;

    public WcInfo()
    {
    }

    public String getSchedule()
    {
      return schedule;
    }

    public Date getTextUpdated()
    {
      return textUpdated;
    }

    public String getChecksum()
    {
      return checksum;
    }
  }

  static class Confict
  {
    @XmlElement(name = "prev-base-file")
    private String prevBaseFile;
    @XmlElement(name = "prev-wc-file")
    private String prevWcFile;
    @XmlElement(name = "cur-base-file")
    private String curBaseFile;
    @XmlElement(name = "prop-file")
    private String propFile;

    public Confict()
    {
    }
  }

  static class Commit
  {
    @XmlAttribute
    private Integer revision;
    @XmlElement
    private String author;
    @XmlElement
    private Date date;

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

    public Date getDate()
    {
      return date;
    }
  }

  static class Lock
  {
    @XmlElement
    private String token;
    @XmlElement
    private String owner;
    @XmlElement
    private String comment;
    @XmlElement
    private Date created;
    @XmlElement
    private Date expires;

    public Lock()
    {
    }
  }
}
