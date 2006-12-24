package org.jmeld.util;

import java.util.*;
import java.util.regex.*;

// Set to false if only the tokens that match the pattern are to be returned.
// If true, the text between matching tokens are also returned.
// "", "a", " 1 2 ", "b", " ", "c"
class RETokenizer
       implements Iterator<String>
{
  // Holds the original input to search for tokens
  private CharSequence input;

  // Used to find tokens
  private Matcher matcher;

  // If true, the String between tokens are returned
  private boolean returnDelims;

  // The current delimiter value. If non-null, should be returned
  // at the next call to next()
  private String delim;

  // The current matched value. If non-null and delim=null,
  // should be returned at the next call to next()
  private String match;

  // The value of matcher.end() from the last successful match.
  private int lastEnd = 0;

  // patternStr is a regular expression pattern that identifies tokens.
  // If returnDelims delim is false, only those tokens that match the
  // pattern are returned. If returnDelims true, the text between
  // matching tokens are also returned. If returnDelims is true, the
  // tokens are returned in the following sequence - delimiter, token,
  // delimiter, token, etc. Tokens can never be empty but delimiters might
  // be empty (empty string).
  public RETokenizer(
    CharSequence input,
    String       patternStr,
    boolean      returnDelims)
  {
    // Save values
    this.input = input;
    this.returnDelims = returnDelims;

    // Compile pattern and prepare input
    Pattern pattern = Pattern.compile(patternStr);
    matcher = pattern.matcher(input);
  }

  // Returns true if there are more tokens or delimiters.
  public boolean hasNext()
  {
    if (matcher == null)
    {
      return false;
    }
    if (delim != null || match != null)
    {
      return true;
    }
    if (matcher.find())
    {
      if (returnDelims)
      {
        delim = input.subSequence(
            lastEnd,
            matcher.start()).toString();
      }
      match = matcher.group();
      lastEnd = matcher.end();
    }
    else if (returnDelims && lastEnd < input.length())
    {
      delim = input.subSequence(
          lastEnd,
          input.length()).toString();
      lastEnd = input.length();

      // Need to remove the matcher since it appears to automatically
      // reset itself once it reaches the end.
      matcher = null;
    }
    return delim != null || match != null;
  }

  // Returns the next token (or delimiter if returnDelims is true).
  public String next()
  {
    String result = null;

    if (delim != null)
    {
      result = delim;
      delim = null;
    }
    else if (match != null)
    {
      result = match;
      match = null;
    }
    return result;
  }

  // Returns true if the call to next() will return a token rather
  // than a delimiter.
  public boolean isNextToken()
  {
    return delim == null && match != null;
  }

  // Not supported.
  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  public List<String> getTokens()
  {
    List<String> list;
    String       s;

    list = new ArrayList<String>();
    for (; hasNext();)
    {
      s = next();
      if (s.length() > 0)
      {
        list.add(s);
      }
    }

    return list;
  }
}
