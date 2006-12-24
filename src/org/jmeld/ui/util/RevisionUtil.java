package org.jmeld.ui.util;

import org.jmeld.diff.*;

import java.awt.*;
import java.util.*;

public class RevisionUtil
{
  private static Map<Color, Color> darker = new HashMap<Color, Color>();

  public static Color getColor(JMDelta delta)
  {
    if (delta.isDelete())
    {
      return Colors.DELETED;
    }

    if (delta.isChange())
    {
      return Colors.CHANGED;
    }

    return Colors.ADDED;
  }

  public static Color getDarkerColor(JMDelta delta)
  {
    Color c;
    Color result;

    c = getColor(delta);

    result = darker.get(c);
    if (result == null)
    {
      System.out.println("color = " + c);
      result = c.darker();
      System.out.println("darker = " + result);
      darker.put(c, result);
      System.out.println("result = " + darker.get(c));
    }

    return result;
  }
}
