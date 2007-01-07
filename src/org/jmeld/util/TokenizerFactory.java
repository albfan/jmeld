package org.jmeld.util;

public class TokenizerFactory
{
  // class variables:
  private static TokenizerFactory instance = new TokenizerFactory();

  // instance variables:
  private WordTokenizer innerDiffTokenizer;
  private WordTokenizer fileNameTokenizer;

  private TokenizerFactory()
  {
  }

  public static synchronized WordTokenizer getInnerDiffTokenizer()
  {
    if (instance.innerDiffTokenizer == null)
    {
      instance.innerDiffTokenizer = new WordTokenizer(
          "\\s+|;|:|\\(|\\)|\\[|\\]|[-+*&^%\\/}{=<>`'\"|]+|\\.");
    }

    return instance.innerDiffTokenizer;
  }

  public static synchronized WordTokenizer getFileNameTokenizer()
  {
    if (instance.innerDiffTokenizer == null)
    {
      instance.innerDiffTokenizer = new WordTokenizer("[/\\\\]+");
    }

    return instance.innerDiffTokenizer;
  }
}
