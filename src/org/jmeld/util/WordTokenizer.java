package org.jmeld.util;

import java.util.*;
import java.util.regex.*;

public class WordTokenizer
{
  private Pattern p;

  public WordTokenizer(String pattern)
  {
    p = Pattern.compile(pattern);
  }

  public List<String> getTokens(String text)
  {
    Matcher      m;
    List<String> result;
    int          index;
    String       s;

    result = new ArrayList<String>();

    index = 0;
    m = p.matcher(text);
    while (m.find())
    {
      s = text.substring(
          index,
          m.start());
      result.add(s);
      index += s.length();

      s = text.substring(
          m.start(),
          m.end());
      result.add(s);
      index += s.length();
    }

    if (index < text.length())
    {
      s = text.substring(
          index,
          text.length());
      result.add(s);
    }

    return result;
  }
}
