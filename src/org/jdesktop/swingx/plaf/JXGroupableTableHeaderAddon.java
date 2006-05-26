/*
 * $Id: JXGroupableTableHeaderAddon.java,v 1.4 2006/03/31 06:51:23 evickroy Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 */
package org.jdesktop.swingx.plaf;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.swingx.JXGroupableTableHeader;
import org.jdesktop.swingx.plaf.windows.WindowsClassicLookAndFeelAddons;
import org.jdesktop.swingx.plaf.windows.WindowsLookAndFeelAddons;
import org.jdesktop.swingx.util.JVM;
import org.jdesktop.swingx.util.OS;

/**
 * Addon for <code>JXGroupableTableHeader</code>.<br>
 *
 */
public class JXGroupableTableHeaderAddon extends AbstractComponentAddon {

  public JXGroupableTableHeaderAddon() {
    super("JXGroupableTableHeader");
  }

  @Override
  protected void addBasicDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    Color menuBackground = new ColorUIResource(SystemColor.menu);
    defaults.addAll(Arrays.asList(new Object[]{
      JXGroupableTableHeader.muiClassID,
      "org.jdesktop.swingx.plaf.basic.BasicGroupableTableHeaderUI"
    }));
  }

  @Override
  protected void addMetalDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMetalDefaults(addon, defaults);

    defaults.addAll(Arrays.asList(new Object[]{
      JXGroupableTableHeader.muiClassID,
      "org.jdesktop.swingx.plaf.metal.MetalGroupableTableHeaderUI"
    }));      
  }

  @Override
  protected void addWindowsDefaults(LookAndFeelAddons addon,
    List<Object> defaults) {
    super.addWindowsDefaults(addon, defaults);
    
    if (addon instanceof WindowsLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JXGroupableTableHeader.muiClassID,
        "org.jdesktop.swingx.plaf.windows.WindowsGroupableTableHeaderUI"}));
    }
    
    if (addon instanceof WindowsClassicLookAndFeelAddons) {
      defaults.addAll(Arrays.asList(new Object[]{
        JXGroupableTableHeader.muiClassID,
        "org.jdesktop.swingx.plaf.windows.WindowsClassicGroupableTableHeaderUI"
      }));
    }
  }
  
  @Override
  protected void addMacDefaults(LookAndFeelAddons addon, List<Object> defaults) {
    super.addMacDefaults(addon, defaults);
    defaults.addAll(Arrays.asList(new Object[]{
      JXGroupableTableHeader.muiClassID,
      "org.jdesktop.swingx.plaf.metal.MetalGroupableTableHeaderUI"
    }));
  }
}
