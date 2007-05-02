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

import com.jgoodies.looks.plastic.*;
import com.jgoodies.looks.plastic.theme.*;

import org.jmeld.ui.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.prefs.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class JMeld
       implements Runnable
{
  private String fileName1;
  private String fileName2;

  public JMeld(String[] args)
  {
    if (args.length > 0)
    {
      fileName1 = args[0];
    }

    if (args.length > 1)
    {
      fileName2 = args[1];
    }
  }

  public void run()
  {
    JFrame     frame;
    JMeldPanel panel;
    String     version;

    KeyboardFocusManager.setCurrentKeyboardFocusManager(
      new DefaultKeyboardFocusManager()
      {
        public boolean dispatchKeyEvent(KeyEvent e)
        {
          //System.out.println("dispatch: " + KeyStroke.getKeyStrokeForEvent(e));
          //System.out.println("   event: " + e);
          return super.dispatchKeyEvent(e);
        }

        public void processKeyEvent(
          Component focusedComponent,
          KeyEvent  e)
        {
          System.out.println("processKeyEvent[" + focusedComponent.getClass()
            + "] : " + KeyStroke.getKeyStrokeForEvent(e));
          super.processKeyEvent(focusedComponent, e);
        }
      });

    try
    {
      System.setProperty("swing.aatext", "true");

      version = System.getProperty("java.version");
      if (version.startsWith("1.7"))
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      else
      {
        PlasticLookAndFeel.setPlasticTheme(new MeldBlue());
        PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        //UIManager.setLookAndFeel(new com.lipstikLF.LipstikLookAndFeel());
      }
    }
    catch (Exception e)
    {
    }

    frame = new JFrame("JMeld");
    panel = new JMeldPanel(fileName1, fileName2);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    //frame.setJMenuBar(panel.getMenuBar());
    frame.setIconImage(ResourceLoader.getImageIcon("jmeld-small").getImage());
    new WindowPreference(
      frame.getTitle(),
      frame);
    frame.addWindowListener(panel.getWindowListener());

    frame.setVisible(true);

    // Just to keep the damned metacity happy
    frame.toFront();
  }

  public static void main(String[] args)
  {
    //e.debug.EventDispatchThreadHangMonitor.initMonitoring();

    // According to the latest news EVERYTHING regarding swing should
    //   be executed on the EventDispatchThread
    SwingUtilities.invokeLater(new JMeld(args));
  }
}
