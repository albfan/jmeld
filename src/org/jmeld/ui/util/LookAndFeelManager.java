package org.jmeld.ui.util;

import com.jgoodies.looks.plastic.*;
import javax.swing.*;

public class LookAndFeelManager
{
  private static LookAndFeelManager instance = new LookAndFeelManager();

  private LookAndFeelManager()
  {
  }

  public static LookAndFeelManager getInstance()
  {
    return instance;
  }

  public void install()
  {
    String version;

    try
    {
      version = System.getProperty("java.version");
      if (version.startsWith("1.7"))
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      else
      {
        PlasticLookAndFeel.setPlasticTheme(new MeldBlue());
        PlasticLookAndFeel
            .setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
      }
    }
    catch (Exception e)
    {
    }

  }
}
