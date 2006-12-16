package org.jmeld.ui;

import org.jmeld.ui.swing.*;
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
  private JLabel    statusLabel;
  private BusyLabel busyLabel;
  private Timer     timer;

  private StatusBar()
  {
    setLayout(new BorderLayout());

    statusLabel = new JLabel(" ");
    statusLabel.setBorder(
      new CompoundBorder(
        new EmptyBorder(2, 0, 2, 5),
        new CompoundBorder(
          new LineBorder(UIManager.getColor("controlShadow")),
          new EmptyBorder(2, 2, 2, 2))));

    busyLabel = new BusyLabel();

    add(statusLabel, BorderLayout.CENTER);
    add(busyLabel, BorderLayout.EAST);

    timer = new Timer(
        3000,
        clearText());
    timer.setRepeats(false);

    setMinimumSize(new Dimension(25, 25));
    setPreferredSize(new Dimension(25, 25));
  }

  public static StatusBar getInstance()
  {
    return instance;
  }

  public static void start()
  {
    instance.busyLabel.start();
  }

  public static void setStatus(String text)
  {
    instance.statusLabel.setText(text);
  }

  public static void stop()
  {
    instance.timer.restart();
    instance.busyLabel.stop();
  }

  private void clear()
  {
    statusLabel.setText("");
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
