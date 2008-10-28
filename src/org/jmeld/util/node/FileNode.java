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
package org.jmeld.util.node;

import org.jmeld.ui.text.*;

import java.io.*;

public class FileNode
    extends JMeldNode
    implements BufferNode
{
  private File file;
  private long fileLastModified;
  private FileDocument document;
  private boolean exists;

  public FileNode(String name, File file)
  {
    super(name, !file.isDirectory());
    this.file = file;

    initialize();
  }

  public File getFile()
  {
    return file;
  }

  @Override
  public void resetContent()
  {
    document = null;
    initialize();
  }

  public boolean exists()
  {
    return exists;
  }

  public FileDocument getDocument()
  {
    if (document == null || isDocumentOutOfDate())
    {
      initialize();
      if (exists())
      {
        document = new FileDocument(file);
        fileLastModified = file.lastModified();
      }
    }

    return document;
  }

  @Override
  public long getSize()
  {
    return file.length();
  }

  private boolean isDocumentOutOfDate()
  {
    boolean outOfDate;

    if (file == null || !exists())
    {
      return false;
    }

    outOfDate = file.lastModified() != fileLastModified;

    if (outOfDate)
    {
      System.out.println("FileNode[" + this + "] is out of date ["
                         + file.lastModified() + " != " + fileLastModified
                         + "]");
    }
    return outOfDate;
  }

  private void initialize()
  {
    exists = file.exists();
  }

  public boolean isReadonly()
  {
    return false;
  }
}
