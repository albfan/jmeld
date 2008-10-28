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
package org.jmeld.util.prefs;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;

public class ComboBoxPreference
    extends Preference
{
  // Class variables:
  private static String ITEMS = "ITEMS";

  // Instance variables:
  private JComboBox target;
  private int maxItems = 10;

  public ComboBoxPreference(String preferenceName, JComboBox target)
  {
    super("ComboBox-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    DefaultComboBoxModel model;

    model = new DefaultComboBoxModel();
    for (String item : getListOfString(ITEMS, maxItems))
    {
      model.addElement(item);
    }

    target.setModel(model);
    model.addListDataListener(getListDataListener());
    if (target.getItemCount() > 0)
    {
      target.setSelectedIndex(0);
    }
  }

  private void save()
  {
    List<String> list;
    ComboBoxModel model;
    String item;

    list = new ArrayList<String>();

    model = target.getModel();

    // Put the selectedItem on top.
    item = (String) model.getSelectedItem();
    if (item != null)
    {
      list.add(item);
    }

    for (int i = 0; i < model.getSize(); i++)
    {
      item = (String) model.getElementAt(i);

      // Don't save items twice.
      if (list.contains(item))
      {
        continue;
      }

      list.add(item);
    }

    putListOfString(ITEMS, maxItems, list);
  }

  private ListDataListener getListDataListener()
  {
    return new ListDataListener()
    {
      public void contentsChanged(ListDataEvent e)
      {
        save();
      }

      public void intervalAdded(ListDataEvent e)
      {
        save();
      }

      public void intervalRemoved(ListDataEvent e)
      {
        save();
      }
    };
  }
}
