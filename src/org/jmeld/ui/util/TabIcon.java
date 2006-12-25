package org.jmeld.ui.util;

import org.jmeld.ui.*;

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
  private Icon        currentIcon;
  private Icon        closeIcon;
  private Icon        closeIcon_rollover;
  private Icon        closeIcon_pressed;
  private boolean     pressed;

  public TabIcon(
    Icon   icon,
    String text)
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

    closeIcon = ImageUtil.getImageIcon("jmeld_close");
    closeIcon_rollover = ImageUtil.getImageIcon("jmeld_close-rollover");
    closeIcon_pressed = ImageUtil.getImageIcon("jmeld_close-pressed");
    if (closeIcon != null)
    {
      CLOSE_ICON_WIDTH = closeIcon.getIconWidth();
      CLOSE_ICON_HEIGHT = closeIcon.getIconHeight();
    }

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

  public void paintIcon(
    Component c,
    Graphics  g,
    int       x,
    int       y)
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
      tabbedPane.addMouseMotionListener(getMouseMotionListener());
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
    if (closeIcon != null)
    {
      if (currentIcon == null)
      {
        currentIcon = closeIcon;
      }

      currentIcon.paintIcon(c, g, x0, y0);
      closeBounds = new Rectangle(x0, y0, CLOSE_ICON_WIDTH, CLOSE_ICON_HEIGHT);
    }
    else
    {
      g.drawLine(x0, y0, x0 + CLOSE_ICON_HEIGHT, y0 + CLOSE_ICON_WIDTH);
      g.drawLine(x0 + CLOSE_ICON_HEIGHT, y0, x0, y0 + CLOSE_ICON_WIDTH);
      closeBounds = new Rectangle(x0, y0, CLOSE_ICON_WIDTH, CLOSE_ICON_HEIGHT);
    }

    x0 += CLOSE_ICON_WIDTH;
  }

  private MouseListener getMouseListener()
  {
    return new MouseAdapter()
      {
        public void mousePressed(MouseEvent me)
        {
          Icon icon;

          if (!me.isConsumed() && closeBounds != null
            && closeBounds.contains(
              me.getX(),
              me.getY()))
          {
            pressed = true;

            icon = closeIcon_pressed;
            if (currentIcon != icon)
            {
              currentIcon = icon;
              tabbedPane.repaint();
            }
          }
          else
          {
            pressed = false;
          }
        }

        public void mouseReleased(MouseEvent me)
        {
          int       index;
          Component component;

          if (!me.isConsumed() && closeBounds != null
            && closeBounds.contains(
              me.getX(),
              me.getY()))
          {
            index = tabbedPane.indexOfTab(TabIcon.this);
            if (index != -1)
            {
              component = tabbedPane.getComponentAt(index);
              if (component instanceof BufferDiffPanel)
              {
                if (!((BufferDiffPanel) component).checkSave())
                {
                  me.consume();
                  return;
                }
              }

              tabbedPane.remove(index);
              me.consume();
            }
          }

          if (currentIcon != closeIcon)
          {
            currentIcon = closeIcon;
            tabbedPane.repaint();
          }
        }
      };
  }

  private MouseMotionListener getMouseMotionListener()
  {
    return new MouseMotionAdapter()
      {
        public void mouseMoved(MouseEvent me)
        {
          Icon icon;

          if (!me.isConsumed() && closeBounds != null
            && closeBounds.contains(
              me.getX(),
              me.getY()))
          {
            icon = closeIcon_rollover;
          }
          else
          {
            pressed = false;
            icon = closeIcon;
          }

          if (icon != currentIcon)
          {
            currentIcon = icon;
            tabbedPane.repaint();
          }
        }
      };
  }
}
