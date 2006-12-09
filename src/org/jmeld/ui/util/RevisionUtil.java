package org.jmeld.ui.util;

import org.apache.commons.jrcs.diff.*;

import java.awt.*;

public class RevisionUtil
{
  public static Color getColor(Delta delta)
  {
    if (delta instanceof DeleteDelta)
    {
      return Colors.DELETED;
    }

    if (delta instanceof ChangeDelta)
    {
      return Colors.CHANGED;
    }

    return Colors.ADDED;
  }
}
