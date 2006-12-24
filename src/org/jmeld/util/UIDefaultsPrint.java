package org.jmeld.util;

import javax.swing.*;

import java.util.*;

public class UIDefaultsPrint
{
  public static void main(String[] args)
  {
    ArrayList list;

    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      list = new ArrayList();
      for (Enumeration e = UIManager.getDefaults().keys();
        e.hasMoreElements();)
      {
        list.add(e.nextElement().toString());
      }

      Collections.sort(list);
      for (Object key : list)
      {
        System.out.printf(
          "%-40.40s = %s\n",
          key,
          UIManager.get(key));
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
