package org.jmeld.util;

public class StringUtil
{
  private StringUtil()
  {
  }

  public static boolean isEmpty(String string)
  {
    return (string == null || string.trim().compareTo("") == 0);
  }

  public static String replaceNewLines(String text)
  {
    return text.replaceAll("\n", "<LF>").replaceAll("\r", "<CR>");
  }
}
