package org.jmeld.vc.hg;

import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
    extends VcCmd<StatusResult>
{
  private File file;

  public StatusCmd(File file)
  {
    this.file = file;

    initWorkingDirectory(file);
  }

  public Result execute()
  {
    super.execute("hg", "status", "-m", "-a", "-r", "-d", "-c", "-u",
      "--noninteractive", file.getAbsolutePath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    StatusResult statusResult;
    StatusResult.Status status;
    BufferedReader reader;
    String text;

    statusResult = new StatusResult(file);

    reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        data)));
    try
    {
      while ((text = reader.readLine()) != null)
      {
        if (text.length() < 3)
        {
          continue;
        }

        status = null;
        switch (text.charAt(0))
        {
          case 'M':
            status = StatusResult.Status.modified;
            break;
          case 'A':
            status = StatusResult.Status.added;
            break;
          case 'R':
            status = StatusResult.Status.removed;
            break;
          case 'C':
            status = StatusResult.Status.clean;
            break;
          case '!':
            status = StatusResult.Status.missing;
            break;
          case '?':
            status = StatusResult.Status.unversioned;
            break;
          case 'I':
            status = StatusResult.Status.ignored;
            break;
          case ' ':
            status = StatusResult.Status.clean;
            break;
        }

        statusResult.addEntry(text.substring(2), status);
      }
    }
    catch (IOException ex)
    {
      // This cannot happen! We are reading from a byte array.
    }

    setResultData(statusResult);
  }

  public static void main(String[] args)
  {
    StatusCmd cmd;
    StatusResult result;

    File file = parseFile(args);
    if (file == null) {
      return;
    }

    result = new MercurialVersionControl().executeStatus(file);
    if (result != null)
    {
      for (StatusResult.Entry entry : result.getEntryList())
      {
        System.out.println(entry.getStatus().getShortText() + " "
                           + entry.getName());
      }
    }
  }
}
