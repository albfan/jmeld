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
package org.jmeld.ui.swing;

import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.swing.text.*;

import java.awt.*;

public class JMHighlightPainter
       extends DefaultHighlighter.DefaultHighlightPainter
       implements ConfigurationListenerIF
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
    ADDED.initConfiguration();
    ADDED_LINE = new JMHighlightPainter(Colors.ADDED, true);
    ADDED_LINE.initConfiguration();
    CHANGED = new JMHighlightPainter(Colors.CHANGED);
    CHANGED.initConfiguration();
    CHANGED2 = new JMHighlightPainter(Colors.CHANGED2);
    CHANGED2.initConfiguration();
    DELETED = new JMHighlightPainter(Colors.DELETED);
    DELETED.initConfiguration();
    DELETED_LINE = new JMHighlightPainter(Colors.DELETED, true);
    DELETED_LINE.initConfiguration();
    SEARCH = new JMHighlightPainter(Color.yellow);
    SEARCH.initConfiguration();
    CURRENT_SEARCH = new JMHighlightPainter(Color.yellow.darker());
    CURRENT_SEARCH.initConfiguration();
  }

  private Color   color;
  private boolean line;
  private boolean debug;

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

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  public void paint(
    Graphics       g,
    int            p0,
    int            p1,
    Shape          shape,
    JTextComponent comp)
  {
    Rectangle b;
    Rectangle r1;
    Rectangle r2;
    int       x;
    int       y;
    int       width;
    int       count;

    b = shape.getBounds();

    try
    {
      r1 = comp.modelToView(p0);
      r2 = comp.modelToView(p1);

      g.setColor(color);
      if (line)
      {
        g.drawLine(0, r1.y, b.x + b.width, r1.y);
      }
      else
      {
        if (this == CHANGED2 || this == SEARCH || this == CURRENT_SEARCH)
        {
          if (r1.y == r2.y)
          {
            g.fillRect(r1.x, r1.y, r2.x - r1.x, r1.height);
          }
          else
          {
            debug("r1: x=%d,y=%d,width=%d,height=%d\n", r1.x, r1.y, r1.width,
              r1.height);
            debug("r2: x=%d,y=%d,width=%d,height=%d\n", r2.x, r2.y, r2.width,
              r2.height);

            count = ((r2.y - r1.y) / r1.height) + 1;
            debug("count = " + count);
            y = r1.y;
            for (int i = 0; i < count; i++, y += r1.height)
            {
              if (i == 0)
              {
                // firstline:
                x = r1.x;
                width = b.width - b.x;
                debug("first     : ");
              }
              else if (i == count - 1)
              {
                // lastline:
                x = b.x;
                width = r2.x - x;
                debug("last      : ");
              }
              else
              {
                // all lines in between the first and the lastline:
                x = b.x;
                width = b.width - b.x;
                debug("in between: ");
              }

              debug("x=%d,y=%d,width=%d,height=%d\n", x, y, width, r1.height);
              g.fillRect(x, y, width, r1.height);
            }
          }
        }
        else
        {
          g.fillRect(0, r1.y, b.x + b.width, r2.y - r1.y);
        }
      }
    }
    catch (BadLocationException ex)
    {
      ex.printStackTrace();
    }
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
    if (this == ADDED || this == ADDED_LINE)
    {
      color = getSettings().getAddedColor();
    }
    else if (this == DELETED || this == DELETED_LINE)
    {
      color = getSettings().getDeletedColor();
    }
    else if (this == CHANGED)
    {
      color = getSettings().getChangedColor();
    }
  }

  private EditorSettings getSettings()
  {
    return JMeldSettings.getInstance().getEditor();
  }

  private void debug(String format, Object... args)
  {
    if(debug)
    {
      System.out.printf(format, args);
    }
  }
}
