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

import org.jmeld.vc.*;
import org.jmeld.*;
import org.jmeld.util.node.*;

import java.io.*;

import org.jmeld.vc.*;
import org.jmeld.util.*;

import java.io.*;
import java.nio.charset.*;

public class VersionControlBaseDocument
    extends AbstractBufferDocument
{
  // Instance variables:
  private VersionControlIF versionControl;
  private StatusResult.Entry entry;
  private FileNode fileNode;
  private File file;
  private BaseFile baseFile;
  private boolean baseFileInitialized;
  private Charset charset;

  public VersionControlBaseDocument(VersionControlIF versionControl,
      StatusResult.Entry entry, FileNode fileNode, File file)
  {
    this.versionControl = versionControl;
    this.entry = entry;
    this.fileNode = fileNode;
    this.file = file;

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
    if (useBaseFile())
    {
      initBaseFile();
      return baseFile == null ? -1 : baseFile.getLength();
    }
    else
    {
      return fileNode.getDocument().getBufferSize();
    }
  }

  @Override
  public Reader getReader()
      throws JMeldException
  {
    BufferedInputStream bais;

    if (useBaseFile())
    {
      try
      {
        initBaseFile();
        bais = new BufferedInputStream(new ByteArrayInputStream(baseFile
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
    else
    {
      return fileNode.getDocument().getReader();
    }
  }

  @Override
  protected Writer getWriter()
      throws JMeldException
  {
    return null;
  }

  private boolean useBaseFile()
  {
    switch (entry.getStatus())
    {
      case modified:
      case removed:
      case missing:
        return true;

      default:
        return false;
    }
  }

  private void initBaseFile()
  {
    if (!baseFileInitialized)
    {
      baseFile = versionControl.getBaseFile(file);
      baseFileInitialized = true;
    }
  }

  @Override
  public boolean isReadonly()
  {
    return true;
  }
}
