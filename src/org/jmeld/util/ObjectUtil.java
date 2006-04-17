package org.jmeld.util;

public class ObjectUtil
{
  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null && o2 == null)
    {
      return true;
    }

    if (o1 == null || o2 == null)
    {
      return false;
    }

    return o1.equals(o2);
  }
}
