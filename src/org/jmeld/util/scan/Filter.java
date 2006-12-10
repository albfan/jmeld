package org.jmeld.util.scan;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Filter
       implements FileFilter
{
  private List<Matcher> includes;
  private List<Matcher> excludes;

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
          return false;
        }
      }
    }

    if (includes.size() > 0)
    {
      for (Matcher m : includes)
      {
        if (m.reset(f.getName()).matches())
        {
          return true;
        }
      }

      return false;
    }

    return true;
  }
}
