package org.jmeld.util;

import java.util.*;
import java.util.regex.*;

public class WordTokenizer
{
  private static WordTokenizer instance = new WordTokenizer();
  private Pattern              p;

  private WordTokenizer()
  {
    p = Pattern.compile("\\s+|;|:|\\(|\\)|\\[|\\]|[-+*&^%\\/}{=<>`'\"|]+|\\.");
  }

  static public WordTokenizer getInstance()
  {
    return instance;
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
