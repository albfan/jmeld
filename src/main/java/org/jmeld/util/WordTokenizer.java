/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
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

  /** Get the tokens of the text.
   *   All tokens and non-tokens are returned in the result.
   *   So that the length of the text is the same length as
   *   the length of all tokens.
   */
  public List<String> getTokens(String text)
  {
    Matcher m;
    List<String> result;
    int index;
    String s;

    result = new ArrayList<String>();

    index = 0;
    m = p.matcher(text);
    while (m.find())
    {
      s = text.substring(index, m.start());
      // Here the text starts with a token!
      if (s.length() > 0)
      {
        result.add(s);
        index += s.length();
      }

      // Add the string that matches the token also to the result.
      s = text.substring(m.start(), m.end());
      if (s.length() > 0)
      {
        result.add(s);
        index += s.length();
      }
    }

    // Here the text does not end with the pattern!
    if (index < text.length())
    {
      s = text.substring(index, text.length());
      if (s.length() > 0)
      {
        result.add(s);
      }
    }

    return result;
  }
}
