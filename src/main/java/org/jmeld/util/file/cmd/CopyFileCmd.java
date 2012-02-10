package org.jmeld.util.file.cmd;

import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import java.io.*;
import java.util.*;

public class CopyFileCmd
    extends AbstractCmd
{
  private JMDiffNode diffNode;
  private FileNode fromFileNode;
  private FileNode toFileNode;

  public CopyFileCmd(JMDiffNode diffNode, FileNode fromFileNode,
      FileNode toFileNode)
      throws Exception
  {
    this.diffNode = diffNode;
    this.fromFileNode = fromFileNode;
    this.toFileNode = toFileNode;
  }

  public void createCommands()
      throws Exception
  {
    List<File> parentFiles;
    File fromFile;
    File toFile;

    fromFile = fromFileNode.getFile().getCanonicalFile();
    toFile = toFileNode.getFile().getCanonicalFile();

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
    addFinallyCommand(new ResetCommand(toFileNode));
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
      dirFile.mkdir();
    }

    public void undo()
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
    private File backupFile;
    private boolean toFileExists;

    CopyCommand(File fromFile, File toFile)
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

    @Override
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

  class ResetCommand
      extends Command
  {
    private FileNode fileNode;

    ResetCommand(FileNode fileNode)
    {
      this.fileNode = fileNode;
    }

    public void execute()
        throws Exception
    {
      reset();
    }

    public void undo()
    {
      reset();
    }

    @Override
    public void discard()
    {
      reset();
    }

    private void reset()
    {
      fileNode.resetContent();
      diffNode.compareContents();
    }
  }
}
