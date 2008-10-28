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

public class ComboBoxSelectionPreference
    extends Preference
{
  // Class variables:
  private static String SELECTED_ITEM = "SELECTED_ITEM";

  // Instance variables:
  private JComboBox target;
  private int maxItems = 10;

  public ComboBoxSelectionPreference(String preferenceName, JComboBox target)
  {
    super("ComboBox-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    Object object;
    String selectedItem;
    int selectedIndex;

    selectedItem = getString(SELECTED_ITEM, null);

    target.getModel().addListDataListener(getListDataListener());

    if (target.getItemCount() > 0)
    {
      selectedIndex = 0;
      if (selectedItem != null)
      {
        for (int i = 0; i < target.getItemCount(); i++)
        {
          object = target.getItemAt(i);
          if (object == null)
          {
            continue;
          }

          if (object.toString().equals(selectedItem))
          {
            selectedIndex = i;
            break;
          }
        }
      }

      target.setSelectedIndex(selectedIndex);
    }
  }

  private void save()
  {
    ComboBoxModel model;
    Object item;

    model = target.getModel();

    // Save the selectedItem
    item = model.getSelectedItem();
    if (item != null)
    {
      putString(SELECTED_ITEM, item.toString());
    }
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
