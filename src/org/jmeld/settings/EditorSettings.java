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
package org.jmeld.settings;

import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.awt.*;

@XmlAccessorType(XmlAccessType.NONE)
public class EditorSettings
       extends AbstractConfigurationElement
{
  @XmlElement
  private boolean                 showLineNumbers;
  @XmlElement
  private int                     tabSize = 4;
  @XmlElement
  private boolean                 ignoreWhitespace;
  @XmlElement
  private boolean                 leftsideReadonly;
  @XmlElement
  private boolean                 rightsideReadonly;
  @XmlElement
  private ColorSetting            addedColor;
  @XmlElement
  private ColorSetting            changedColor;
  @XmlElement
  private ColorSetting            deletedColor;

  public EditorSettings()
  {
  }

  public boolean getShowLineNumbers()
  {
    return showLineNumbers;
  }

  public void setShowLineNumbers(boolean showLineNumbers)
  {
    this.showLineNumbers = showLineNumbers;
    fireChanged();
  }

  public int getTabSize()
  {
    return tabSize;
  }

  public void setTabSize(int tabSize)
  {
    this.tabSize = tabSize;
    fireChanged();
  }

  public boolean getIgnoreWhitespace()
  {
    return ignoreWhitespace;
  }

  public void setIgnoreWhitespace(boolean ignoreWhitespace)
  {
    this.ignoreWhitespace = ignoreWhitespace;
    fireChanged();
  }

  public boolean getLeftsideReadonly()
  {
    return leftsideReadonly;
  }

  public void setLeftsideReadonly(boolean leftsideReadonly)
  {
    this.leftsideReadonly = leftsideReadonly;
    fireChanged();
  }

  public boolean getRightsideReadonly()
  {
    return rightsideReadonly;
  }

  public void setRightsideReadonly(boolean rightsideReadonly)
  {
    this.rightsideReadonly = rightsideReadonly;
    fireChanged();
  }

  public void restoreColors()
  {
    addedColor = null;
    changedColor = null;
    deletedColor = null;
    fireChanged();
  }

  public void setAddedColor(Color color)
  {
    addedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getAddedColor()
  {
    return getColor(addedColor, Colors.ADDED);
  }

  public void setChangedColor(Color color)
  {
    changedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getChangedColor()
  {
    return getColor(changedColor, Colors.CHANGED);
  }

  public void setDeletedColor(Color color)
  {
    deletedColor = new ColorSetting(color);
    fireChanged();
  }

  public Color getDeletedColor()
  {
    return getColor(deletedColor, Colors.DELETED);
  }

  private Color getColor(
    ColorSetting cc,
    Color        defaultColor)
  {
    Color c;

    c = null;
    if (cc != null)
    {
      c = cc.getColor();
    }

    if (c == null)
    {
      c = defaultColor;
    }

    return c;
  }
}
