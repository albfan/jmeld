package org.jmeld.ui.util;

import java.awt.*;

public class ColorUtil
{
  private ColorUtil()
  {
  }

  public static Color brighter(Color color)
  {
    return brighter(color, 0.05f);
  }

  /** Create a brighter color by changing the b component of a
   *    hsb-color (b=brightness, h=hue, s=saturation)
   */
  public static Color brighter(
    Color color,
    float factor)
  {
    float[] hsbvals;

    if (factor < 0.0f || factor > 1.0f)
    {
      return color;
    }

    hsbvals = new float[3];
    Color.RGBtoHSB(
      color.getRed(),
      color.getGreen(),
      color.getBlue(),
      hsbvals);
    hsbvals[2] = hsbvals[2] + factor;
    if (hsbvals[2] > 1.0f)
    {
      hsbvals[2] = 1.0f;
    }

    return new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));
  }
}
