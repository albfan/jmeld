package org.jmeld.ui.util;

import org.jmeld.diff.*;

import java.awt.*;

public class RevisionUtil
{
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
}
