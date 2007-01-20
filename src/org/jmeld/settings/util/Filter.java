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

import org.jmeld.ui.util.*;
import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.NONE)
public class Filter
       extends AbstractConfigurationElement
{
  @XmlAttribute
  private Boolean                     includeDefault = Boolean.FALSE;
  @XmlAttribute
  private String                      name;
  @XmlElement
  private List<FilterRule>            rules;
  {
    rules = new ArrayList<FilterRule>();
  }

  public Filter(String name)
  {
    setName(name);
  }

  public Filter()
  {
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public void addRule(FilterRule rule)
  {
    rules.add(rule);
  }

  public void removeRule(FilterRule rule)
  {
    rules.remove(rule);
  }

  public List<FilterRule> getRules()
  {
    return rules;
  }
}
