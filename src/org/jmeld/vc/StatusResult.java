package org.jmeld.vc;

import java.io.*;
import java.util.*;

public class StatusResult
{
  private File path;
  private Set<Entry> entryList = new HashSet<Entry>();

  public StatusResult(File path)
  {
    this.path = path;
  }

  public File getPath()
  {
    return path;
  }

  public void addEntry(String name, Status status)
  {
    Entry entry;

    entry = new Entry(name, status);
    if (entryList.contains(entry))
    {
      return;
    }

    entryList.add(entry);
  }

  public List<Entry> getEntryList()
  {
    List<Entry> list;

    list = new ArrayList(entryList);
    Collections.sort(list);

    return list;
  }

  public class Entry
      implements Comparable<Entry>
  {
    private String name;
    private Status status;

    Entry(String name, Status status)
    {
      this.name = name;
      this.status = status;
    }

    public String getName()
    {
      return name;
    }

    public Status getStatus()
    {
      return status;
    }

    public int compareTo(Entry entry)
    {
      return name.compareTo(entry.name);
    }

    public String toString()
    {
      return name;
    }

    public boolean equals(Object o)
    {
      if (!(o instanceof Entry))
      {
        return false;
      }

      return name.equals(((Entry) o).name);
    }

    public int hashCode()
    {
      return name.hashCode();
    }
  }

  public enum Status
  {
    modified('M'),
    added('A'),
    removed('D'),
    clean(' '),
    conflicted('C'),
    ignored('I'),
    unversioned('?'),
    missing('!'),
    dontknow('#');

    private char shortText;

    Status(char shortText)
    {
      this.shortText = shortText;
    }

    public char getShortText()
    {
      return shortText;
    }
  }
}
