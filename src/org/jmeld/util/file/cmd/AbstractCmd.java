package org.jmeld.util.file.cmd;

import java.io.*;
import java.util.*;

public abstract class AbstractCmd
{
  private List<Command> commands = new ArrayList<Command>();
  protected boolean     debug;

  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }

  public synchronized void execute()
    throws IOException
  {
    commands.clear();

    createCommands();

    for (Command command : commands)
    {
      command.execute();
    }
  }

  protected abstract void createCommands()
    throws IOException;

  protected void addCommand(Command command)
  {
    commands.add(command);
  }

  public void redo()
    throws IOException
  {
    execute();
  }

  public synchronized void undo()
    throws IOException
  {
    // Undo should be executed in the reverse order!
    // Note: the commandList itself is reversed and that is OK because
    //       at the end of this method the commandList is cleared.
    Collections.reverse(commands);
    for (Command command : commands)
    {
      command.undo();
    }

    commands.clear();
  }

  public synchronized void discard()
    throws IOException
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
      throws IOException;

    public void redo()
      throws IOException
    {
      execute();
    }

    public abstract void undo()
      throws IOException;

    public void discard()
      throws IOException
    {
    }
  }
}
