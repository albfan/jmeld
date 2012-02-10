package org.jmeld.util.file.cmd;

import org.jmeld.util.file.*;
import org.jmeld.util.node.*;

import javax.swing.undo.*;

import java.io.*;

public class RemoveFileCmd
    extends AbstractCmd
{
  private JMDiffNode diffNode;
  private FileNode fileNode;

  public RemoveFileCmd(JMDiffNode diffNode, FileNode fileNode)
  {
    this.diffNode = diffNode;
    this.fileNode = fileNode;
  }

  public void createCommands()
      throws Exception
  {
    addCommand(new RemoveCommand(fileNode.getFile()));
    addFinallyCommand(new ResetCommand(fileNode));
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

        FileUtil.copy(file, originalFile);
      }

      if (debug)
      {
        System.out.println("delete : " + file);
      }
      file.delete();
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
          FileUtil.copy(originalFile, file);
        }
      }
      catch (Exception ex)
      {
        throw new CannotUndoException();
      }
    }

    @Override
    public void discard()
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
