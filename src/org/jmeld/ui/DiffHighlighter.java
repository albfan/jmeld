package org.jmeld.ui;

import org.jmeld.ui.util.*;

import javax.swing.text.*;

import java.awt.*;

public class DiffHighlighter
       extends DefaultHighlighter.DefaultHighlightPainter
{
  public static final DiffHighlighter ADDED;
  public static final DiffHighlighter ADDED_LINE;
  public static final DiffHighlighter CHANGED;
  public static final DiffHighlighter CHANGED2;
  public static final DiffHighlighter DELETED;
  public static final DiffHighlighter DELETED_LINE;

  static
  {
    ADDED = new DiffHighlighter(Colors.ADDED);
    ADDED_LINE = new DiffHighlighter(Colors.ADDED, true);
    CHANGED = new DiffHighlighter(Colors.CHANGED);
    CHANGED2 = new DiffHighlighter(Colors.CHANGED2);
    DELETED = new DiffHighlighter(Colors.DELETED);
    DELETED_LINE = new DiffHighlighter(Colors.DELETED, true);
  }

  private Color   color;
  private boolean line;

  private DiffHighlighter(Color color)
  {
    this(color, false);
  }

  private DiffHighlighter(
    Color   color,
    boolean line)
  {
    super(color);

    this.color = color;
    this.line = line;
  }

  public Shape paintLayer(
    Graphics       g,
    int            p0,
    int            p1,
    Shape          shape,
    JTextComponent comp,
    View           view)
  {
    Rectangle b;
    Rectangle r1;
    Rectangle r2;

    b = shape.getBounds();
    try
    {
      r1 = comp.modelToView(p0);
      r2 = comp.modelToView(p1);

      g.setColor(color);
      if (line)
      {
        g.drawLine(r1.x, r1.y, r1.x + b.width, r1.y);
      }
      else
      {
        if (this == CHANGED2)
        {
          g.fillRect(r1.x, r1.y, r2.x - r1.x, r1.height);
        }
        else
        {
          g.fillRect(r1.x, r1.y, r1.x + b.width, r1.height);
        }
      }
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }

    return b;
  }
}
