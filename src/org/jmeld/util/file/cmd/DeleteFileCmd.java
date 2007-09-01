package org.jmeld.util.file.cmd;

import org.jmeld.util.file.*;

import java.io.*;
import java.util.*;

public class DeleteFileCmd
       extends AbstractCmd
{
  private File file;

  public DeleteFileCmd(File file)
  {
    this.file = file;
  }

  public void createCommands()
    throws IOException
  {
    addCommand(new DeleteCommand(file));
  }

  class DeleteCommand
         extends Command
  {
    private File file;
    private File originalFile;

    DeleteCommand(File file)
    {
      this.file = file;
    }

    public void execute()
      throws IOException
    {
      if (file.exists())
      {
        originalFile = FileUtil.createTempFile("jmeld", "backup");

        if (debug)
        {
          System.out.println("copy : " + file + " -> " + originalFile);
        }
        FileUtil.copy(file, originalFile);
      }

      if (debug)
      {
        System.out.println("delete : " + file);
      }
      file.delete();
    }

    public void undo()
      throws IOException
    {
      if (originalFile != null)
      {
        if (debug)
        {
          System.out.println("copy : " + originalFile + " -> " + file);
        }
        FileUtil.copy(originalFile, file);
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
