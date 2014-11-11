package org.jmeld.vc.svn;

import org.jmeld.util.*;

import java.io.*;

public class InfoCmd
    extends SvnXmlCmd<InfoData>
{
  private File file;

  public InfoCmd(File file)
  {
    super(InfoData.class);

    this.file = file;
  }

  public Result execute()
  {
    super.execute("svn", "info", "--non-interactive", "-R", "--xml", file
        .getPath());

    return getResult();
  }

  public InfoData getInfoData()
  {
    return getResultData();
  }

  public static void main(String[] args)
  {
    InfoCmd cmd;

    File file = parseFile(args);
    if (file == null) {
      return;
    }

    cmd = new InfoCmd(file);
    if (cmd.execute().isTrue())
    {
      for (InfoData.Entry entry : cmd.getInfoData().getEntryList())
      {
        System.out.println(entry.getRevision() + " : " + entry.getPath());
      }
    }
    else
    {
      cmd.printError();
    }
  }
}
