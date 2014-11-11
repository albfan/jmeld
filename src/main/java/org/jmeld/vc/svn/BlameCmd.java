package org.jmeld.vc.svn;

import org.jmeld.util.*;
import org.jmeld.vc.*;

import java.io.*;

public class BlameCmd
    extends SvnXmlCmd<BlameData>
{
  private File file;

  public BlameCmd(File file)
  {
    super(BlameData.class);

    this.file = file;
  }

  public Result execute()
  {
    super.execute("svn", "blame", "--non-interactive", "--xml", file.getPath());

    return getResult();
  }

  public BlameIF getBlame()
  {
    return getResultData();
  }

  public static void main(String[] args)
  {
    BlameCmd cmd;

    File file = parseFile(args);
    if (file == null) {
      return;
    }

    cmd = new BlameCmd(file);
    if (cmd.execute().isTrue())
    {
      for (BlameIF.TargetIF target : cmd.getBlame().getTargetList())
      {
        for (BlameIF.EntryIF entry : target.getEntryList())
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
