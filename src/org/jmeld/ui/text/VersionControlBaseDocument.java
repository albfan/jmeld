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
import org.jmeld.util.*;

import java.io.*;
import java.nio.charset.*;

public class VersionControlBaseDocument
    extends AbstractBufferDocument
{
  // instance variables:
  private VersionControlIF versionControl;
  private File file;
  private BaseFile baseFile;
  private boolean baseFileInitialized;
  private Charset charset;

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

  @Override
  public int getBufferSize()
  {
    BaseFile bf;

    bf = getBaseFile();

    return bf == null ? -1 : bf.getLength();
  }

  @Override
  public Reader getReader()
      throws JMeldException
  {
    BufferedInputStream bais;

    if (!file.isFile() || !file.canRead())
    {
      throw new JMeldException("Could not open file: " + file);
    }

    try
    {
      bais = new BufferedInputStream(new ByteArrayInputStream(getBaseFile()
          .getByteArray()));
      charset = CharsetDetector.getInstance().getCharset(bais);
      return new BufferedReader(new InputStreamReader(bais, charset));
    }
    catch (Exception ex)
    {
      throw new JMeldException("Could not create FileReader for : "
                               + file.getName(), ex);
    }
  }

  @Override
  protected Writer getWriter()
      throws JMeldException
  {
    return null;
  }

  private BaseFile getBaseFile()
  {
    if (!baseFileInitialized)
    {
      baseFile = versionControl.getBaseFile(file);
      baseFileInitialized = true;
    }

    return baseFile;
  }

  @Override
  public boolean isReadonly()
  {
    return true;
  }

  public static void main(String[] args)
  {
    File file;
    VersionControlBaseDocument fd;
    VersionControlIF versionControl;

    try
    {
      file = new File(args[0]);

      versionControl = VersionControlUtil.getVersionControl(file);
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
