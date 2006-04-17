package org.jmeld.ui;

import org.apache.commons.jrcs.diff.*;
import org.jmeld.ui.text.*;
import org.jmeld.ui.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DiffScrollComponent
       extends JComponent
       implements ChangeListener
{
  private BufferDiffPanel diffPanel;
  private FilePanel       filePanelOriginal;
  private FilePanel       filePanelRevised;
  private List<Command>   commands;
  private Object          antiAlias;

  public DiffScrollComponent(BufferDiffPanel diffPanel,
    FilePanel filePanelOriginal, FilePanel filePanelRevised)
  {
    this.diffPanel = diffPanel;
    this.filePanelOriginal = filePanelOriginal;
    this.filePanelRevised = filePanelRevised;

    filePanelOriginal.getScrollPane().getViewport().addChangeListener(this);
    filePanelRevised.getScrollPane().getViewport().addChangeListener(this);

    addMouseListener(getMouseListener());
    addMouseWheelListener(getMouseWheelListener());
  }

  public void stateChanged(ChangeEvent event)
  {
    repaint();
  }

  private MouseWheelListener getMouseWheelListener()
  {
    return new MouseWheelListener()
      {
        public void mouseWheelMoved(MouseWheelEvent me)
        {
          diffPanel.toNextDelta(me.getWheelRotation() > 0);
        }
      };
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mouseClicked(MouseEvent me)
        {
          executeCommand((double) me.getX(), (double) me.getY());
        }
      };
  }

  public boolean executeCommand(double x, double y)
  {
    if (commands == null)
    {
      return false;
    }

    for (Command command : commands)
    {
      if (command.contains(x, y))
      {
        command.execute();
        return true;
      }
    }

    return false;
  }

  public void paint(Graphics g)
  {
    Rectangle  r;
    int        middle;
    Graphics2D g2;

    g2 = (Graphics2D) g;

    r = g.getClipBounds();
    g2.setColor(getBackground());
    g2.fill(r);

    middle = r.height / 2;
    g2.setColor(Color.LIGHT_GRAY);
    g2.drawLine(r.x + 15, r.y + middle, r.x + r.width - 15, r.y + middle);

    drawDiffs(g2);
  }

  private void drawDiffs(Graphics2D g2)
  {
    JViewport        viewportOriginal;
    JViewport        viewportRevised;
    JTextComponent   editorOriginal;
    JTextComponent   editorRevised;
    Revision         revision;
    BufferDocumentIF bdOriginal;
    BufferDocumentIF bdRevised;
    int              firstLineOriginal;
    int              lastLineOriginal;
    int              firstLineRevised;
    int              lastLineRevised;
    int              offset;
    Rectangle        r;
    Point            p;
    Delta            delta;
    Chunk            original;
    Chunk            revised;
    Rectangle        viewportRect;
    Rectangle        fromRect;
    Rectangle        toRect;
    int              fromLine;
    int              toLine;
    int              x;
    int              y;
    int              width;
    int              height;
    Rectangle        bounds;
    int              x0;
    int              y0;
    int              x1;
    int              y1;
    Color            color;
    Polygon          shape;
    Rectangle        rect;

    bounds = g2.getClipBounds();

    revision = diffPanel.getCurrentRevision();
    if (revision == null)
    {
      return;
    }

    commands = new ArrayList<Command>();

    antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

    // Original side:
    viewportOriginal = filePanelOriginal.getScrollPane().getViewport();
    editorOriginal = filePanelOriginal.getEditor();
    bdOriginal = filePanelOriginal.getBufferDocument();
    r = viewportOriginal.getViewRect();

    // Calculate firstLine shown of the first document. 
    p = new Point(r.x, r.y);
    offset = editorOriginal.viewToModel(p);
    firstLineOriginal = bdOriginal.getLineForOffset(offset);

    // Calculate lastLine shown of the first document. 
    p = new Point(r.x, r.y + r.height);
    offset = editorOriginal.viewToModel(p);
    bdOriginal = filePanelOriginal.getBufferDocument();
    lastLineOriginal = bdOriginal.getLineForOffset(offset) + 1;

    // Revised side:
    viewportRevised = filePanelRevised.getScrollPane().getViewport();
    editorRevised = filePanelRevised.getEditor();
    bdRevised = filePanelRevised.getBufferDocument();
    r = viewportRevised.getViewRect();

    // Calculate firstLine shown of the second document. 
    p = new Point(r.x, r.y);
    offset = editorRevised.viewToModel(p);
    firstLineRevised = bdRevised.getLineForOffset(offset);

    // Calculate lastLine shown of the second document. 
    p = new Point(r.x, r.y + r.height);
    offset = editorRevised.viewToModel(p);
    lastLineRevised = bdRevised.getLineForOffset(offset) + 1;

    try
    {
      // Draw only the delta's that have some line's drawn in one of the viewports.
      for (int i = 0; i < revision.size(); i++)
      {
        delta = revision.getDelta(i);
        original = delta.getOriginal();
        revised = delta.getRevised();

        // This delta is before the firstLine of the screen: Keep on searching!
        if (original.anchor() + original.size() < firstLineOriginal
          && revised.anchor() + revised.size() < firstLineRevised)
        {
          continue;
        }

        // This delta is after the lastLine of the screen: stop! 
        if (original.anchor() > lastLineOriginal
          && revised.anchor() > lastLineRevised)
        {
          break;
        }

        // OK, this delta has some visible lines. Now draw it!
        if (delta instanceof DeleteDelta)
        {
          color = Colors.DELETED;
        }
        else if (delta instanceof ChangeDelta)
        {
          color = Colors.CHANGED;
        }
        else
        {
          color = Colors.ADDED;
        }

        g2.setColor(color);

        // Draw original chunk:
        fromLine = original.anchor();
        toLine = original.anchor() + original.size();
        viewportRect = viewportOriginal.getViewRect();
        offset = bdOriginal.getOffsetForLine(fromLine);
        fromRect = editorOriginal.modelToView(offset);
        offset = bdOriginal.getOffsetForLine(toLine);
        toRect = editorOriginal.modelToView(offset);

        x = 0;
        y = fromRect.y - viewportRect.y + 1;
        y = y < 0 ? 0 : y;
        width = 10;
        height = 0;
        if (fromRect.y <= viewportRect.y
          && toRect.y <= viewportRect.y + viewportRect.height)
        {
          height = toRect.y - viewportRect.y - 1;
        }
        else if (fromRect.y > viewportRect.y
          && toRect.y > viewportRect.y + viewportRect.height)
        {
          height = viewportRect.y + viewportRect.height - fromRect.y - 1;
        }
        else if (fromRect.y > viewportRect.y
          && toRect.y <= viewportRect.y + viewportRect.height)
        {
          height = toRect.y - fromRect.y - 1;
        }
        else if (fromRect.y <= viewportRect.y
          && toRect.y > viewportRect.y + viewportRect.height)
        {
          height = viewportRect.height - 1;
        }

        x0 = x + width;
        y0 = y;

        if (height > 0)
        {
          g2.setColor(color);
          g2.fillRect(x, y, width, height);
        }

        g2.setColor(color.darker());
        g2.drawLine(x, y, x + width, y);
        if (height > 0)
        {
          g2.drawLine(x + width, y, x + width, y + height);
          g2.drawLine(x, y + height, x + width, y + height);
        }

        // Draw revised chunk:
        fromLine = revised.anchor();
        toLine = revised.anchor() + revised.size();
        viewportRect = viewportRevised.getViewRect();
        offset = bdRevised.getOffsetForLine(fromLine);
        fromRect = editorRevised.modelToView(offset);
        offset = bdRevised.getOffsetForLine(toLine);
        toRect = editorRevised.modelToView(offset);

        x = bounds.x + bounds.width - 10;
        y = fromRect.y - viewportRect.y + 1;
        y = y < 0 ? 0 : y;
        width = 10;
        height = 0;
        if (fromRect.y <= viewportRect.y
          && toRect.y <= viewportRect.y + viewportRect.height)
        {
          height = toRect.y - viewportRect.y - 1;
        }
        else if (fromRect.y > viewportRect.y
          && toRect.y > viewportRect.y + viewportRect.height)
        {
          height = viewportRect.y + viewportRect.height - fromRect.y - 1;
        }
        else if (fromRect.y > viewportRect.y
          && toRect.y <= viewportRect.y + viewportRect.height)
        {
          height = toRect.y - fromRect.y - 1;
        }
        else if (fromRect.y <= viewportRect.y
          && toRect.y > viewportRect.y + viewportRect.height)
        {
          height = viewportRect.height - 1;
        }

        x1 = x;
        y1 = y;

        if (height > 0)
        {
          g2.setColor(color);
          g2.fillRect(x, y, width, height);
        }

        g2.setColor(color.darker());
        g2.drawLine(x, y, x + width, y);
        if (height > 0)
        {
          g2.drawLine(x, y, x, y + height);
          g2.drawLine(x, y + height, x + width, y + height);
        }

        // Draw the chunk connection:
        g2.drawLine(x0, y0, x0 + 15, y0);
        setAntiAlias(g2);
        g2.drawLine(x0 + 15, y0, x1 - 15, y1);
        resetAntiAlias(g2);
        g2.drawLine(x1 - 15, y1, x1, y1);

        // Draw merge revised->original command.
        shape = new Polygon();
        shape.addPoint(x0, y0);
        shape.addPoint(x0 + 11, y0 - 4);
        shape.addPoint(x0 + 11, y0 + 4);
        setAntiAlias(g2);
        g2.setColor(color);
        g2.fill(shape);
        g2.setColor(color.darker());
        g2.draw(shape);
        resetAntiAlias(g2);
        commands.add(new ChangeCommand(shape, delta, false));

        // Draw delete original command
        if (original.size() > 0)
        {
          g2.setColor(Color.red);
          g2.drawLine(x0 + 3 - width, y0 + 3, x0 + 7 - width, y0 + 7);
          g2.drawLine(x0 + 7 - width, y0 + 3, x0 + 3 - width, y0 + 7);
          rect = new Rectangle(x0 + 2 - width, y0 + 2, 6, 6);
          commands.add(new DeleteCommand(rect, delta, true));
        }

        // Draw merge original->revised command.
        shape = new Polygon();
        shape.addPoint(x1, y1);
        shape.addPoint(x1 - 11, y1 - 4);
        shape.addPoint(x1 - 11, y1 + 4);
        setAntiAlias(g2);
        g2.setColor(color);
        g2.fillPolygon(shape);
        g2.setColor(color.darker());
        g2.drawPolygon(shape);
        resetAntiAlias(g2);
        commands.add(new ChangeCommand(shape, delta, true));

        // Draw delete revision command
        if (revised.size() > 0)
        {
          g2.setColor(Color.red);
          g2.drawLine(x1 + 3, y1 + 3, x1 + 7, y1 + 7);
          g2.drawLine(x1 + 7, y1 + 3, x1 + 3, y1 + 7);
          rect = new Rectangle(x1 + 2, y1 + 2, 6, 6);
          commands.add(new DeleteCommand(rect, delta, false));
        }
      }
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }

    resetAntiAlias(g2);
  }

  class ChangeCommand
         extends Command
  {
    ChangeCommand(Shape shape, Delta delta, boolean originalToRevised)
    {
      super(shape, delta, originalToRevised);
    }

    public void execute()
    {
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
      Chunk            fromChunk;
      Chunk            toChunk;

      try
      {
        if (originalToRevised)
        {
          fromBufferDocument = filePanelOriginal.getBufferDocument();
          toBufferDocument = filePanelRevised.getBufferDocument();
          fromChunk = delta.getOriginal();
          toChunk = delta.getRevised();
        }
        else
        {
          fromBufferDocument = filePanelRevised.getBufferDocument();
          toBufferDocument = filePanelOriginal.getBufferDocument();
          fromChunk = delta.getRevised();
          toChunk = delta.getOriginal();
        }

        if (fromBufferDocument == null || toBufferDocument == null)
        {
          return;
        }

        from = fromBufferDocument.getDocument();
        to = toBufferDocument.getDocument();

        fromLine = fromChunk.anchor();
        size = fromChunk.size();
        fromOffset = fromBufferDocument.getOffsetForLine(fromLine);
        toOffset = fromBufferDocument.getOffsetForLine(fromLine + size);

        s = from.getText(fromOffset, toOffset - fromOffset);

        fromLine = toChunk.anchor();
        size = toChunk.size();
        fromOffset = toBufferDocument.getOffsetForLine(fromLine);
        toOffset = toBufferDocument.getOffsetForLine(fromLine + size);

        to.replace(fromOffset, toOffset - fromOffset, s, null);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  class DeleteCommand
         extends Command
  {
    DeleteCommand(Shape shape, Delta delta, boolean originalToRevised)
    {
      super(shape, delta, originalToRevised);
    }

    public void execute()
    {
      BufferDocumentIF bufferDocument;
      PlainDocument    document;
      String           s;
      int              fromLine;
      int              fromOffset;
      int              toOffset;
      int              size;
      Chunk            chunk;

      try
      {
        if (originalToRevised)
        {
          bufferDocument = filePanelOriginal.getBufferDocument();
          chunk = delta.getOriginal();
        }
        else
        {
          bufferDocument = filePanelRevised.getBufferDocument();
          chunk = delta.getRevised();
        }

        if (bufferDocument == null)
        {
          return;
        }

        document = bufferDocument.getDocument();

        fromLine = chunk.anchor();
        size = chunk.size();
        fromOffset = bufferDocument.getOffsetForLine(fromLine);
        toOffset = bufferDocument.getOffsetForLine(fromLine + size);

        document.remove(fromOffset, toOffset - fromOffset);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  abstract class Command
  {
    Rectangle bounds;
    Delta     delta;
    boolean   originalToRevised;

    Command(Shape shape, Delta delta, boolean originalToRevised)
    {
      this.bounds = shape.getBounds();
      this.delta = delta;
      this.originalToRevised = originalToRevised;
    }

    boolean contains(double x, double y)
    {
      return bounds.contains(x, y);
    }

    public abstract void execute();
  }

  private void setAntiAlias(Graphics2D g2)
  {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
  }

  private void resetAntiAlias(Graphics2D g2)
  {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
  }
}
