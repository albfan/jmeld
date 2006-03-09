package org.jmeld.ui.util;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;

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
