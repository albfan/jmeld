package org.jmeld.ui.util;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class TabIcon
       implements Icon
{
  private static int  CLOSE_ICON_HEIGHT = 7;
  private static int  CLOSE_ICON_WIDTH = 7;
  private static int  SPACE_WIDTH = 6;
  private Icon        icon;
  private String      text;
  private int         width;
  private int         height;
  private JLabel      label;
  private int         stringWidth;
  private Rectangle   closeBounds;
  private JTabbedPane tabbedPane;

  public TabIcon(Icon icon, String text)
  {
    this.icon = icon;
    this.text = text;

    init();
  }

  private void init()
  {
    Font        font;
    FontMetrics fm;

    height = 0;
    width = 0;

    if (icon != null)
    {
      height = height < icon.getIconHeight() ? icon.getIconHeight() : height;
      width = width + icon.getIconWidth();
    }

    if (text != null)
    {
      label = new JLabel(text);
      font = label.getFont();
      fm = label.getFontMetrics(font);

      height = height < fm.getHeight() ? fm.getHeight() : height;
      stringWidth = fm.stringWidth(text);
      width += stringWidth;
    }

    height = height < CLOSE_ICON_HEIGHT ? CLOSE_ICON_HEIGHT : height;
    width += CLOSE_ICON_WIDTH;

    if (icon != null)
    {
      width += SPACE_WIDTH;
    }

    if (text != null)
    {
      width += SPACE_WIDTH;
    }
  }

  public int getIconWidth()
  {
    return width;
  }

  public int getIconHeight()
  {
    return height;
  }

  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    FontMetrics fm;
    int         x0;
    int         y0;
    Rectangle   b;

    x0 = x;

    if (tabbedPane == null)
    {
      tabbedPane = (JTabbedPane) c;
      tabbedPane.addMouseListener(getMouseListener());
    }

    if (icon != null)
    {
      icon.paintIcon(c, g, x0, y);
      x0 += icon.getIconWidth();
      x0 += SPACE_WIDTH;
    }

    if (text != null)
    {
      fm = label.getFontMetrics(label.getFont());
      y0 = y + fm.getAscent() + ((height - fm.getHeight()) / 2);

      g.setFont(label.getFont());
      g.drawString(text, x0, y0);

      x0 += stringWidth;
      x0 += SPACE_WIDTH;
    }

    y0 = y + (height - CLOSE_ICON_HEIGHT) / 2;
    g.drawLine(x0, y0, x0 + CLOSE_ICON_HEIGHT, y0 + CLOSE_ICON_WIDTH);
    g.drawLine(x0 + CLOSE_ICON_HEIGHT, y0, x0, y0 + CLOSE_ICON_WIDTH);
    closeBounds = new Rectangle(x0, y0, CLOSE_ICON_WIDTH, CLOSE_ICON_HEIGHT);

    x0 += CLOSE_ICON_WIDTH;
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mousePressed(MouseEvent me)
        {
          int index;

          if (!me.isConsumed() && closeBounds != null
            && closeBounds.contains(me.getX(), me.getY()))
          {
            index = tabbedPane.indexOfTab(TabIcon.this);
            if (index != -1)
            {
              tabbedPane.remove(index);
              me.consume();
            }
          }
        }
      };
  }
}
