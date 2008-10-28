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

import org.jmeld.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;

public class TabbedPanePreference
    extends Preference
{
  // Class variables:
  private static String TITLE = "TITLE";

  // Instance variables:
  private JTabbedPane target;

  public TabbedPanePreference(String preferenceName, JTabbedPane target)
  {
    super("TabbedPane-" + preferenceName);

    this.target = target;

    init();
  }

  private void init()
  {
    String title;

    title = getString(TITLE, "");

    if (!StringUtil.isEmpty(title))
    {
      for (int index = 0; index < target.getTabCount(); index++)
      {
        if (title.equals(target.getTitleAt(index)))
        {
          target.setSelectedIndex(index);
          break;
        }
      }
    }

    target.getModel().addChangeListener(getChangeListener());
  }

  private void save()
  {
    int index;
    String title;

    index = target.getSelectedIndex();
    title = index == -1 ? null : target.getTitleAt(index);
    putString(TITLE, title);
  }

  private ChangeListener getChangeListener()
  {
    return new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        save();
      }
    };
  }
}
