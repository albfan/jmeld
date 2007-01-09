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
  private JLabel       status;
  private JProgressBar progress;
  private BusyLabel    busy;
  private Timer        timer;
  private JPanel       notificationArea;

  private StatusBar()
  {
    setLayout(new BorderLayout());

    init();
  }

  private void init()
  {
    JPanel panel;

    status = new JLabel(" ");
    status.setBorder(new EmptyBorder(4, 2, 4, 2));
    progress = new JProgressBar();
    busy = new BusyLabel();

    panel = new JPanel(new BorderLayout());
    add(status, BorderLayout.CENTER);
    add(panel, BorderLayout.EAST);

    notificationArea = new JPanel(new GridLayout(1, 0));
    panel.add(notificationArea, BorderLayout.CENTER);
    panel.add(busy, BorderLayout.EAST);

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
    instance.busy.start();
  }

  public static void setStatus(String text)
  {
    instance.status.setText(text);
  }

  public static void setStatus(
    int    progress,
    String text)
  {
    instance.status.setText(text);
    instance.progress.setValue(progress);
  }

  public static void stop()
  {
    instance.timer.restart();
    instance.busy.stop();
  }

  private void clear()
  {
    status.setText("");
    progress.setValue(0);
  }

  public static void setNotification(String id, ImageIcon icon)
  {
    JLabel label;

    label = new JLabel(icon);
    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

    setNotification(id, label);
  }

  public static void setNotification(String id, JComponent component)
  {
    instance._setNotification(id, component);
  }

  private void _setNotification(String id, JComponent component)
  {
    id = getNotificationId(id);

    // check if notification is already showing!
    if(notificationArea.getClientProperty(id) != null)
    {
      return;
    }

    notificationArea.add(component);
    notificationArea.putClientProperty(id, component);
    
    revalidate();
  }

  public static void removeNotification(String id)
  {
    instance._removeNotification(id);
  }

  public void _removeNotification(String id)
  {
    JComponent component;
    
    id = getNotificationId(id);

    component = (JComponent) notificationArea.getClientProperty(id);
    if(component == null)
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
