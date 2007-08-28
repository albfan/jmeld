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

import com.jgoodies.forms.layout.*;

import org.jmeld.*;
import org.jmeld.diff.*;
import org.jmeld.ui.search.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.node.*;

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
  public static final int    LEFT = 0;
  public static final int    MIDDLE = 1;
  public static final int    RIGHT = 2;
  private static int         instanceCounter = 0;
  private int                instanceCount = ++instanceCounter;
  private JMeldPanel         mainPanel;
  private FilePanel[]        filePanels;
  int                        filePanelSelectedIndex = -1;
  private JMRevision         currentRevision;
  private JMDelta            selectedDelta;
  private int                selectedLine;
  private MyUndoManager      undoManager = new MyUndoManager();
  private ScrollSynchronizer scrollSynchronizer;
  private JMDiff             diff;

  BufferDiffPanel(JMeldPanel mainPanel)
  {
    this.mainPanel = mainPanel;

    diff = new JMDiff();
    filePanels = new FilePanel[3];

    init();

    setFocusable(true);
  }

  public void setDiffNode(JMDiffNode diffNode)
  {
    setBufferDocuments(
      diffNode.getBufferNodeLeft().getDocument(),
      diffNode.getBufferNodeRight().getDocument(),
      diffNode.getDiff(),
      diffNode.getRevision());
  }

  private void setBufferDocuments(
    BufferDocumentIF bd1,
    BufferDocumentIF bd2,
    JMDiff           diff,
    JMRevision       revision)
  {
    this.diff = diff;

    currentRevision = revision;

    if (bd1 != null)
    {
      filePanels[LEFT].setBufferDocument(bd1);
    }

    if (bd2 != null)
    {
      filePanels[RIGHT].setBufferDocument(bd2);
    }

    if (bd1 != null && bd2 != null)
    {
      filePanels[LEFT].updateFileLabel(
        bd1.getName(),
        bd2.getName());
      filePanels[RIGHT].updateFileLabel(
        bd2.getName(),
        bd1.getName());
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
    List<String>     titles;

    title = "";

    titles = new ArrayList<String>();
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

      title = bd.getShortName();
      if (StringUtil.isEmpty(title))
      {
        continue;
      }

      titles.add(title);
    }

    title = "";
    if (titles.size() == 1)
    {
      title = titles.get(0);
    }
    else
    {
      if (titles.get(0).equals(titles.get(1)))
      {
        title = titles.get(0);
      }
      else
      {
        title = titles.get(0) + "-" + titles.get(1);
      }
    }

    return title;
  }

  public void diff()
  {
    BufferDocumentIF bd1;
    BufferDocumentIF bd2;

    bd1 = filePanels[LEFT].getBufferDocument();
    bd2 = filePanels[RIGHT].getBufferDocument();

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
    rows = "6px, pref, 3px, fill:0:grow, pref";
    layout = new FormLayout(columns, rows);
    cc = new CellConstraints();

    setLayout(layout);

    filePanels[LEFT] = new FilePanel(this, BufferDocumentIF.ORIGINAL, LEFT);
    filePanels[RIGHT] = new FilePanel(this, BufferDocumentIF.REVISED, RIGHT);

    // panel for file1
    add(
      new RevisionBar(this, filePanels[LEFT], true),
      cc.xy(2, 4));
    add(
      filePanels[LEFT].getSaveButton(),
      cc.xy(2, 2));
    add(
      filePanels[LEFT].getFileLabel(),
      cc.xyw(4, 2, 3));
    add(
      filePanels[LEFT].getScrollPane(),
      cc.xyw(4, 4, 3));
    add(
      filePanels[LEFT].getFilePanelBar(),
      cc.xyw(4, 5, 3));

    add(
      new DiffScrollComponent(this, LEFT, RIGHT),
      cc.xy(7, 4));

    // panel for file2
    add(
      new RevisionBar(this, filePanels[RIGHT], false),
      cc.xy(12, 4));
    add(
      filePanels[RIGHT].getFileLabel(),
      cc.xyw(8, 2, 3));
    add(
      filePanels[RIGHT].getScrollPane(),
      cc.xyw(8, 4, 3));
    add(
      filePanels[RIGHT].getSaveButton(),
      cc.xy(12, 2));
    add(
      filePanels[RIGHT].getFilePanelBar(),
      cc.xyw(8, 5, 3));

    scrollSynchronizer = new ScrollSynchronizer(this, filePanels[LEFT],
        filePanels[RIGHT]);

    setSelectedPanel(filePanels[LEFT]);
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

    fp = getSelectedPanel();
    if (fp == null)
    {
      return null;
    }

    searchHits = fp.doSearch(command);

    scrollToSearch(fp, searchHits);

    return searchHits;
  }

  public void doNextSearch()
  {
    FilePanel  fp;
    SearchHits searchHits;

    fp = getSelectedPanel();
    if (fp == null)
    {
      return;
    }

    searchHits = fp.getSearchHits();
    searchHits.next();
    fp.reDisplay();

    scrollToSearch(fp, searchHits);
  }

  public void doPreviousSearch()
  {
    FilePanel  fp;
    SearchHits searchHits;

    fp = getSelectedPanel();
    if (fp == null)
    {
      return;
    }

    searchHits = fp.getSearchHits();
    searchHits.previous();
    fp.reDisplay();

    scrollToSearch(fp, searchHits);
  }

  public void doRefresh()
  {
    diff();
  }

  public void doMergeMode(boolean mergeMode)
  {
    for (FilePanel fp : filePanels)
    {
      if (fp != null)
      {
        fp.getEditor().setFocusable(!mergeMode);
      }
    }
  }

  private void scrollToSearch(
    FilePanel  fp,
    SearchHits searchHits)
  {
    SearchHit currentHit;
    int       line;

    currentHit = searchHits.getCurrent();
    if (currentHit != null)
    {
      line = currentHit.getLine();

      scrollSynchronizer.scrollToLine(fp, line);
      setSelectedLine(line);
    }
  }

  private FilePanel getSelectedPanel()
  {
    if (filePanelSelectedIndex >= 0
      && filePanelSelectedIndex < filePanels.length)
    {
      return filePanels[filePanelSelectedIndex];
    }

    return null;
  }

  void setSelectedPanel(FilePanel fp)
  {
    int index;

    index = -1;
    for (int i = 0; i < filePanels.length; i++)
    {
      if (filePanels[i] == fp)
      {
        index = i;
      }
    }

    if (index != filePanelSelectedIndex)
    {
      if (filePanelSelectedIndex != -1)
      {
        filePanels[filePanelSelectedIndex].setSelected(false);
      }

      filePanelSelectedIndex = index;

      if (filePanelSelectedIndex != -1)
      {
        filePanels[filePanelSelectedIndex].setSelected(true);
      }
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
    runChange(RIGHT, LEFT);
  }

  public void doRight()
  {
    runChange(LEFT, RIGHT);
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
      if (fromOffset < 0)
      {
        return;
      }

      toOffset = fromBufferDocument.getOffsetForLine(fromLine + size);
      if (toOffset < 0)
      {
        return;
      }

      s = from.getText(fromOffset, toOffset - fromOffset);

      fromLine = toChunk.getAnchor();
      size = toChunk.getSize();
      fromOffset = toBufferDocument.getOffsetForLine(fromLine);
      if (fromOffset < 0)
      {
        return;
      }

      toOffset = toBufferDocument.getOffsetForLine(fromLine + size);
      if (toOffset < 0)
      {
        return;
      }

      getUndoHandler().start("replace");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection(s);
      getUndoHandler().end("replace");

      setSelectedDelta(null);
      setSelectedLine(delta.getOriginal().getAnchor());
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
      if (fromOffset < 0)
      {
        return;
      }

      toOffset = bufferDocument.getOffsetForLine(fromLine + size);
      if (toOffset < 0)
      {
        return;
      }

      getUndoHandler().start("remove");
      toEditor.setSelectionStart(fromOffset);
      toEditor.setSelectionEnd(toOffset);
      toEditor.replaceSelection("");
      getUndoHandler().end("remove");

      setSelectedDelta(null);
      setSelectedLine(delta.getOriginal().getAnchor());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void doDown()
  {
    JMDelta       d;
    JMDelta       sd;
    List<JMDelta> deltas;
    int           index;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    sd = getSelectedDelta();
    index = deltas.indexOf(sd);
    if (index == -1 || sd.getOriginal().getAnchor() != selectedLine)
    {
      // Find the delta that would have been next to the 
      //   disappeared delta:
      d = null;
      for (JMDelta delta : deltas)
      {
        d = delta;
        if (delta.getOriginal().getAnchor() > selectedLine)
        {
          break;
        }
      }

      setSelectedDelta(d);
    }
    else
    {
      // Select the next delta if there is any.
      if (index + 1 < deltas.size())
      {
        setSelectedDelta(deltas.get(index + 1));
      }
    }

    showSelectedDelta();
  }

  public void doUp()
  {
    JMDelta       d;
    JMDelta       sd;
    JMDelta       previousDelta;
    List<JMDelta> deltas;
    int           index;

    if (currentRevision == null)
    {
      return;
    }

    deltas = currentRevision.getDeltas();
    sd = getSelectedDelta();
    index = deltas.indexOf(sd);
    if (index == -1 || sd.getOriginal().getAnchor() != selectedLine)
    {
      // Find the delta that would have been previous to the 
      //   disappeared delta:
      d = null;
      previousDelta = null;
      for (JMDelta delta : deltas)
      {
        d = delta;
        if (delta.getOriginal().getAnchor() > selectedLine)
        {
          if (previousDelta != null)
          {
            d = previousDelta;
          }
          break;
        }

        previousDelta = delta;
      }

      setSelectedDelta(d);
    }
    else
    {
      // Select the next delta if there is any.
      if (index - 1 >= 0)
      {
        setSelectedDelta(deltas.get(index - 1));
      }
    }
    showSelectedDelta();
  }

  public void doGotoDelta(JMDelta delta)
  {
    setSelectedDelta(delta);
    showSelectedDelta();
  }

  public void doGotoLine(int line)
  {
    BufferDocumentIF bd;
    int              offset;
    int              startOffset;
    int              endOffset;
    JViewport        viewport;
    JTextComponent   editor;
    Point            p;
    FilePanel        fp;
    Rectangle        rect;

    setSelectedLine(line);

    fp = getFilePanel(0);

    bd = fp.getBufferDocument();
    offset = bd.getOffsetForLine(line);
    viewport = fp.getScrollPane().getViewport();
    editor = fp.getEditor();

    // Don't go anywhere if the line is already visible.
    rect = viewport.getViewRect();
    startOffset = editor.viewToModel(rect.getLocation());
    endOffset = editor.viewToModel(new Point(rect.x, rect.y + rect.height));
    if (offset >= startOffset && offset <= endOffset)
    {
      return;
    }

    try
    {
      p = editor.modelToView(offset).getLocation();
      p.x = 0;

      viewport.setViewPosition(p);
    }
    catch (BadLocationException ex)
    {
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
    selectedDelta = delta;
    setSelectedLine(delta == null ? 0 : delta.getOriginal().getAnchor());
  }

  private void setSelectedLine(int line)
  {
    selectedLine = line;
  }

  private void showSelectedDelta()
  {
    JMDelta delta;

    delta = getSelectedDelta();
    if (delta == null)
    {
      return;
    }

    scrollSynchronizer.showDelta(delta);
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
