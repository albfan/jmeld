package org.jmeld.util.file.cmd;

import org.jmeld.util.file.*;

import javax.swing.undo.*;

import java.io.*;
import java.util.*;

public class RemoveFileCmd
       extends AbstractCmd
{
  private File file;

  public RemoveFileCmd(File file)
  {
    this.file = file;
  }

  public void createCommands()
    throws Exception
  {
    addCommand(new RemoveCommand(file));
  }

  class RemoveCommand
         extends Command
  {
    private File file;
    private File originalFile;

    RemoveCommand(File file)
    {
      this.file = file;
    }

    public void execute()
      throws Exception
    {
      if (file.exists())
      {
        originalFile = FileUtil.createTempFile("jmeld", "backup");

        if (debug)
        {
          System.out.println("copy : " + file + " -> " + originalFile);
        }

        if (!dryrun)
        {
          FileUtil.copy(file, originalFile);
        }
      }

      if (debug)
      {
        System.out.println("delete : " + file);
      }
      if (!dryrun)
      {
        file.delete();
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
            System.out.println("copy : " + originalFile + " -> " + file);
          }
          if (!dryrun)
          {
            FileUtil.copy(originalFile, file);
          }
        }
      }
      catch (Exception ex)
      {
        throw new CannotUndoException();
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
