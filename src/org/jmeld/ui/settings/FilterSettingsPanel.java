/*
 * FilterPreferencePanel.java
 *
 * Created on January 10, 2007, 6:31 PM
 */
package org.jmeld.ui.settings;

import org.jmeld.settings.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  kees
 */
public class FilterSettingsPanel
       extends FilterSettingsForm
       implements ConfigurationListenerIF
{
  public FilterSettingsPanel()
  {
    init();
    initConfiguration();

    JMeldSettings.getInstance().addConfigurationListener(this);
  }

  private void init()
  {
  }

  public void configurationChanged()
  {
    initConfiguration();
  }

  private void initConfiguration()
  {
  }
}
