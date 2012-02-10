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

import org.jmeld.ui.search.SearchHits;
import org.jmeld.util.ObjectUtil;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class AbstractContentPanel
    extends JPanel
    implements JMeldContentPanelIF
{
  private MyUndoManager undoManager = new MyUndoManager();
  private String id;

  public void setId(String id)
  {
    this.id = id;
  }

  public String getId()
  {
    return id;
  }

  public boolean isSaveEnabled()
  {
    return false;
  }

  public void doSave()
  {
  }

  public boolean checkSave()
  {
    return true;
  }

  public boolean isUndoEnabled()
  {
    return getUndoHandler().canUndo();
  }

  public void doUndo()
  {
    try
    {
      if (getUndoHandler().canUndo())
      {
        getUndoHandler().undo();
      }
    }
    catch (CannotUndoException ex)
    {
      System.out.println("Unable to undo: " + ex);
      ex.printStackTrace();
    }
  }

  public boolean isRedoEnabled()
  {
    return getUndoHandler().canRedo();
  }

  public void doRedo()
  {
    try
    {
      if (getUndoHandler().canRedo())
      {
        getUndoHandler().redo();
      }
    }
    catch (CannotUndoException ex)
    {
      System.out.println("Unable to undo: " + ex);
      ex.printStackTrace();
    }
  }

  public void doLeft(boolean shift)
  {
  }

  public void doRight(boolean shift)
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

  public void doGoToLine(int line)
  {
  }

  public void doStopSearch()
  {
  }

  public SearchHits doSearch()
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

  public boolean checkExit()
  {
    return true;
  }

  public class MyUndoManager
      extends UndoManager
      implements UndoableEditListener
  {
    CompoundEdit activeEdit;

    private MyUndoManager()
    {
    }

    public void start(String text)
    {
      activeEdit = new CompoundEdit();
    }

    public void add(UndoableEdit edit)
    {
      addEdit(edit);
    }

    public void end(String text)
    {
      activeEdit.end();
      addEdit(activeEdit);
      activeEdit = null;

      checkActions();
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e)
    {
      if (activeEdit != null)
      {
        activeEdit.addEdit(e.getEdit());
        return;
      }

      addEdit(e.getEdit());
      checkActions();
    }
  }

  public MyUndoManager getUndoHandler()
  {
    return undoManager;
  }

  public void checkActions()
  {
  }

  public String getSelectedText()
  {
    return null;
  }

  public boolean equals(Object o)
  {
    if (!(o instanceof AbstractContentPanel))
    {
      return false;
    }

    return ObjectUtil.equals(((AbstractContentPanel) o).getId(), id);
  }
}
