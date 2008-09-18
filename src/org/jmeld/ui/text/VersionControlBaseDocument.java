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
package org.jmeld.ui.text;

import org.jmeld.*;

import java.io.*;

import org.jmeld.util.vc.*;

public class VersionControlBaseDocument
    extends AbstractBufferDocument
{
  // instance variables:
  private VersionControlIF versionControl;
  private File             file;
  private BaseFile         baseFile;

  public VersionControlBaseDocument(VersionControlIF versionControl, File file)
  {
    this.file = file;
    this.versionControl = versionControl;

    try
    {
      setName(file.getCanonicalPath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      setName(file.getName());
    }

    setShortName(file.getName());
  }

  public int getBufferSize()
  {
    return (int) file.length();
  }

  protected Reader getReader()
      throws JMeldException
  {
    if (!file.isFile() || !file.canRead())
    {
      throw new JMeldException("Could not open file: " + file);
    }

    try
    {
      if (baseFile == null)
      {
        baseFile = versionControl.getBaseFile(file);
        if (baseFile == null)
        {
          throw new JMeldException("Could not create BaseFileReader for : "
                                   + file.getName());
        }
      }

      return new CharArrayReader(baseFile.getCharArray());
    }
    catch (Exception ex)
    {
      throw new JMeldException("Could not create FileReader for : "
                               + file.getName(), ex);
    }
  }

  protected Writer getWriter()
      throws JMeldException
  {
    return null;
  }

  public static void main(String[] args)
  {
    File file;
    VersionControlBaseDocument fd;
    VersionControlIF versionControl;

    try
    {
      file = new File(args[0]);

      versionControl = VersionControlFactory.getInstance(file);
      fd = new VersionControlBaseDocument(versionControl, file);
      fd.read();
      fd.print();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
