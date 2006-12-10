package org.jmeld.ui;

public interface JMeldPanelIF
{
  public boolean isSaveEnabled();

  public void doSave();

  public boolean isUndoEnabled();

  public void doUndo();

  public boolean isRedoEnabled();

  public void doRedo();
}
