package org.jmeld.util.file.cmd;

import javax.swing.undo.*;

import java.util.*;

public abstract class AbstractCmd
    extends AbstractUndoableEdit
{
  private List<Command> commandList = new ArrayList<Command>();
  private List<Command> finallyCommandList = new ArrayList<Command>();
  protected boolean debug = true;

  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }

  public synchronized void execute()
      throws Exception
  {
    commandList.clear();
    finallyCommandList.clear();
    createCommands();

    for (Command command : commandList)
    {
      command.execute();
    }

    for (Command command : finallyCommandList)
    {
      command.execute();
    }
  }

  protected abstract void createCommands()
      throws Exception;

  protected void addCommand(Command command)
  {
    commandList.add(command);
  }

  protected void addFinallyCommand(Command command)
  {
    finallyCommandList.add(command);
  }

  @Override
  public synchronized void redo()
  {
    super.redo();
    for (Command command : commandList)
    {
      command.redo();
    }

    for (Command command : finallyCommandList)
    {
      command.redo();
    }
  }

  @Override
  public synchronized void undo()
  {
    super.undo();

    // Undo should be executed in the reverse order!
    // Note: the commandList itself is reversed and that is OK because
    //       at the end of this method the commandList is cleared.
    try
    {
      Collections.reverse(commandList);
      for (Command command : commandList)
      {
        command.undo();
      }
      Collections.reverse(commandList);

      Collections.reverse(finallyCommandList);
      for (Command command : finallyCommandList)
      {
        command.undo();
      }
      Collections.reverse(finallyCommandList);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw new CannotRedoException();
    }
  }

  public synchronized void discard()
  {
    Collections.reverse(commandList);
    for (Command command : commandList)
    {
      command.discard();
    }

    commandList.clear();

    Collections.reverse(finallyCommandList);
    for (Command command : finallyCommandList)
    {
      command.discard();
    }

    finallyCommandList.clear();
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
        ex.printStackTrace();
        throw new CannotRedoException();
      }
    }

    public abstract void undo();

    public void discard()
    {
    }
  }
}
