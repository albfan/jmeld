package org.jmeld.ui;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

public class RevisionBar
       extends JComponent
{
  private BufferDiffPanel diffPanel;
  private FilePanel       filePanel;
  private boolean         original;

  public RevisionBar(
    BufferDiffPanel diffPanel,
    FilePanel       filePanel,
    boolean         original)
  {
    this.diffPanel = diffPanel;
    this.filePanel = filePanel;
    this.original = original;

    setBorder(BorderFactory.createLineBorder(Color.lightGray));

    addMouseListener(getMouseListener());
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mouseClicked(MouseEvent me)
        {
          Rectangle        r;
          int              y;
          int              line;
          Revision         revision;
          Point            p;
          BufferDocumentIF bd;
          int              offset;
          JTextComponent   editor;
          JViewport        viewport;

          r = getBounds();
          y = me.getY();

          revision = diffPanel.getCurrentRevision();
          if (revision == null)
          {
            return;
          }

          line = (y * getNumberOfLines(revision)) / r.height;

          bd = filePanel.getBufferDocument();
          offset = bd.getOffsetForLine(line);
          viewport = filePanel.getScrollPane().getViewport();
          editor = filePanel.getEditor();

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
      };
  }

  public void paintComponent(Graphics g)
  {
    Rectangle  r;
    Graphics2D g2;
    Revision   revision;
    Chunk      chunk;
    Delta      delta;
    int        y;
    int        height;
    int        numberOfLines;

    g2 = (Graphics2D) g;

    r = g.getClipBounds();
    g2.setColor(Color.white);
    g2.fill(r);

    revision = diffPanel.getCurrentRevision();
    if (revision == null)
    {
      return;
    }

    numberOfLines = getNumberOfLines(revision);
    if (numberOfLines <= 0)
    {
      return;
    }

    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);
      chunk = original ? delta.getOriginal() : delta.getRevised();

      g.setColor(RevisionUtil.getColor(delta));
      y = (r.height * chunk.anchor()) / numberOfLines;
      height = (r.height * chunk.size()) / numberOfLines;
      if (height <= 0)
      {
        height = 1;
      }

      g.fillRect(0, y, r.width, height);
    }
  }

  private int getNumberOfLines(Revision revision)
  {
    return original ? revision.getOrgSize() : revision.getRevSize();
  }
}
