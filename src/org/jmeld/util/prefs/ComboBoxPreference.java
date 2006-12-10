package org.jmeld.util.prefs;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;

public class ComboBoxPreference
       extends Preference
{
  // Class variables:
  private static String ELEMENT = "ELEMENT";

  // Instance variables:
  private JComboBox target;
  private int       maxItems = 10;

  public ComboBoxPreference(
    String    preferenceName,
    JComboBox target)
  {
    super("ComboBox-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    DefaultComboBoxModel model;

    model = new DefaultComboBoxModel();
    for (String element : getListOfString(ELEMENT, maxItems))
    {
      model.addElement(element);
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
    List<String>  list;
    ComboBoxModel model;
    String        element;

    list = new ArrayList<String>();

    model = target.getModel();

    // Put the selectedItem on top.
    element = (String) model.getSelectedItem();
    if (element != null)
    {
      list.add(element);
    }

    for (int i = 0; i < model.getSize(); i++)
    {
      element = (String) model.getElementAt(i);

      // Don't save elements twice.
      if (list.contains(element))
      {
        continue;
      }

      list.add(element);
    }

    putListOfString(ELEMENT, maxItems, list);
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
