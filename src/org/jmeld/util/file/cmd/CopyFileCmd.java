package org.jmeld.util.file.cmd;

import org.jmeld.util.file.*;

import java.io.*;
import java.util.*;

public class CopyFileCmd
       extends AbstractCmd
{
  private File fromFile;
  private File toFile;

  public CopyFileCmd(
    File fromFile,
    File toFile)
    throws Exception
  {
    this.fromFile = fromFile.getCanonicalFile();
    this.toFile = toFile.getCanonicalFile();
  }

  public void createCommands()
    throws Exception
  {
    List<File> parentFiles;

    parentFiles = FileUtil.getParentFiles(toFile);
    Collections.reverse(parentFiles);
    for (File parentFile : parentFiles)
    {
      if (!parentFile.exists())
      {
        addCommand(new MkDirCommand(parentFile));
        System.out.println("mkdir " + parentFile);
      }
    }
    addCommand(new CopyCommand(fromFile, toFile));
  }

  class MkDirCommand
         extends Command
  {
    private File dirFile;

    MkDirCommand(File dirFile)
      throws Exception
    {
      this.dirFile = dirFile;
    }

    public void execute()
      throws Exception
    {
      if (debug)
      {
        System.out.println("mkdir : " + dirFile);
      }
      if (!dryrun)
      {
        dirFile.mkdir();
      }
    }

    public void undo()
    {
      if (debug)
      {
        System.out.println("rmdir : " + dirFile);
      }
      if (!dryrun)
      {
        dirFile.delete();
      }
    }
  }

  class CopyCommand
         extends Command
  {
    private File    fromFile;
    private File    toFile;
    private File    backupFile;
    private boolean toFileExists;

    CopyCommand(
      File fromFile,
      File toFile)
    {
      this.fromFile = fromFile;
      this.toFile = toFile;
    }

    public void execute()
      throws Exception
    {
      if (toFile.exists())
      {
        toFileExists = true;

        backupFile = FileUtil.createTempFile("jmeld", "backup");

        if (debug)
        {
          System.out.println("copy : " + toFile + " -> " + backupFile);
        }

        FileUtil.copy(toFile, backupFile);
      }

      if (debug)
      {
        System.out.println("copy : " + fromFile + " -> " + toFile);
      }

      FileUtil.copy(fromFile, toFile);
    }

    public void undo()
    {
      try
      {
        if (toFileExists)
        {
          if (backupFile != null)
          {
            if (debug)
            {
              System.out.println("copy : " + backupFile + " -> " + toFile);
            }

            FileUtil.copy(backupFile, toFile);
            backupFile.delete();
            backupFile = null;
          }
        }
        else
        {
          if (debug)
          {
            System.out.println("delete : " + toFile);
          }

          toFile.delete();
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    public void discard()
    {
      if (backupFile != null)
      {
        if (debug)
        {
          System.out.println("delete : " + backupFile);
        }

        backupFile.delete();
      }
    }
  }
}
