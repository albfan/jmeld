package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.util.VcCmd;

import java.io.*;

public class StatusCmd
    extends VcCmd<StatusResult>
{
  private enum Phase
  {
    state,
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
    super.execute("git", "status", "-s", file.getAbsolutePath());

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
          char indextree = text.charAt(0);
            //TODO: interpret worktree status
          char worktree = text.charAt(1);
          switch (indextree)
          {
            case 'M':
              status = StatusResult.Status.modified;
              break;
            case 'A':
              status = StatusResult.Status.added;
              break;
            case 'D':
              status = StatusResult.Status.removed;
              break;
          case 'R':
              status = StatusResult.Status.renamed;
              break;
          case 'U':
              status = StatusResult.Status.updated;
              break;
          case 'C':
              status = StatusResult.Status.copied;
              break;
            case ' ':
              status = StatusResult.Status.unmodified;
              break;
            case '!':
              status = StatusResult.Status.ignored;
              break;
            case '?':
              status = StatusResult.Status.unversioned;
              break;
          }

          statusResult.addEntry(text.substring(3), status);
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

    result = new GitVersionControl().executeStatus(file);
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
