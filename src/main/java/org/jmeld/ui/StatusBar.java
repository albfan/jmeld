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
package org.jmeld.ui;

import org.jmeld.ui.dnd.DragAndDropPanel;
import org.jmeld.ui.swing.BusyLabel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusBar
    extends JPanel
{
  // Class variables:
  private static StatusBar instance = new StatusBar();

  // Instance variables:
  private JLabel statusLabel;
  private JPanel progressArea;
  private JProgressBar progressBar;
  private BusyLabel busy;
  private DragAndDropPanel dragAndDrop;
  private Timer timer;
  private JPanel notificationArea;

  private StatusBar()
  {
    setLayout(new BorderLayout());

    init();
  }

  public static StatusBar getInstance()
  {
    return instance;
  }

  private void init()
  {
    JPanel panel;

    statusLabel = new JLabel(" ");
    statusLabel.setBorder(new EmptyBorder(4, 2, 4, 2));
    progressBar = new JProgressBar();
    progressBar.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(2,
      5, 2, 5), progressBar.getBorder()));
    progressBar.setStringPainted(true);
    busy = new BusyLabel();
    dragAndDrop = new DragAndDropPanel();

    panel = new JPanel(new BorderLayout());
    add(dragAndDrop, BorderLayout.WEST);
    add(statusLabel, BorderLayout.CENTER);
    add(panel, BorderLayout.EAST);

    notificationArea = new JPanel(new GridLayout(1, 0));
    progressArea = new JPanel(new GridLayout(1, 0));
    panel.add(progressArea, BorderLayout.WEST);
    panel.add(notificationArea, BorderLayout.CENTER);
    panel.add(busy, BorderLayout.EAST);

    timer = new Timer(3000, clearText());
    timer.setRepeats(false);

    setMinimumSize(new Dimension(25, 25));
    setPreferredSize(new Dimension(25, 25));
  }

  public void start()
  {
    busy.start();
  }

  public void setState(String format, Object... args)
  {
    String text;

    text = String.format(format, args);
    statusLabel.setText(text);
  }

  public void setText(String format, Object... args)
  {
    setState(format, args);
    stop();
  }

  public void setAlarm(String format, Object... args)
  {
    statusLabel.setForeground(Color.red);
    setState(format, args);
    stop();
  }

  public void setProgress(int value, int maximum)
  {
    if (progressArea.getComponentCount() == 0)
    {
      progressArea.add(progressBar);
      revalidate();
    }

    if (progressBar.getMaximum() != maximum)
    {
      progressBar.setMaximum(maximum);
    }

    progressBar.setValue(value);
    progressBar.setString(value + "/" + maximum);
  }

  public void stop()
  {
    timer.restart();
    busy.stop();
  }

  private void clear()
  {
    statusLabel.setText("");
    statusLabel.setForeground(Color.black);

    progressArea.removeAll();
    revalidate();
  }

  public void setNotification(String id, ImageIcon icon)
  {
    JLabel label;

    label = new JLabel(icon);
    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

    setNotification(id, label);
  }

  public void setNotification(String id, JComponent component)
  {
    _setNotification(id, component);
  }

  private void _setNotification(String id, JComponent component)
  {
    id = getNotificationId(id);

    // check if notification is already showing!
    if (notificationArea.getClientProperty(id) != null)
    {
      return;
    }

    notificationArea.add(component);
    notificationArea.putClientProperty(id, component);

    revalidate();
  }

  public void removeNotification(String id)
  {
    _removeNotification(id);
  }

  public void _removeNotification(String id)
  {
    JComponent component;

    id = getNotificationId(id);

    component = (JComponent) notificationArea.getClientProperty(id);
    if (component == null)
    {
      return;
    }

    notificationArea.remove(component);
    notificationArea.putClientProperty(id, null);

    revalidate();
  }

  private String getNotificationId(String id)
  {
    return "JMeld.notification." + id;
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
