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

/*
    KeyboardFocusManager.setCurrentKeyboardFocusManager(
      new DefaultKeyboardFocusManager()
      {
        public boolean dispatchKeyEvent(KeyEvent e)
        {
          System.out.println("dispatch: " + KeyStroke.getKeyStrokeForEvent(e));
          //System.out.println("   event: " + e);
          return super.dispatchKeyEvent(e);
        }

        public void setGlobalFocusOwner(Component c)
        {
          super.setGlobalFocusOwner(c);
        }
      });
    */

    try
    {
      System.setProperty("swing.aatext", "true");

      version = System.getProperty("java.version");
      if (version.startsWith("1.6"))
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
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    panel = new JMeldPanel(fileName1, fileName2);
    frame.add(panel);
    frame.setJMenuBar(panel.getMenuBar());
    frame.setIconImage(ResourceLoader.getImageIcon("jmeld-small").getImage());
    new WindowPreference(
      frame.getTitle(),
      frame);

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
