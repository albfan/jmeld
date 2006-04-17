package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.diff.*;
import org.jmeld.ui.text.*;
import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class DiffPanel
       extends JPanel
{
  private JMeldPanel         mainPanel;
  private FilePanel          filePanel1;
  private FilePanel          filePanel2;
  private Revision           currentRevision;
  private MyUndoManager      undoManager = new MyUndoManager();
  private ScrollSynchronizer scrollSynchronizer;
  private JMeldDiff          diff;

  DiffPanel(JMeldPanel mainPanel)
  {
    this.mainPanel = mainPanel;

    diff = new JMeldDiff();

    init();
  }

  public void setBufferDocuments(BufferDocumentIF bd1, BufferDocumentIF bd2,
    JMeldDiff diff, Revision revision)
  {
    this.diff = diff;

    if (bd1 != null)
    {
      filePanel1.setBufferDocument(bd1);
    }

    if (bd2 != null)
    {
      filePanel2.setBufferDocument(bd2);
    }

    if (bd1 != null && bd2 != null)
    {
      filePanel1.setRevision(revision);
      filePanel2.setRevision(revision);
    }

    currentRevision = revision;
    repaint();
  }

  public String getTitle()
  {
    String           title;
    BufferDocumentIF bd;

    title = "";

    if (filePanel1 != null)
    {
      bd = filePanel1.getBufferDocument();
      if (bd != null)
      {
        title += bd.getShortName();
      }
    }

    if (filePanel2 != null)
    {
      title += "-";
      bd = filePanel2.getBufferDocument();
      if (bd != null)
      {
        title += bd.getShortName();
      }
    }

    return title;
  }

  public void diff()
  {
    BufferDocumentIF bd1;
    BufferDocumentIF bd2;

    bd1 = filePanel1.getBufferDocument();
    bd2 = filePanel2.getBufferDocument();

    if (bd1 != null && bd2 != null)
    {
      try
      {
        currentRevision = diff.diff(bd1.getLines(), bd2.getLines());

        filePanel1.setRevision(currentRevision);
        filePanel2.setRevision(currentRevision);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    repaint();
  }

  private void init()
  {
    FormLayout      layout;
    String          columns;
    String          rows;
    CellConstraints cc;

    columns = "4px, pref, 0:grow, 5px, min, 60px, 0:grow, 5px, min, pref, 4px";
    rows = "6px, pref, 3px, fill:0:grow, 6px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    setLayout(layout);

    filePanel1 = new FilePanel(this, BufferDocumentIF.ORIGINAL);
    filePanel2 = new FilePanel(this, BufferDocumentIF.REVISED);

    // panel for file1
    add(filePanel1.getSaveButton(), cc.xy(2, 2));
    //add(filePanel1.getFileBox(), cc.xy(3, 2));
    //add(filePanel1.getBrowseButton(), cc.xy(5, 2));
    add(filePanel1.getFileLabel(), cc.xyw(3, 2, 3));
    add(filePanel1.getScrollPane(), cc.xyw(3, 4, 3));

    add(new DiffScrollComponent(this, filePanel1, filePanel2), cc.xy(6, 4));

    // panel for file2
    //add(filePanel2.getFileBox(), cc.xy(7, 2));
    //add(filePanel2.getBrowseButton(), cc.xy(9, 2));
    add(filePanel2.getFileLabel(), cc.xyw(7, 2, 3));
    add(filePanel2.getScrollPane(), cc.xyw(7, 4, 3));
    add(filePanel2.getSaveButton(), cc.xy(10, 2));

    scrollSynchronizer = new ScrollSynchronizer(this, filePanel1, filePanel2);
  }

  void toNextDelta(boolean next)
  {
    scrollSynchronizer.toNextDelta(next);
  }

  Revision getCurrentRevision()
  {
    return currentRevision;
  }

  public void resetUndoManager()
  {
    undoManager.discardAllEdits();
  }

  public boolean isSaveEnabled()
  {
    System.out.println("isSaveEnabled?");
    if (filePanel1 != null)
    {
      if (filePanel1.isDocumentChanged())
      {
    System.out.println("document1 is changed!");
        return true;
      }
    }

    if (filePanel2 != null)
    {
      if (filePanel2.isDocumentChanged())
      {
    System.out.println("document2 is changed!");
        return true;
      }
    }

    return false;
  }

  public boolean isUndoEnabled()
  {
    return undoManager.canUndo();
  }

  public void doUndo()
  {
    try
    {
      if (undoManager.canUndo())
      {
        undoManager.undo();
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
    return undoManager.canRedo();
  }

  public void doRedo()
  {
    try
    {
      if (undoManager.canRedo())
      {
        undoManager.redo();
      }
    }
    catch (CannotUndoException ex)
    {
      System.out.println("Unable to undo: " + ex);
      ex.printStackTrace();
    }
  }

  public UndoableEditListener getUndoHandler()
  {
    return undoManager;
  }

  public void checkActions()
  {
    mainPanel.checkActions();
  }

  class MyUndoManager
         extends UndoManager
         implements UndoableEditListener
  {
    public void undoableEditHappened(UndoableEditEvent e)
    {
      addEdit(e.getEdit());
      checkActions();
    }
  }
}
