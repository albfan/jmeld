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

import java.awt.image.RGBImageFilter;

/** Filter that adds transparency */
class TransparentFilter
    extends RGBImageFilter
{
  int percent;

  public TransparentFilter(int percent)
  {
    canFilterIndexColorModel = true;
    this.percent = percent;
  }

  public int filterRGB(int x, int y, int rgb)
  {
    int alpha;

    alpha = (rgb >> 24) & 0xff;
    alpha = Math.min(255, (int) (alpha * percent) / 100);

    return (rgb & 0xffffff) + (alpha << 24);
  }
}
