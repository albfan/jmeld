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
    private File fromFile;
    private File toFile;
    private File originalFile;

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
        if (!dryrun)
        {
          originalFile = FileUtil.createTempFile("jmeld", "backup");
        }

        if (debug)
        {
          System.out.println("copy : " + toFile + " -> " + originalFile);
        }
        if (!dryrun)
        {
          FileUtil.copy(toFile, originalFile);
        }
      }

      if (debug)
      {
        System.out.println("copy : " + fromFile + " -> " + toFile);
      }
      if (!dryrun)
      {
        FileUtil.copy(fromFile, toFile);
      }
    }

    public void undo()
    {
      try
      {
        if (originalFile != null)
        {
          if (debug)
          {
            System.out.println("copy : " + originalFile + " -> " + toFile);
          }
          if (!dryrun)
          {
            FileUtil.copy(originalFile, toFile);
          }
        }
        else
        {
          if (debug)
          {
            System.out.println("delete : " + toFile);
          }
          if (!dryrun)
          {
            toFile.delete();
          }
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    public void discard()
    {
      if (originalFile != null)
      {
        if (debug)
        {
          System.out.println("delete : " + originalFile);
        }
        if (!dryrun)
        {
          originalFile.delete();
        }
      }
    }
  }
}
