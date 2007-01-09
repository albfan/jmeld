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
package org.jmeld.util.scan;

import java.io.*;
import java.util.*;

public class ScanDir
{
  private Map<String, File> visitedFiles = new HashMap<String, File>();
  private File              directory;
  private FileFilter        fileFilter;
  private int               depth;

  public ScanDir(File directory)
  {
    this(directory, null);
  }

  public ScanDir(
    File       directory,
    FileFilter fileFilter)
  {
    this.directory = directory;
    this.fileFilter = fileFilter;
  }

  public void visitRecursivly(FileVisitorIF visitor)
  {
    visitedFiles = new HashMap<String, File>();

    visit(".", directory, visitor);
  }

  private void visit(
    String        directoryName,
    File          file,
    FileVisitorIF visitor)
  {
    File[] files;
    String fileName;
    File   f;

    if (file == null)
    {
      return;
    }

    fileName = getFileName(file);
    if (fileName == null)
    {
      return;
    }

    if (visitedFiles.containsKey(fileName))
    {
      return;
    }

    if (file.isDirectory())
    {
      visitor.visit(directoryName, file);

      files = file.listFiles(fileFilter);
      for (int i = 0; i < files.length; i++)
      {
        depth++;

        f = files[i];
        visit(directoryName + File.separator + f.getName(), f, visitor);
        depth--;
      }

      return;
    }

    visitedFiles.put(fileName, file);
    visitor.visit(directoryName, file);
  }

  private String getFileName(File file)
  {
    try
    {
      return file.getCanonicalPath();
    }
    catch (Exception ex)
    {
      System.err.print("Cannot get canonicalPath of file " + file.getName());
    }

    return null;
  }
}
