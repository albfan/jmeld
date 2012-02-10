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
import org.jmeld.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;
import javax.swing.*;

import java.awt.*;

@XmlAccessorType(XmlAccessType.NONE)
public class FontSetting
    extends AbstractConfigurationElement
{
  @XmlAttribute
  private String name;
  @XmlAttribute
  private int style;
  @XmlAttribute
  private int size;

  private Font font;

  public FontSetting()
  {
  }

  public FontSetting(Font font)
  {
    this.name = font.getName();
    this.style = font.getStyle();
    this.size = font.getSize();

    this.font = font;
  }

  public Font getFont()
  {
    if (font == null && !StringUtil.isEmpty(name))
    {
      font = new Font(name, style, size);
    }

    return font;
  }
}
