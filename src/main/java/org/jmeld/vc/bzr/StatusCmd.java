package org.jmeld.vc.bzr;

import org.jmeld.vc.*;
import org.jmeld.vc.git.GitVersionControl;
import org.jmeld.vc.util.*;

import org.jmeld.util.*;

import java.io.*;

public class StatusCmd
    extends VcCmd<StatusResult>
{
  private enum Phase
  {
    state,
    inventory;
  }

  private File file;
  private Phase phase;

  public StatusCmd(File file)
  {
    this.file = file;

    initWorkingDirectory(file);
  }

  public Result execute()
  {
    phase = Phase.state;
    super.execute("bzr", "status", "-S", file.getAbsolutePath());

    phase = Phase.inventory;
    super.execute("bzr", "inventory", file.getAbsolutePath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    StatusResult statusResult;
    StatusResult.Status status;
    BufferedReader reader;
    String text;

    if (phase == Phase.state)
    {
      statusResult = new StatusResult(file);
    }
    else
    {
      statusResult = getResultData();
    }

    reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
        data)));
    try
    {
      while ((text = reader.readLine()) != null)
      {
        if (phase == Phase.state)
        {
          if (text.length() < 5)
          {
            continue;
          }

          status = null;
          switch (text.charAt(0))
          {
            case '+':
              switch (text.charAt(1))
              {
                case 'M':
                case 'K':
                  status = StatusResult.Status.modified;
                  break;
                case 'N':
                  status = StatusResult.Status.added;
                  break;
                case 'D':
                  status = StatusResult.Status.removed;
                  break;
              }
              break;
            case ' ':
              switch (text.charAt(1))
              {
                case 'D':
                  status = StatusResult.Status.missing;
                  break;
              }
              break;
            case '-':
              status = StatusResult.Status.ignored;
              break;
            case '?':
              status = StatusResult.Status.unversioned;
              break;
          }

          statusResult.addEntry(text.substring(4), status);
        }
        else if (phase == Phase.inventory)
        {
          status = StatusResult.Status.clean;
          statusResult.addEntry(text, status);
        }
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

    result = new BazaarVersionControl().executeStatus(file);
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
