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
public class ColorSetting
    extends AbstractConfigurationElement
{
  @XmlAttribute
  private int b = -1;
  @XmlAttribute
  private int g = -1;
  @XmlAttribute
  private int r = -1;
  private Color color;

  public ColorSetting()
  {
  }

  public ColorSetting(Color color)
  {
    this.r = color.getRed();
    this.g = color.getGreen();
    this.b = color.getBlue();

    this.color = color;
  }

  public Color getColor()
  {
    if (r == -1 || g == -1 || b == -1)
    {
      return null;
    }

    if (color == null)
    {
      color = new Color(r, g, b);
    }

    return color;
  }
}
