package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.*;
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

public class BufferDiffPanel
       extends JPanel
       implements JMeldPanelIF
{
  private JMeldPanel         mainPanel;
  private FilePanel[]        filePanels;
  private Revision           currentRevision;
  private MyUndoManager      undoManager = new MyUndoManager();
  private ScrollSynchronizer scrollSynchronizer;
  private JMeldDiff          diff;

  BufferDiffPanel(JMeldPanel mainPanel)
  {
    this.mainPanel = mainPanel;

    diff = new JMeldDiff();

    filePanels = new FilePanel[3];

    init();
  }

  public void setBufferDocuments(BufferDocumentIF bd1, BufferDocumentIF bd2,
    JMeldDiff diff, Revision revision)
  {
    this.diff = diff;

    if (bd1 != null)
    {
      filePanels[0].setBufferDocument(bd1);
    }

    if (bd2 != null)
    {
      filePanels[1].setBufferDocument(bd2);
    }

    if (bd1 != null && bd2 != null)
    {
      filePanels[0].setRevision(revision);
      filePanels[1].setRevision(revision);
    }

    currentRevision = revision;
    repaint();
  }

  public String getTitle()
  {
    String           title;
    BufferDocumentIF bd;

    title = "";

    for (FilePanel filePanel : filePanels)
    {
      if (filePanel == null)
      {
        continue;
      }

      bd = filePanel.getBufferDocument();
      if (bd == null)
      {
        continue;
      }

      if (!StringUtil.isEmpty(title))
      {
        title += "-";
      }

      title += bd.getShortName();
    }

    return title;
  }

  public void diff()
  {
    BufferDocumentIF bd1;
    BufferDocumentIF bd2;

    bd1 = filePanels[0].getBufferDocument();
    bd2 = filePanels[1].getBufferDocument();

    if (bd1 != null && bd2 != null)
    {
      try
      {
        currentRevision = diff.diff(bd1.getLines(), bd2.getLines());

        filePanels[0].setRevision(currentRevision);
        filePanels[1].setRevision(currentRevision);
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

    filePanels[0] = new FilePanel(this, BufferDocumentIF.ORIGINAL);
    filePanels[1] = new FilePanel(this, BufferDocumentIF.REVISED);

    // panel for file1
    add(filePanels[0].getSaveButton(), cc.xy(2, 2));
    add(filePanels[0].getFileLabel(), cc.xyw(3, 2, 3));
    add(filePanels[0].getScrollPane(), cc.xyw(3, 4, 3));

    add(new DiffScrollComponent(this, filePanels[0], filePanels[1]),
      cc.xy(6, 4));

    // panel for file2
    add(filePanels[1].getFileLabel(), cc.xyw(7, 2, 3));
    add(filePanels[1].getScrollPane(), cc.xyw(7, 4, 3));
    add(filePanels[1].getSaveButton(), cc.xy(10, 2));

    scrollSynchronizer = new ScrollSynchronizer(this, filePanels[0],
        filePanels[1]);
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

  public boolean checkSave()
  {
    SavePanelDialog dialog;

    if (!isSaveEnabled())
    {
      return true;
    }

    dialog = new SavePanelDialog(mainPanel);
    for (FilePanel filePanel : filePanels)
    {
      if (filePanel != null)
      {
        dialog.add(filePanel.getBufferDocument());
      }
    }

    dialog.show();

    if (dialog.isOK())
    {
      dialog.doSave();
      return true;
    }

    return false;
  }

  public void doSave()
  {
    BufferDocumentIF document;

    for (FilePanel filePanel : filePanels)
    {
      if (filePanel == null)
      {
        continue;
      }

      if (!filePanel.isDocumentChanged())
      {
        continue;
      }

      document = filePanel.getBufferDocument();

      try
      {
        document.write();
      }
      catch (JMeldException ex)
      {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(mainPanel,
          "Can't write file" + document.getName(), "Problem writing file",
          JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public boolean isSaveEnabled()
  {
    for (FilePanel filePanel : filePanels)
    {
      if (filePanel != null && filePanel.isDocumentChanged())
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

  public MyUndoManager getUndoHandler()
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
    CompoundEdit activeEdit;

    public void start(String text)
    {
      activeEdit = new CompoundEdit();
    }

    public void end(String text)
    {
      activeEdit.end();
      addEdit(activeEdit);
      activeEdit = null;

      checkActions();
    }

    public void undoableEditHappened(UndoableEditEvent e)
    {
      if(activeEdit != null)
      {
        activeEdit.addEdit(e.getEdit());
        return;
      }

      addEdit(e.getEdit());
      checkActions();
    }
  }
}
