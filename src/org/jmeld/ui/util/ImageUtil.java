package org.jmeld.ui.util;

import org.jmeld.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.net.*;

public class ImageUtil
{
  public static synchronized ImageIcon getSmallImageIcon(String iconName)
  {
    return getImageIcon("16x16/" + iconName + "-16");
  }

  public static synchronized ImageIcon getImageIcon(String iconName)
  {
    ImageIcon icon;
    URL       url;

    iconName = "images/" + iconName + ".png";

    url = ResourceLoader.getResource(iconName);
    if (url == null)
    {
      return null;
    }

    return new ImageIcon(url);
  }

  /** Create a 20% Transparent icon */
  public static ImageIcon createTransparentIcon(ImageIcon icon)
  {
    return createTransparentIcon(icon, 20);
  }

  /** Create a x% Transparent icon */
  public static ImageIcon createTransparentIcon(
    ImageIcon icon,
    int       percentage)
  {
    return createIcon(
      icon,
      new TransparentFilter(percentage));
  }

  /** Create a new icon which is filtered by some ImageFilter */
  private static synchronized ImageIcon createIcon(
    ImageIcon   icon,
    ImageFilter filter)
  {
    ImageProducer ip;
    Image         image;
    MediaTracker  tracker;

    if (icon == null)
    {
      return null;
    }

    ip = new FilteredImageSource(
        icon.getImage().getSource(),
        filter);
    image = Toolkit.getDefaultToolkit().createImage(ip);

    tracker = new MediaTracker(new JPanel());
    tracker.addImage(image, 1);
    try
    {
      tracker.waitForID(1);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
      return null;
    }

    return new ImageIcon(image);
  }
}
