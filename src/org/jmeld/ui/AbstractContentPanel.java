package org.jmeld.ui;

import org.jmeld.ui.search.*;

import javax.swing.*;

public class AbstractContentPanel
       extends JPanel
       implements JMeldContentPanelIF
{
  public boolean isSaveEnabled()
  {
    return false;
  }

  public void doSave()
  {
  }

  public boolean isUndoEnabled()
  {
    return false;
  }

  public void doUndo()
  {
  }

  public boolean isRedoEnabled()
  {
    return false;
  }

  public void doRedo()
  {
  }

  public void doLeft()
  {
  }

  public void doRight()
  {
  }

  public void doUp()
  {
  }

  public void doDown()
  {
  }

  public void doZoom(boolean direction)
  {
  }

  public void doGoToSelected()
  {
  }

  public void doGoToFirst()
  {
  }

  public void doGoToLast()
  {
  }

  public SearchHits doSearch(SearchCommand command)
  {
    return null;
  }

  public void doNextSearch()
  {
  }

  public void doPreviousSearch()
  {
  }

  public void doRefresh()
  {
  }

  public void doMergeMode(boolean mergeMode)
  {
  }
}
