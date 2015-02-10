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
package org.jmeld.ui.action;

import org.jmeld.ui.util.ImageUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MeldAction
    extends AbstractAction
{
  // class variables:
  //   backwards compatible with jdk1.5
  public static String LARGE_ICON_KEY = "SwingLargeIconKey";

  // instance variables:
  private Object object;
  private Method actionMethod;
  private Method isActionEnabledMethod;
  private ActionHandler actionHandler;

  MeldAction(ActionHandler actionHandler, Object object, String name)
  {
    super(name);

    this.actionHandler = actionHandler;
    this.object = object;
    initMethods();
  }

  private void initMethods()
  {
    try
    {
      actionMethod = object.getClass().getMethod("do" + getName(),
        ActionEvent.class);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }

    try
    {
      // This method is not mandatory! 
      //   If it is not available the method is always enabled.
      isActionEnabledMethod = object.getClass().getMethod(
        "is" + getName() + "Enabled");
    }
    catch (NoSuchMethodException ex)
    {
    }
  }

  public String getName()
  {
    return (String) getValue(NAME);
  }

  public void setToolTip(String toolTip)
  {
    putValue(SHORT_DESCRIPTION, toolTip);
  }

  public void setIcon(String iconName)
  {
    putValue(SMALL_ICON, ImageUtil.getSmallImageIcon(iconName));
    putValue(LARGE_ICON_KEY, ImageUtil.getImageIcon(iconName));
  }

  public ImageIcon getTransparentSmallImageIcon()
  {
    return ImageUtil.createTransparentIcon((ImageIcon) getValue(SMALL_ICON));
  }

  public void actionPerformed(ActionEvent ae)
  {
    if (object == null || actionMethod == null)
    {
      System.out.println("setActionCommand() has not been executed!");
      return;
    }

    try
    {
      actionMethod.setAccessible(true);
      actionMethod.invoke(object, ae);

      actionHandler.checkActions();
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace();
    }
    catch (IllegalArgumentException ex)
    {
      ex.printStackTrace();
    }
    catch (InvocationTargetException ex)
    {
      ex.printStackTrace();
    }
  }

  public boolean isActionEnabled()
  {
    if (object == null || isActionEnabledMethod == null)
    {
      return true;
    }

    try
    {
      return (Boolean) isActionEnabledMethod.invoke(object);
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace();
    }
    catch (IllegalArgumentException ex)
    {
      ex.printStackTrace();
    }
    catch (InvocationTargetException ex)
    {
      ex.printStackTrace();
    }

    return true;
  }
}
