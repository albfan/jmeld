package org.jmeld.ui;

import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class BusyLabel
       extends JLabel
{
  // Instance variables:
  private Timer    timer;
  private boolean  busy;
  private BusyIcon icon;

  public BusyLabel()
  {
    icon = new BusyIcon();
    setIcon(icon);

    timer = new Timer(
        125,
        busy());
    timer.setRepeats(false);
  }

  public void start()
  {
    busy = true;
    timer.restart();
  }

  public void stop()
  {
    busy = false;
  }

  private ActionListener busy()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          if (busy)
          {
            icon.roll();
            repaint();
            timer.restart();
          }
          else
          {
            icon.stop();
            repaint();
          }
        }
      };
  }

  class BusyIcon
         implements Icon
  {
    private int startIndex;
    List<Color> colors;

    BusyIcon()
    {
      colors = new ArrayList<Color>();
      colors.add( new Color(178, 178, 178));
      colors.add( new Color(153, 153, 153));
      colors.add( new Color(128, 128, 128));
      colors.add( new Color(102, 102, 102));
      colors.add( new Color(51, 51, 51));
      colors.add( new Color(26, 26, 26));
      colors.add( new Color(0, 0, 0));
      colors.add( new Color(0, 0, 0));
    }

    void setIndex(int startIndex)
    {
      this.startIndex = startIndex;
    }

    public void stop()
    {
      startIndex = 0;
    }

    public void roll()
    {
      startIndex--;
      if (startIndex < 0)
      {
        startIndex = 7;
      }
    }

    public int getIconWidth()
    {
      return 16;
    }

    public int getIconHeight()
    {
      return 16;
    }

    public void paintIcon(
      Component component,
      Graphics  g,
      int       x,
      int       y)
    {
      Color c;
      int   tx;
      int   ty;

      for (int i = 0; i < 8; i++)
      {
        tx = 0;
        ty = 0;

        if (busy)
        {
          c = colors.get((i + startIndex) % 8);
        }
        else
        {
          c = colors.get(0);
        }

        switch (i)
        {

          case 0:
            tx = 10;
            ty = 2;
            break;

          case 1:
            tx = 12;
            ty = 6;
            break;

          case 2:
            tx = 10;
            ty = 10;
            break;

          case 3:
            tx = 6;
            ty = 12;
            break;

          case 4:
            tx = 2;
            ty = 10;
            break;

          case 5:
            tx = 0;
            ty = 6;
            break;

          case 6:
            tx = 2;
            ty = 2;
            break;

          case 7:
            tx = 6;
            ty = 0;
            break;
        }

        g.setColor(c);
        g.drawLine(x + tx + 0, y + ty + 1, x + tx + 0, y + ty + 2);
        g.drawLine(x + tx + 1, y + ty + 0, x + tx + 1, y + ty + 3);
        g.drawLine(x + tx + 2, y + ty + 0, x + tx + 2, y + ty + 3);
        g.drawLine(x + tx + 3, y + ty + 1, x + tx + 3, y + ty + 2);
      }
    }
  }
}
