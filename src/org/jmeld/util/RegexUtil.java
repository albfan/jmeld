package org.jmeld.util;

import java.util.regex.*;

public class RegexUtil
{
  private static Pattern whiteSpacePattern;

  public static String replaceAll(Pattern pattern, String string,
      String replacement)
  {
    return pattern.matcher(string).replaceAll(replacement);
  }

  public static Pattern getWhitespacePattern()
  {
    if (whiteSpacePattern == null)
    {
      whiteSpacePattern = Pattern.compile("^\\p{Blank}+");
    }

    return whiteSpacePattern;
  }
}
