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
package org.jmeld;

import org.jmeld.settings.*;
import org.jmeld.ui.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JMeld
    implements Runnable
{
  private List<String> fileNameList;
  private static JMeldPanel jmeldPanel;

  public JMeld(String[] args)
  {
    fileNameList = new ArrayList<String>();
    for (String arg : args)
    {
      fileNameList.add(arg);
    }
  }

  public static JMeldPanel getJMeldPanel()
  {
    return jmeldPanel;
  }

  public void run()
  {
    JFrame frame;

    debugKeyboard();

    LookAndFeelManager.getInstance().install();

    frame = new JFrame("JMeld");
    jmeldPanel = new JMeldPanel();
    frame.add(jmeldPanel);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setIconImage(ImageUtil.getImageIcon("jmeld-small").getImage());
    new WindowPreference(frame.getTitle(), frame);
    frame.addWindowListener(jmeldPanel.getWindowListener());
    //frame.getRootPane().setTransferHandler(new FileDropHandler());
    frame.setVisible(true);

    // Just to keep the damned metacity happy
    frame.toFront();

    jmeldPanel.openComparison(fileNameList);
  }

  private void debugKeyboard()
  {
    KeyboardFocusManager
        .setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager()
        {
          @Override
          public boolean dispatchKeyEvent(KeyEvent e)
          {
            //System.out.println("dispatch: " + KeyStroke.getKeyStrokeForEvent(e));
            //System.out.println("   event: " + e);
            return super.dispatchKeyEvent(e);
          }

          @Override
          public void processKeyEvent(Component focusedComponent, KeyEvent e)
          {
            //System.out.println("processKeyEvent[" + focusedComponent.getClass()
            //+ "] : " + KeyStroke.getKeyStrokeForEvent(e));
            super.processKeyEvent(focusedComponent, e);
          }
        });
  }

  public static void main(String[] args)
  {
    //e.debug.EventDispatchThreadHangMonitor.initMonitoring();
    if (JMeldSettings.getInstance().getEditor().isAntialiasEnabled())
    {
      System.setProperty("swing.aatext", "true");
    }

    // According to the latest news EVERYTHING regarding swing should
    //   be executed on the EventDispatchThread
    SwingUtilities.invokeLater(new JMeld(args));
  }
}
