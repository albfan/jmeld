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

import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "jmeld")
public class JMeldSettings
    extends AbstractConfiguration
{
  // class variables:
  public static JMeldSettings instance;

  // Instance variables:
  @XmlElement(name = "editor")
  private EditorSettings editor = new EditorSettings();
  @XmlElement(name = "filter")
  private FilterSettings filter = new FilterSettings();
  @XmlElement(name = "folder")
  private FolderSettings folder = new FolderSettings();

  public JMeldSettings()
  {
  }

  public static synchronized JMeldSettings getInstance()
  {
    return (JMeldSettings) ConfigurationManager.getInstance().get(
      JMeldSettings.class);
  }

  @Override
  public void init()
  {
    editor.init(this);
    filter.init(this);
    folder.init(this);
  }

  public EditorSettings getEditor()
  {
    return editor;
  }

  public FilterSettings getFilter()
  {
    return filter;
  }

  public FolderSettings getFolder()
  {
    return folder;
  }
}
