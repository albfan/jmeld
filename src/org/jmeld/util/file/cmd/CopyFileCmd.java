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
  {
    this.fromFile = fromFile;
    this.toFile = toFile;
  }

  public void createCommands()
    throws IOException
  {
    List<File> parentFiles;

    parentFiles = FileUtil.getParentFiles(toFile);
    Collections.reverse(parentFiles);
    for (File parentFile : parentFiles)
    {
      if (!parentFile.exists())
      {
        addCommand(new MkDirCommand(parentFile));
      }
    }
    addCommand(new CopyCommand(fromFile, toFile));
  }

  class MkDirCommand
         extends Command
  {
    private File dirFile;

    MkDirCommand(File dirFile)
    {
      this.dirFile = dirFile;
    }

    public void execute()
      throws IOException
    {
      if (debug)
      {
        System.out.println("mkdir : " + dirFile);
      }
      dirFile.mkdir();
    }

    public void undo()
      throws IOException
    {
      if (debug)
      {
        System.out.println("rmdir : " + dirFile);
      }
      dirFile.delete();
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
      throws IOException
    {
      if (toFile.exists())
      {
        originalFile = FileUtil.createTempFile("jmeld", "backup");

        if (debug)
        {
          System.out.println("copy : " + toFile + " -> " + originalFile);
        }
        FileUtil.copy(toFile, originalFile);
      }

      if (debug)
      {
        System.out.println("copy : " + fromFile + " -> " + toFile);
      }
      FileUtil.copy(fromFile, toFile);
    }

    public void undo()
      throws IOException
    {
      if (originalFile != null)
      {
        if (debug)
        {
          System.out.println("copy : " + originalFile + " -> " + toFile);
        }
        FileUtil.copy(originalFile, toFile);
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

    public void discard()
      throws IOException
    {
      if (originalFile != null)
      {
        if (debug)
        {
          System.out.println("delete : " + originalFile);
        }
        originalFile.delete();
      }
    }
  }
}
