package org.jmeld.ui;

public interface JMeldContentPanelIF
{
  public boolean isSaveEnabled();

  public void doSave();

  public boolean isUndoEnabled();

  public void doUndo();

  public boolean isRedoEnabled();

  public void doRedo();

  public void doLeft();

  public void doRight();

  public void doUp();

  public void doDown();

  public void doZoom(boolean direction);
}
