package org.jmeld.ui.util;

import java.awt.*;

public class ColorUtil
{
  private ColorUtil()
  {
  }

  public static Color lighter(Color color)
  {
    return lighter(color, -0.10f);
  }

  public static Color brighter(Color color)
  {
    return brighter(color, 0.05f);
  }

  public static Color darker(Color color)
  {
    return brighter(color, -0.05f);
  }

  /** Create a brighter color by changing the b component of a
   *    hsb-color (b=brightness, h=hue, s=saturation)
   */
  public static Color brighter(Color color, float factor)
  {
    float[] hsbvals;

    hsbvals = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);

    return setBrightness(color, hsbvals[2] + factor);
  }

  /** Get the brightness of a color. 
   *    The H from HSB!
   */
  public static float getBrightness(Color color)
  {
    float[] hsbvals;

    hsbvals = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);

    return hsbvals[2];
  }

  /** Create a brighter color by changing the b component of a
   *    hsb-color (b=brightness, h=hue, s=saturation)
   */
  public static Color lighter(Color color, float factor)
  {
    float[] hsbvals;

    hsbvals = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);

    return setSaturation(color, hsbvals[1] + factor);
  }

  public static Color setSaturation(Color color, float saturation)
  {
    float[] hsbvals;

    if (saturation < 0.0f || saturation > 1.0f)
    {
      return color;
    }

    hsbvals = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);
    hsbvals[1] = saturation;

    color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));

    return color;
  }

  public static Color setBrightness(Color color, float brightness)
  {
    float[] hsbvals;

    hsbvals = new float[3];
    Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);
    hsbvals[2] = brightness;
    hsbvals[2] = Math.min(hsbvals[2], 1.0f);
    hsbvals[2] = Math.max(hsbvals[2], 0.0f);

    color = new Color(Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]));

    return color;
  }
}
