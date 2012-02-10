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
package org.jmeld.settings.util;

import org.jmeld.settings.*;
import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
public class Filter
    extends AbstractConfigurationElement
{
  @XmlAttribute
  private Boolean includeDefault;
  @XmlAttribute
  private String name;
  @XmlElement
  private List<FilterRule> rules = new ArrayList<FilterRule>();

  public Filter(String name)
  {
    setName(name);
  }

  public Filter()
  {
    // Non argument constructor for JAXB
  }

  public void init(JMeldSettings root)
  {
    super.init(root);

    for (FilterRule rule : rules)
    {
      rule.init(root);
    }
  }

  public boolean isDefault()
  {
    return "default".equals(name);
  }

  public void setName(String name)
  {
    this.name = name;
    fireChanged();
  }

  public String getName()
  {
    return name;
  }

  public void insertRule(FilterRule ruleToInsertAfter, FilterRule rule)
  {
    int index;

    rule.init(configuration);

    index = rules.indexOf(ruleToInsertAfter);
    if (index != -1)
    {
      rules.add(index + 1, rule);
    }
    else
    {
      rules.add(rule);
    }

    fireChanged();
  }

  public void addRule(FilterRule rule)
  {
    rule.init(configuration);
    rules.add(rule);
    fireChanged();
  }

  public void removeRule(FilterRule rule)
  {
    rules.remove(rule);
    fireChanged();
  }

  public List<FilterRule> getRules()
  {
    return rules;
  }

  public List<String> getExcludes()
  {
    return getPatterns(FilterRule.Rule.excludes);
  }

  public List<String> getIncludes()
  {
    return getPatterns(FilterRule.Rule.includes);
  }

  private List<String> getPatterns(FilterRule.Rule r)
  {
    List<String> result;

    result = new ArrayList<String>();
    for (FilterRule rule : new GetRules().getRules())
    {
      if (rule.getRule() == r)
      {
        result.add(rule.getPattern());
      }
    }

    return result;
  }

  public String toString()
  {
    return name;
  }

  /** Recursively get all rules.
   *  Recursively because the rule 'importFilter' will
   *    import all rules from that filter!
   */
  class GetRules
  {
    HashSet<FilterRule> result = new HashSet<FilterRule>();
    HashSet<Filter> evaluatedFilters = new HashSet<Filter>();

    List<FilterRule> getRules()
    {
      collectRules(Filter.this);
      return new ArrayList(result);
    }

    void collectRules(Filter filter)
    {
      Filter nextFilter;

      evaluatedFilters.add(filter);

      for (FilterRule rule : filter.getRules())
      {
        // Rule is already evaluated or not active
        if (result.contains(rule) || !rule.isActive())
        {
          continue;
        }

        // Rule 'importFilter' will add it's own rules to the result.
        if (rule.getRule() == FilterRule.Rule.importFilter)
        {
          nextFilter = JMeldSettings.getInstance().getFilter().getFilter(
            rule.getPattern());

          // Don't evaluate a filter twice! (otherwise there will be a never
          //   ending recursive loop)
          if (nextFilter != null && !evaluatedFilters.contains(nextFilter))
          {
            collectRules(nextFilter);
          }
        }

        result.add(rule);
      }
    }
  }
}
