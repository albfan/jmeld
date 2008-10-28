package org.jmeld.util.vc;

import java.io.*;
import java.util.*;

public class StatusResult
{
  private File path;
  private List<Entry> entryList = new ArrayList<Entry>();

  public StatusResult(File path)
  {
    this.path = path;
  }

  public File getPath()
  {
    return path;
  }

  public Entry addEntry(String name, Status status)
  {
    Entry entry;

    entry = new Entry(name, status);
    entryList.add(entry);

    return entry;
  }

  public List<Entry> getEntryList()
  {
    return entryList;
  }

  public class Entry
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
