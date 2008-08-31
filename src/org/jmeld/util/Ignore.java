package org.jmeld.util;

public class Ignore
{
  public final boolean ignore;
  public final boolean ignoreWhitespace;
  public final boolean ignoreWhitespaceAtBegin;
  public final boolean ignoreWhitespaceInBetween;
  public final boolean ignoreWhitespaceAtEnd;
  public final boolean ignoreEOL;
  public final boolean ignoreBlankLines;
  public final boolean ignoreCase;

  public Ignore(
    boolean ignoreWhitespace,
    boolean ignoreEOL,
    boolean ignoreBlankLines)
  {
    this(ignoreWhitespace, ignoreWhitespace, ignoreWhitespace, ignoreEOL,
      ignoreBlankLines, false);
  }

  public Ignore(
    boolean ignoreWhitespaceAtBegin,
    boolean ignoreWhitespaceInBetween,
    boolean ignoreWhitespaceAtEnd,
    boolean ignoreEOL,
    boolean ignoreBlankLines,
    boolean ignoreCase)
  {
    this.ignoreWhitespaceAtBegin = ignoreWhitespaceAtBegin;
    this.ignoreWhitespaceInBetween = ignoreWhitespaceInBetween;
    this.ignoreWhitespaceAtEnd = ignoreWhitespaceAtEnd;
    this.ignoreEOL = ignoreEOL;
    this.ignoreBlankLines = ignoreBlankLines;
    this.ignoreCase = ignoreCase;

    this.ignore = (ignoreWhitespaceAtBegin || ignoreWhitespaceInBetween
      || ignoreWhitespaceAtEnd || ignoreEOL || ignoreBlankLines || ignoreCase);
    this.ignoreWhitespace = (ignoreWhitespaceAtBegin
      || ignoreWhitespaceInBetween || ignoreWhitespaceAtEnd);
  }

  public String toString()
  {
    return "ignore: " + (!ignore ? "nothing" : "")
    + (ignoreWhitespaceAtBegin ? "whitespace[begin] " : "")
    + (ignoreWhitespaceInBetween ? "whitespace[in between] " : "")
    + (ignoreWhitespaceAtEnd ? "whitespace[end] " : "")
    + (ignoreEOL ? "eol " : "") + (ignoreBlankLines ? "blanklines " : "")
    + (ignoreCase ? "case " : "");
  }
}
