package org.jmeld.util;

public class Ignore
{
  public final boolean ignore;
  public final boolean ignoreWhitespace;
  public final boolean ignoreEOL;
  public final boolean ignoreBlankLines;

  public Ignore(
    boolean ignoreWhitespace,
    boolean ignoreEOL,
    boolean ignoreBlankLines)
  {
    this.ignoreWhitespace = ignoreWhitespace;
    this.ignoreEOL = ignoreEOL;
    this.ignoreBlankLines = ignoreBlankLines;

    this.ignore = (ignoreWhitespace || ignoreEOL || ignoreBlankLines);
  }

  public String toString()
  {
    return "ignore: " + (!ignore ? "nothing" : "")
    + (ignoreWhitespace ? "whitespace " : "") + (ignoreEOL ? "eol " : "")
    + (ignoreBlankLines ? "blanklines " : "");
  }
}
