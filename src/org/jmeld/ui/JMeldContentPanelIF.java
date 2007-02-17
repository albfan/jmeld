/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.ui;

import org.jmeld.ui.search.*;

public interface JMeldContentPanelIF
{
  public boolean isSaveEnabled();

  public void doSave();

  public boolean checkSave();

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

  public void doMergeMode(boolean mergeMode);
  
  public boolean checkExit();
}
