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
          "\\s|;|:|\\(|\\)|\\[|\\]|[-+*&^%\\/}{=<>`'\"|]+|\\.");
      instance.innerDiffTokenizer = new WordTokenizer("\\b\\B*");
    }

    return instance.innerDiffTokenizer;
  }

  public static synchronized WordTokenizer getFileNameTokenizer()
  {
    if (instance.fileNameTokenizer == null)
    {
      instance.fileNameTokenizer = new WordTokenizer("[ /\\\\]+");
    }

    return instance.fileNameTokenizer;
  }
}
