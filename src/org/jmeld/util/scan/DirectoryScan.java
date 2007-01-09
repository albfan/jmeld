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

import org.jmeld.ui.*;
import org.jmeld.util.node.*;

import java.io.*;
import java.util.*;

public class DirectoryScan
{
  private File                  directory;
  private Filter                filter;
  private Map<String, FileNode> map;

  public DirectoryScan(
    File   directory,
    Filter filter)
  {
    this.directory = directory;
    this.filter = filter;

    map = new HashMap<String, FileNode>();
  }

  public Map<String, FileNode> scan()
  {
    ScanDir scan;

    scan = new ScanDir(directory, filter);
    scan.visitRecursivly(new FileVisitor());

    return map;
  }

  class FileVisitor
         implements FileVisitorIF
  {
    public void visit(
      String directoryName,
      File   file)
    {
      map.put(
        directoryName,
        new FileNode(directoryName, file));

      if (file.isDirectory())
      {
        StatusBar.setStatus("Scanning directory : " + directoryName
          + File.separator + file.getName());
      }
    }
  }

  public static void main(String[] args)
  {
    new DirectoryScan(
      new File(args[0]),
      new Filter()).scan();
  }
}
