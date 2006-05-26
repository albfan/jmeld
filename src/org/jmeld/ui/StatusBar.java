package org.jmeld.ui;

import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class StatusBar
       extends JPanel
{
  // Class variables:
  private static StatusBar instance = new StatusBar();

  // Instance variables:
  private JLabel          statusLabel;
  private JLabel          busyLabel;
  private Timer           timer;
  private boolean busy;
  private Timer           busyTimer;
  private int             busyIndex;
  private ImageIcon       notBusyIcon;
  private List<ImageIcon> busyIcons;

  private StatusBar()
  {
    setLayout(new BorderLayout());

    notBusyIcon = ResourceLoader.getImageIcon("busy-off");
    busyIcons = new ArrayList<ImageIcon>();
    for (int i = 0; i < 8; i++)
    {
      busyIcons.add(ResourceLoader.getImageIcon("busy-on" + (i + 1)));
    }

    statusLabel = new JLabel(" ");
    statusLabel.setBorder(
      new CompoundBorder(
        new EmptyBorder(2, 0, 2, 5),
        new CompoundBorder(
          new LineBorder(Color.lightGray),
          new EmptyBorder(2, 2, 2, 2))));

    busyLabel = new JLabel();
    busyLabel.setIcon(notBusyIcon);

    add(statusLabel, BorderLayout.CENTER);
    add(busyLabel, BorderLayout.EAST);

    timer = new Timer(
        3000,
        clearText());
    timer.setRepeats(false);

    busyTimer = new Timer(
        125,
        busy());
    timer.setRepeats(false);

    setMinimumSize(new Dimension(30, 30));
    setPreferredSize(new Dimension(30, 30));
  }

  public static StatusBar getInstance()
  {
    return instance;
  }

  public static void start()
  {
    instance.busy = true;
    instance.busyIndex = 0;
    instance.busyTimer.restart();
  }

  public static void setStatus(String text)
  {
    instance.statusLabel.setText(text);
  }

  public static void stop()
  {
    //instance.busy = false;
    instance.timer.restart();
  }

  private void clear()
  {
    statusLabel.setText("");
  }

  private ActionListener busy()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          if (busy)
          {
            busyLabel.setIcon(busyIcons.get(busyIndex));
            busyIndex++;
            if (busyIndex >= busyIcons.size())
            {
              busyIndex = 0;
            }

            busyTimer.restart();
          }
          else
          {
            busyLabel.setIcon(notBusyIcon);
          }
        }
      };
  }

  private ActionListener clearText()
  {
    return new ActionListener()
      {
        public void actionPerformed(ActionEvent ae)
        {
          clear();
        }
      };
  }
}
