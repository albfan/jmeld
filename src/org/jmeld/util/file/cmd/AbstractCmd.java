package org.jmeld.util.file.cmd;

import javax.swing.undo.*;

import java.io.*;
import java.util.*;

public abstract class AbstractCmd
       extends AbstractUndoableEdit
{
  private List<Command> commands = new ArrayList<Command>();
  protected boolean     debug = true;
  protected boolean     dryrun = true;

  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }

  public synchronized void execute()
    throws Exception
  {
    commands.clear();
    createCommands();

    for (Command command : commands)
    {
      command.execute();
    }
  }

  protected abstract void createCommands()
    throws Exception;

  protected void addCommand(Command command)
  {
    commands.add(command);
  }

  public synchronized void redo()
  {
    for (Command command : commands)
    {
      command.redo();
    }
  }

  public synchronized void undo()
  {
    // Undo should be executed in the reverse order!
    // Note: the commandList itself is reversed and that is OK because
    //       at the end of this method the commandList is cleared.
    Collections.reverse(commands);
    try
    {
      for (Command command : commands)
      {
        command.undo();
      }
    }
    catch (Exception ex)
    {
      throw new CannotRedoException();
    }
  }

  public synchronized void discard()
  {
    Collections.reverse(commands);
    for (Command command : commands)
    {
      command.discard();
    }

    commands.clear();
  }

  abstract class Command
  {
    public abstract void execute()
      throws Exception;

    public void redo()
    {
      try
      {
        execute();
      }
      catch (Exception ex)
      {
        throw new CannotRedoException();
      }
    }

    public abstract void undo();

    public void discard()
    {
    }
  }
}
