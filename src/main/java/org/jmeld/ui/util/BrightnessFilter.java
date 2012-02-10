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

import java.awt.*;
import java.awt.image.RGBImageFilter;

/** Filter that adds transparency */
class BrightnessFilter
    extends RGBImageFilter
{
  float percent;

  public BrightnessFilter(float percent)
  {
    canFilterIndexColorModel = true;
    this.percent = percent;
  }

  public int filterRGB(int x, int y, int rgb)
  {
    float[] hsb;
    int r;
    int g;
    int b;
    int a;

    b = rgb & 0xFF;
    g = (rgb >> 8) & 0xFF;
    r = (rgb >> 16) & 0xFF;
    a = (rgb >> 24) & 0xFF;

    if (a == 255)
    {
      hsb = Color.RGBtoHSB(r, g, b, null);
      hsb[2] = hsb[2] + percent;
      if (hsb[2] > 1.0)
      {
        hsb[2] = 1.0f;
      }

      if (hsb[2] < 0.0)
      {
        hsb[2] = 0.0f;
      }

      rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
      rgb |= ((a & 0xFF) << 24);
    }

    return rgb;
  }
}
