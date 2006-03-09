package org.jmeld.util;

public class StringUtil
{
  private StringUtil()
  {
  }

  public static String replaceNewLines(String text)
  {
    return text.replaceAll("\n", "<LF>").replaceAll("\r", "<CR>");
  }
}
