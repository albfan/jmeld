package org.jmeld.util;

import javax.xml.bind.annotation.*;
import org.jmeld.util.conf.*;

@XmlAccessorType(XmlAccessType.NONE)
public class Ignore
    extends AbstractConfigurationElement
{
  @XmlElement
  public boolean ignoreWhitespaceAtBegin;
  @XmlElement
  public boolean ignoreWhitespaceInBetween;
  @XmlElement
  public boolean ignoreWhitespaceAtEnd;
  @XmlElement
  public boolean ignoreEOL;
  @XmlElement
  public boolean ignoreBlankLines;
  @XmlElement
  public boolean ignoreCase;

  // Transient:
  public boolean ignore;
  public boolean ignoreWhitespace;

  public Ignore()
  {
    this(false, false, false);
  }

  public Ignore(boolean ignoreWhitespace, boolean ignoreEOL,
      boolean ignoreBlankLines)
  {
    this(ignoreWhitespace, ignoreWhitespace, ignoreWhitespace, ignoreEOL,
         ignoreBlankLines, false);
  }

  public Ignore(boolean ignoreWhitespaceAtBegin,
      boolean ignoreWhitespaceInBetween, boolean ignoreWhitespaceAtEnd,
      boolean ignoreEOL, boolean ignoreBlankLines, boolean ignoreCase)
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
           + (ignoreEOL ? "eol " : "")
           + (ignoreBlankLines ? "blanklines " : "")
           + (ignoreCase ? "case " : "");
  }
}
