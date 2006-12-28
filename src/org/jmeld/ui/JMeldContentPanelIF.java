package org.jmeld.ui;

import org.jmeld.ui.search.*;

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

  public void doGoToSelected();

  public void doGoToFirst();

  public void doGoToLast();

  public SearchHits doSearch(SearchCommand command);

  public void doNextSearch();

  public void doPreviousSearch();

  public void doRefresh();
}
