package org.jmeld.ui.swing;

import org.jmeld.ui.util.*;

import javax.swing.text.*;

import java.awt.*;

public class JMHighlightPainter
       extends DefaultHighlighter.DefaultHighlightPainter
{
  public static final JMHighlightPainter ADDED;
  public static final JMHighlightPainter ADDED_LINE;
  public static final JMHighlightPainter CHANGED;
  public static final JMHighlightPainter CHANGED2;
  public static final JMHighlightPainter DELETED;
  public static final JMHighlightPainter DELETED_LINE;
  public static final JMHighlightPainter CURRENT_SEARCH;
  public static final JMHighlightPainter SEARCH;

  static
  {
    ADDED = new JMHighlightPainter(Colors.ADDED);
    ADDED_LINE = new JMHighlightPainter(Colors.ADDED, true);
    CHANGED = new JMHighlightPainter(Colors.CHANGED);
    CHANGED2 = new JMHighlightPainter(Colors.CHANGED2);
    DELETED = new JMHighlightPainter(Colors.DELETED);
    DELETED_LINE = new JMHighlightPainter(Colors.DELETED, true);
    SEARCH = new JMHighlightPainter(Color.yellow);
    CURRENT_SEARCH = new JMHighlightPainter(Color.yellow.darker());
  }

  private Color   color;
  private boolean line;

  private JMHighlightPainter(Color color)
  {
    this(color, false);
  }

  private JMHighlightPainter(
    Color   color,
    boolean line)
  {
    super(color);

    this.color = color;
    this.line = line;
  }

  public void paint(
    Graphics       g,
    int            p0,
    int            p1,
    Shape          shape,
    JTextComponent comp
    )
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
        if (this == CHANGED2 || this == SEARCH || this == CURRENT_SEARCH)
        {
          g.fillRect(r1.x, r1.y, r2.x - r1.x, r1.height);
        }
        else
        {
          g.fillRect(r1.x, r1.y, r1.x + b.width, r2.y - r1.y);
        }
      }
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }
  }
}
