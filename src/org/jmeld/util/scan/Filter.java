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
