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
package org.jmeld.util.scan;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Filter
       implements FileFilter
{
  private List<Matcher>     includes;
  private List<Matcher>     excludes;
  private Map<File, String> debug;

  public Filter()
  {
    includes = new ArrayList<Matcher>();
    excludes = new ArrayList<Matcher>();
  }

  public void include(String regex)
  {
    includes.add(Pattern.compile(regex).matcher(""));
  }

  public void exclude(String regex)
  {
    excludes.add(Pattern.compile(regex).matcher(""));
  }

  public boolean accept(File f)
  {
    if (excludes.size() > 0)
    {
      for (Matcher m : excludes)
      {
        if (m.reset(f.getName()).matches())
        {
          return exclude(
            m.pattern().toString(),
            f);
        }
      }
    }

    if (f.isDirectory())
    {
      return include("isDirectory", f);
    }

    if (includes.size() > 0)
    {
      for (Matcher m : includes)
      {
        if (m.reset(f.getName()).matches())
        {
          return include(
            m.pattern().toString(),
            f);
        }
      }

      return exclude("at least 1 include", f);
    }

    return include("default", f);
  }

  private boolean exclude(
    String text,
    File   f)
  {
    if (debug != null)
    {
      debug.put(f, "Exclude: " + text);
    }

    return false;
  }

  private boolean include(
    String text,
    File   f)
  {
    if (debug != null)
    {
      debug.put(f, "Include: " + text);
    }

    return true;
  }
}
