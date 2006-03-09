package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.apache.commons.jrcs.diff.*;
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

  public DiffPanel(
    JMeldPanel mainPanel,
    String     fileName1,
    String     fileName2)
  {
    this.mainPanel = mainPanel;

    init();

    if (fileName1 != null)
    {
      filePanel1.setFile(new File(fileName1));
    }

    if (fileName2 != null)
    {
      filePanel2.setFile(new File(fileName2));
    }

    diff();
  }

  public String getTitle()
  {
    String title;

    title = "";

    if (filePanel1 != null)
    {
      title += filePanel1.getFileDocument().getName();
    }

    if (filePanel2 != null)
    {
      title += "-";
      title += filePanel2.getFileDocument().getName();
    }

    return title;
  }

  public void diff()
  {
    Diff         diff;
    Revision     rev;
    FileDocument fd1;
    FileDocument fd2;

    fd1 = filePanel1.getFileDocument();
    fd2 = filePanel2.getFileDocument();

    if (fd1 != null && fd2 != null)
    {
      try
      {
        diff = new Diff(fd1.getLines());
        currentRevision = diff.diff(fd2.getLines());

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

    filePanel1 = new FilePanel(this, FileDocument.ORIGINAL);
    filePanel2 = new FilePanel(this, FileDocument.REVISED);

    // panel for file1
    add(
      filePanel1.getSaveButton(),
      cc.xy(2, 2));
    add(
      filePanel1.getFileBox(),
      cc.xy(3, 2));
    add(
      filePanel1.getBrowseButton(),
      cc.xy(5, 2));
    add(
      filePanel1.getScrollPane(),
      cc.xyw(3, 4, 3));

    add(
      new DiffScrollComponent(this, filePanel1, filePanel2),
      cc.xy(6, 4));

    // panel for file2
    add(
      filePanel2.getFileBox(),
      cc.xy(7, 2));
    add(
      filePanel2.getBrowseButton(),
      cc.xy(9, 2));
    add(
      filePanel2.getScrollPane(),
      cc.xyw(7, 4, 3));
    add(
      filePanel2.getSaveButton(),
      cc.xy(10, 2));

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
    if (filePanel1 != null)
    {
      if (filePanel1.isDocumentChanged())
      {
        return true;
      }
    }

    if (filePanel2 != null)
    {
      if (filePanel2.isDocumentChanged())
      {
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

        // I can only be sure that none of the documents have 
        //   changed if nothing can be undone! (I do not know if
        //   there are UndoableEvents per document!)
        if (!undoManager.canUndo())
        {
          filePanel1.setDocumentChanged(false);
          filePanel2.setDocumentChanged(false);
        }
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
