package org.jmeld.util;

public class WordTokenizer
       extends RETokenizer
{
  public WordTokenizer(String text)
  {
    super(text, "[\\p{Punct}]|[\\s]+", true);
  }
}
