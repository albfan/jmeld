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
package org.jmeld.conf;

import org.jmeld.util.conf.*;

import javax.xml.bind.annotation.*;

import java.io.*;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "jmeld")
public class JMeldConfiguration
       extends AbstractConfiguration
{
  // class variables:
  public static JMeldConfiguration instance;

  // Instance variables:
  @XmlElement(name = "editor")
  private EditorConfiguration editor = new EditorConfiguration();

  public JMeldConfiguration()
  {
    init();
  }

  public static synchronized JMeldConfiguration getInstance()
  {
    return (JMeldConfiguration) ConfigurationManager.getInstance()
                                                    .get(JMeldConfiguration.class);
  }

  public void init()
  {
    editor.init(this);
  }

  public EditorConfiguration getEditor()
  {
    return editor;
  }
}
