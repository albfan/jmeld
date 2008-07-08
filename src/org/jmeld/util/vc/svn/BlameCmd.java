package org.jmeld.util.vc.svn;

import org.jmeld.util.*;
import org.jmeld.util.vc.*;

import java.io.*;

public class BlameCmd
       extends SvnXmlCmd<BlameData>
{
  private File file;

  public BlameCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    return super.execute(
      BlameData.class,
      "svn",
      "blame",
      "--non-interactive",
      "--xml",
      file.getPath());
  }

  public BlameData getBlameData()
  {
    return getResultData();
  }

  public BlameIF getBlame()
  {
    return getBlameData();
  }

  public static void main(String[] args)
  {
    BlameCmd cmd;

    cmd = new BlameCmd(new File(args[0]));
    if (cmd.execute().isTrue())
    {
      for (BlameData.Target target : cmd.getBlameData().getTargetList())
      {
        for (BlameData.Entry entry : target.getEntryList())
        {
          System.out.println(entry.getLineNumber() + " : "
            + entry.getCommit().getRevision() + " -> "
            + entry.getCommit().getAuthor());
        }
      }
    }
    else
    {
      cmd.printError();
    }
  }
}
