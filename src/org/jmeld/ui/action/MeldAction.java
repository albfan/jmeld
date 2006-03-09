package org.jmeld.ui.action;

import org.jmeld.ui.util.*;
import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.lang.reflect.*;

public class MeldAction
       extends AbstractAction
{
  // class variables:
  //   backwards compatible with jdk1.5
  public static String LARGE_ICON_KEY = "SwingLargeIconKey";

  // instance variables:
  private Object        object;
  private Method        actionMethod;
  private Method        isActionEnabledMethod;
  private ActionHandler actionHandler;

  MeldAction(
    ActionHandler actionHandler,
    Object        object,
    String        name)
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
      actionMethod = object.getClass()
                           .getMethod("do" + getName(), ActionEvent.class);
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
      isActionEnabledMethod = object.getClass()
                                    .getMethod("is" + getName() + "Enabled");
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
    putValue(
      SMALL_ICON,
      ImageUtil.getSmallImageIcon(iconName));
    putValue(
      LARGE_ICON_KEY,
      ImageUtil.getImageIcon(iconName));
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
