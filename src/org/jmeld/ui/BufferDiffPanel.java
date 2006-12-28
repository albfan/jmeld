package org.jmeld.ui;

import com.jgoodies.forms.layout.*;

import org.jmeld.*;
import org.jmeld.diff.*;
import org.jmeld.ui.search.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
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
       extends AbstractContentPanel
{
  private static int         instanceCounter = 0;
  private int                instanceCount = ++instanceCounter;
  private JMeldPanel         mainPanel;
  private FilePanel[]        filePanels;
  private JMRevision         currentRevision;
  private JMDelta            selectedDelta;
  private MyUndoManager      undoManager = new MyUndoManager();
  private ScrollSynchronizer scrollSynchronizer;
  private JMDiff             diff;

  BufferDiffPanel(JMeldPanel mainPanel)
  {
    this.mainPanel = mainPanel;

    diff = new JMDiff();
    filePanels = new FilePanel[3];

    init();
  }

  public void setBufferDocuments(
    BufferDocumentIF bd1,
    BufferDocumentIF bd2,
    JMDiff           diff,
    JMRevision       revision)
  {
    this.diff = diff;

    currentRevision = revision;

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
      for (FilePanel fp : filePanels)
      {
        if (fp != null)
        {
          fp.reDisplay();
        }
      }
    }

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
        currentRevision = diff.diff(
            bd1.getLines(),
            bd2.getLines());

        for (FilePanel fp : filePanels)
        {
          if (fp != null)
          {
            fp.reDisplay();
          }
        }
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

    columns = "3px, pref, 3px, 0:grow, 5px, min, 60px, 0:grow, 25px, min, 3px, pref, 3px";
    rows = "6px, pref, 3px, fill:0:grow, 6px";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    setLayout(layout);

    filePanels[0] = new FilePanel(this, BufferDocumentIF.ORIGINAL);
    filePanels[1] = new FilePanel(this, BufferDocumentIF.REVISED);

    // panel for file1
    add(
      new RevisionBar(this, filePanels[0], true),
      cc.xy(2, 4));
    add(
      filePanels[0].getSaveButton(),
      cc.xy(2, 2));
    add(
      filePanels[0].getFileLabel(),
      cc.xyw(4, 2, 3));
    add(
      filePanels[0].getScrollPane(),
      cc.xyw(4, 4, 3));

    add(
      new DiffScrollComponent(this, 0, 1),
      cc.xy(7, 4));

    // panel for file2
    add(
      new RevisionBar(this, filePanels[1], false),
      cc.xy(12, 4));
    add(
      filePanels[1].getFileLabel(),
      cc.xyw(8, 2, 3));
    add(
      filePanels[1].getScrollPane(),
      cc.xyw(8, 4, 3));
    add(
      filePanels[1].getSaveButton(),
      cc.xy(12, 2));

    scrollSynchronizer = new ScrollSynchronizer(this, filePanels[0],
        filePanels[1]);
  }

  void toNextDelta(boolean next)
  {
    if (next)
    {
      doDown();
    }
    else
    {
      doUp();
    }
  }

  JMRevision getCurrentRevision()
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

  public SearchHits doSearch(SearchCommand command)
  {
    FilePanel  fp;
    SearchHits searchHits;

    fp = getSearchPanel();
    searchHits = fp.doSearch(command);

    scrollToSearch(fp, searchHits);

    return searchHits;
  }

  public void doNextSearch()
  {
    FilePanel  fp;
    SearchHits searchHits;

    fp = getSearchPanel();
    searchHits = fp.getSearchHits();
    searchHits.next();
    fp.reDisplay();

    scrollToSearch(fp, searchHits);
  }

  public void doPreviousSearch()
  {
    FilePanel  fp;
    SearchHits searchHits;

    fp = getSearchPanel();
    searchHits = fp.getSearchHits();
    searchHits.previous();
    fp.reDisplay();

    scrollToSearch(fp, searchHits);
  }

  public void doRefresh()
  {
    diff();
  }

  private void scrollToSearch(
    FilePanel  fp,
    SearchHits searchHits)
  {
    SearchHit currentHit;

    currentHit = searchHits.getCurrent();
    if (currentHit != null)
    {
      scrollSynchronizer.scrollToLine(
        fp,
        currentHit.getLine());
    }
  }

  private FilePanel getSearchPanel()
  {
    return filePanels[0];
  }

  public MyUndoManager getUndoHandler()
  {
    return undoManager;
  }

  public void checkActions()
  {
    mainPanel.checkActions();
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

    public void end(String text)
    {
      activeEdit.end();
      addEdit(activeEdit);
      activeEdit = null;

      checkActions();
    }

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

  public void doLeft()
  {
    runChange(1, 0);
  }

  public void doRight()
  {
    runChange(0, 1);
  }

  void runChange(
    int fromPanelIndex,
    int toPanelIndex)
  {
    JMDelta          delta;
    BufferDocumentIF fromBufferDocument;
    BufferDocumentIF toBufferDocument;
    PlainDocument    from;
    PlainDocument    to;
    String           s;
    int              fromLine;
    int              toLine;
    int              fromOffset;
    int              toOffset;
    int              size;
    JMChunk          fromChunk;
    JMChunk          toChunk;
    JTextComponent   toEditor;

    delta = getSelectedDelta();
    if (delta == null)
    {
      return;
    }

    // Some sanity checks.
    if (fromPanelIndex < 0 || fromPanelIndex >= filePanels.length)
    {
      return;
    }

    if (toPanelIndex < 0 || toPanelIndex >= filePanels.length)
    {
      return;
    }

    try
    {
      fromBufferDocument = filePanels[fromPanelIndex].getBufferDocument();
      toBufferDocument = filePanels[toPanelIndex].getBufferDocument();

      // TODO: delta and revision are not yet ready for 3-way merge!
      if (fromPanelIndex < toPanelIndex)
      {
        fromChunk = delta.getOriginal();
        toChunk = delta.getRevised();
      }
      else
      {
        fromChunk = delta.getRevised();
        toChunk = delta.getOriginal();
      }
      toEditor = filePanels[toPanelIndex].getEditor();

      if (fromBufferDocument == null || toBufferDocument == null)
      {
        return;
      }

      from = fromBufferDocument.getDocument();
      to = toBufferDocument.getDocument();

      fromLine = fromChunk.getAnchor();
      size = fromChunk.getSize();
      fromOffset = fromBufferDocument.getOffsetForLine(fromLine);
      toOffset = fromBufferDocument.getOffsetForLine(fromLine + size);

      s = from.getText(fromOffset, toOffset - fromOffset);

      fromLine = toChunk.getAnchor();
      size = toChunk.getSize();
      fromOffset = toBufferDocument.getOffsetForLine(fromLine);
      toOffset = toBufferDocument.getOffsetForLine(fromLine + size);

      getUndoHandler().start("replace");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection(s);
      getUndoHandler().end("replace");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void runDelete(
    int fromPanelIndex,
    int toPanelIndex)
  {
    JMDelta          delta;
    BufferDocumentIF bufferDocument;
    PlainDocument    document;
    String           s;
    int              fromLine;
    int              fromOffset;
    int              toOffset;
    int              size;
    JMChunk          chunk;
    JTextComponent   toEditor;

    try
    {
      delta = getSelectedDelta();
      if (delta == null)
      {
        return;
      }

      // Some sanity checks.
      if (fromPanelIndex < 0 || fromPanelIndex >= filePanels.length)
      {
        return;
      }

      if (toPanelIndex < 0 || toPanelIndex >= filePanels.length)
      {
        return;
      }

      bufferDocument = filePanels[fromPanelIndex].getBufferDocument();
      if (fromPanelIndex < toPanelIndex)
      {
        chunk = delta.getOriginal();
      }
      else
      {
        chunk = delta.getRevised();
      }
      toEditor = filePanels[fromPanelIndex].getEditor();

      if (bufferDocument == null)
      {
        return;
      }

      document = bufferDocument.getDocument();
      fromLine = chunk.getAnchor();
      size = chunk.getSize();
      fromOffset = bufferDocument.getOffsetForLine(fromLine);
      toOffset = bufferDocument.getOffsetForLine(fromLine + size);

      getUndoHandler().start("remove");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection("");
      getUndoHandler().end("remove");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void doDown()
  {
    JMDelta       d;
    List<JMDelta> deltas;
    int           index;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    index = deltas.indexOf(getSelectedDelta());
    if (index == -1)
    {
      // I don't know it now anymore!
      setSelectedDelta(null);
    }
    else
    {
      // Select the next delta if there is any.
      if (index + 1 < deltas.size())
      {
        setSelectedDelta(deltas.get(index + 1));
        showSelectedDelta();
      }
    }
  }

  public void doUp()
  {
    JMDelta       d;
    List<JMDelta> deltas;
    int           index;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    index = deltas.indexOf(getSelectedDelta());
    if (index == -1)
    {
      // I don't know it now anymore!
      setSelectedDelta(null);
    }
    else
    {
      // Select the next delta if there is any.
      if (index - 1 >= 0)
      {
        setSelectedDelta(deltas.get(index - 1));
        showSelectedDelta();
      }
    }
  }

  public void doZoom(boolean direction)
  {
    JTextComponent c;
    Font           font;
    float          size;
    Zoom           zoom;

    for (FilePanel p : filePanels)
    {
      if (p == null)
      {
        continue;
      }

      c = p.getEditor();

      zoom = (Zoom) c.getClientProperty("JMeld.zoom");
      if (zoom == null)
      {
        // Save the orginal font because that's the font which will
        //   give the derived font.
        zoom = new Zoom();
        zoom.font = c.getFont();
        c.putClientProperty("JMeld.zoom", zoom);
      }

      size = c.getFont().getSize() + (direction ? 1.0f : -1.0f);
      size = size > 100.0f ? 100.0f : size;
      size = size < 5.0f ? 5.0f : size;

      c.setFont(zoom.font.deriveFont(size));
    }
  }

  public void doGoToSelected()
  {
    showSelectedDelta();
  }

  public void doGoToFirst()
  {
    JMDelta       d;
    List<JMDelta> deltas;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    if (deltas.size() > 0)
    {
      setSelectedDelta(deltas.get(0));
      showSelectedDelta();
    }
  }

  public void doGoToLast()
  {
    JMDelta       d;
    List<JMDelta> deltas;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    if (deltas.size() > 0)
    {
      setSelectedDelta(deltas.get(deltas.size() - 1));
      showSelectedDelta();
    }
  }

  class Zoom
  {
    Font font;
  }

  void setSelectedDelta(JMDelta delta)
  {
    this.selectedDelta = delta;
  }

  private void showSelectedDelta()
  {
    scrollSynchronizer.showDelta(getSelectedDelta());
  }

  public JMDelta getSelectedDelta()
  {
    List<JMDelta> deltas;

    if (currentRevision == null)
    {
      return null;
    }

    deltas = currentRevision.getDeltas();
    if (deltas.size() == 0)
    {
      return null;
    }

    if (selectedDelta == null)
    {
      setSelectedDelta(deltas.get(0));
    }

    return selectedDelta;
  }

  FilePanel getFilePanel(int index)
  {
    if (index < 0 || index > filePanels.length)
    {
      return null;
    }

    return filePanels[index];
  }
}
