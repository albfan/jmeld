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
package org.jmeld.ui.util;

import org.jmeld.diff.JMDelta;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RevisionUtil
{
  private static Map<Color, Color> darker = new HashMap<Color, Color>();

  public static Color getColor(JMDelta delta)
  {
    if (delta.isDelete())
    {
      return getSettings().getDeletedColor();
    }

    if (delta.isChange())
    {
      return getSettings().getChangedColor();
    }

    return getSettings().getAddedColor();
  }

  public static Color getDarkerColor(JMDelta delta)
  {
    Color c;
    Color result;

    c = getColor(delta);

    result = darker.get(c);
    if (result == null)
    {
      result = c.darker();
      darker.put(c, result);
    }

    return result;
  }

  static private EditorSettings getSettings()
  {
    return JMeldSettings.getInstance().getEditor();
  }
}
