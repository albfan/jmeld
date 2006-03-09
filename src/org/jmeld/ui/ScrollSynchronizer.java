package org.jmeld.ui;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

public class ScrollSynchronizer
       implements ChangeListener
{
  private DiffPanel diffPanel;
  private FilePanel filePanelOriginal;
  private FilePanel filePanelRevised;

  public ScrollSynchronizer(
    DiffPanel diffPanel,
    FilePanel filePanelOriginal,
    FilePanel filePanelRevised)
  {
    this.diffPanel = diffPanel;
    this.filePanelOriginal = filePanelOriginal;
    this.filePanelRevised = filePanelRevised;

    filePanelOriginal.getScrollPane().getViewport().addChangeListener(this);
    filePanelRevised.getScrollPane().getViewport().addChangeListener(this);
  }

  public void stateChanged(ChangeEvent event)
  {
    boolean originalScrolled;

    if (filePanelOriginal == null || filePanelRevised == null)
    {
      return;
    }

    originalScrolled = event.getSource() == filePanelOriginal.getScrollPane()
                                                             .getViewport();
    scroll(originalScrolled);
  }

  private void scroll(boolean originalScrolled)
  {
    Revision  revision;
    FilePanel fp1;
    FilePanel fp2;
    int       line;

    revision = diffPanel.getCurrentRevision();
    if (revision == null)
    {
      return;
    }

    if (originalScrolled)
    {
      fp1 = filePanelOriginal;
      fp2 = filePanelRevised;
    }
    else
    {
      fp1 = filePanelRevised;
      fp2 = filePanelOriginal;
    }

    line = getCurrentLineCenter(fp1);

    if (originalScrolled)
    {
      line = DiffUtil.getRevisedLine(revision, line);
    }
    else
    {
      line = DiffUtil.getOriginalLine(revision, line);
    }

    scrollToLine(fp2, line, true);
  }

  void toNextDelta(boolean next)
  {
    int      line;
    Revision revision;
    Delta    delta;
    Delta    previousDelta;
    Delta    currentDelta;
    Delta    nextDelta;
    Delta    toDelta;
    Chunk    original;
    int      currentIndex;
    int      nextIndex;

    revision = diffPanel.getCurrentRevision();
    if (revision == null)
    {
      return;
    }

    line = getCurrentLineCenter(filePanelOriginal);

    currentDelta = null;
    currentIndex = -1;

    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);
      original = delta.getOriginal();

      currentIndex = i;

      if (line >= original.anchor())
      {
        if (line <= original.anchor() + original.size())
        {
          currentDelta = delta;
          break;
        }
      }
      else
      {
        break;
      }
    }

    previousDelta = null;
    nextDelta = null;
    if (currentIndex != -1)
    {
      if (currentIndex > 0)
      {
        previousDelta = revision.getDelta(currentIndex - 1);
      }

      nextIndex = currentIndex;
      if (currentDelta != null)
      {
        nextIndex++;
      }

      if (nextIndex < revision.size())
      {
        nextDelta = revision.getDelta(nextIndex);
      }
    }

/*
   if(previousDelta != null)
   {
     System.out.println("prev: " + previousDelta.getOriginal().anchor());
   }
   else
   {
     System.out.println("prev: null");
   }

          if(currentDelta != null)
          {
            System.out.println("curr: " + currentDelta.getOriginal().anchor());
          }
          else
          {
            System.out.println("curr: null");
          }

          if(nextDelta != null)
          {
            System.out.println("next: " + nextDelta.getOriginal().anchor());
          }
          else
          {
            System.out.println("next: null");
          }
 */
    if (next)
    {
      toDelta = nextDelta;
    }
    else
    {
      toDelta = previousDelta;
    }

    if (toDelta != null)
    {
      scrollToLine(
        filePanelOriginal,
        toDelta.getOriginal().anchor(),
        false);
    }
  }

  private int getCurrentLineCenter(FilePanel fp)
  {
    Revision       revision;
    JScrollPane    scrollPane;
    FileDocument   fd;
    JTextComponent editor;
    JViewport      viewport;
    int            line;
    Rectangle      rect;
    int            offset;
    Point          p;

    editor = fp.getEditor();
    scrollPane = fp.getScrollPane();
    viewport = scrollPane.getViewport();
    p = viewport.getViewPosition();
    offset = editor.viewToModel(p);

    // Scroll around the center of the editpane
    p.y += getHeightOffset(fp);

    offset = editor.viewToModel(p);
    fd = fp.getFileDocument();
    line = fd.getLineForOffset(offset);

    return line;
  }

  private void scrollToLine(
    FilePanel fp,
    int       line,
    boolean   initiatedByScrolling)
  {
    Revision       revision;
    JScrollPane    scrollPane;
    FilePanel      fp2;
    FileDocument   fd;
    JTextComponent editor;
    JViewport      viewport;
    Rectangle      rect;
    int            offset;
    Point          p;
    Dimension      viewSize;
    Dimension      extentSize;
    int            x;

    fp2 = fp == filePanelOriginal ? filePanelRevised : filePanelOriginal;

    fd = fp.getFileDocument();
    offset = fd.getOffsetForLine(line);
    viewport = fp.getScrollPane().getViewport();
    editor = fp.getEditor();

    try
    {
      rect = editor.modelToView(offset);
      if (rect == null)
      {
        return;
      }

      p = rect.getLocation();
      p.y -= getHeightOffset(fp);
      p.y += getCorrectionOffset(fp2);

      // Do not allow scrolling before the begin.
      if (p.y < 0)
      {
        p.y = 0;
      }

      // Do not allow scrolling after the end.
      viewSize = viewport.getViewSize();
      extentSize = viewport.getExtentSize();
      if (p.y > viewSize.height - extentSize.height)
      {
        p.y = viewSize.height - extentSize.height;
      }

      // Don't listen to change-events if the user has used the scrollbar
      //   of one of the filePanels.
      if (initiatedByScrolling)
      {
        viewport.removeChangeListener(this);
      }

      viewport.setViewPosition(p);
      if (initiatedByScrolling)
      {
        viewport.addChangeListener(this);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private int getHeightOffset(FilePanel fp)
  {
    JScrollPane scrollPane;
    JViewport   viewport;
    int         offset;
    int         unitIncrement;

    scrollPane = fp.getScrollPane();
    viewport = scrollPane.getViewport();

    offset = viewport.getSize().height / 2;
    unitIncrement = scrollPane.getHorizontalScrollBar().getUnitIncrement();
    offset = offset - (offset % unitIncrement);

    return offset;
  }

  private int getCorrectionOffset(FilePanel fp)
  {
    JTextComponent editor;
    int            offset;
    Rectangle      rect;
    Point          p;
    JViewport      viewport;

    editor = fp.getEditor();
    viewport = fp.getScrollPane().getViewport();
    p = viewport.getViewPosition();
    offset = editor.viewToModel(p);

    try
    {
      // This happens when you scroll to the bottom. The upper line won't
      //   start at the right position (You can see half of the line)
      // Correct this offset with the pane next to it to keep in sync.
      rect = editor.modelToView(offset);
      if (rect != null)
      {
        return p.y - rect.getLocation().y;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return 0;
  }
}
