package org.jmeld.vc;

import java.util.*;

public interface BlameIF
{
  public List<? extends TargetIF> getTargetList();

  public interface TargetIF
  {
    public String getPath();

    public List<? extends EntryIF> getEntryList();
  }

  public interface EntryIF
  {
    public Integer getLineNumber();

    public CommitIF getCommit();
  }

  public interface CommitIF
  {
    public Integer getRevision();

    public String getAuthor();

    public String getDate();
  }
}
