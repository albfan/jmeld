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
package org.jmeld.settings;

import org.jmeld.settings.util.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
public class FilterSettings
    extends AbstractConfigurationElement
{
  @XmlElement
  private List<Filter> filters;

  public FilterSettings()
  {
    filters = new ArrayList<Filter>();
  }

  public void init(JMeldSettings parent)
  {
    super.init(parent);

    for (Filter f : filters)
    {
      f.init(parent);
    }

    initDefault();
  }

  public void addFilter(Filter filter)
  {
    filter.init(configuration);
    filters.add(filter);
    fireChanged();
  }

  public void removeFilter(Filter filter)
  {
    filters.remove(filter);
    fireChanged();
  }

  public List<Filter> getFilters()
  {
    return filters;
  }

  public Filter getFilter(String name)
  {
    for (Filter f : filters)
    {
      if (ObjectUtil.equals(f.getName(), name))
      {
        return f;
      }
    }

    return null;
  }

  private void initDefault()
  {
    Filter filter;

    if (getFilter("default") != null)
    {
      return;
    }

    filter = new Filter("default");
    filter.addRule(new FilterRule("Temporary files", FilterRule.Rule.excludes,
        "**/*~", true));
    filter.addRule(new FilterRule("Temporary files", FilterRule.Rule.excludes,
        "**/#*#", true));
    filter.addRule(new FilterRule("Temporary files", FilterRule.Rule.excludes,
        "**/.#*", true));
    filter.addRule(new FilterRule("Temporary files", FilterRule.Rule.excludes,
        "**/%*%", true));
    filter.addRule(new FilterRule("Temporary files", FilterRule.Rule.excludes,
        "**/._*", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/.svn", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/.svn/**", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/CVS", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/CVS/**", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/SCCS", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/SCCS/**", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/vssver.scc", true));
    filter.addRule(new FilterRule("Versioncontrol", FilterRule.Rule.excludes,
        "**/.SYNC", true));
    filter.addRule(new FilterRule("Mac", FilterRule.Rule.excludes,
        "**/.DS_Store", true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.jpg",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.gif",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.png",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.wav",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.mp3",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.ogg",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.xcf",
        true));
    filter.addRule(new FilterRule("Media", FilterRule.Rule.excludes, "**/.xpm",
        true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.pyc", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.a", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.obj", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.o", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.so", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.la", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.lib", true));
    filter.addRule(new FilterRule("Binaries", FilterRule.Rule.excludes,
        "**/.dll", true));
    filter.addRule(new FilterRule("Java", FilterRule.Rule.excludes,
        "**/*.class", true));
    filter.addRule(new FilterRule("Java", FilterRule.Rule.excludes, "**/*.jar",
        true));

    addFilter(filter);
  }
}
