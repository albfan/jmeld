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
package org.jmeld.util.file;

import org.apache.jmeld.tools.ant.*;
import org.jmeld.settings.*;
import org.jmeld.settings.util.*;
import org.jmeld.ui.*;
import org.jmeld.util.StringUtil;
import org.jmeld.util.node.*;

import java.io.*;
import java.util.*;

public class DirectoryDiff2
       extends FolderDiff2
{
  private File                  mineDirectory;
  private File                  originalDirectory;
  private DiffNode              rootNode;
  private Map<String, DiffNode> nodes;
  private Filter                filter;

  public DirectoryDiff2(
    File   originalDirectory,
    File   mineDirectory,
    Filter filter)
  {
    this.originalDirectory = originalDirectory;
    this.mineDirectory = mineDirectory;
    this.filter = filter;

    try
    {
      setOriginalFolderShortName(originalDirectory.getName());
      setMineFolderShortName(mineDirectory.getName());
      setOriginalFolderName(originalDirectory.getCanonicalPath());
      setMineFolderName(mineDirectory.getCanonicalPath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public DiffNode getRootNode()
  {
    return rootNode;
  }

  public void diff()
  {
    DirectoryScanner ds;
    DiffNode         node;

    StatusBar.start();
    StatusBar.setState("Start scanning directories...");

    rootNode = new DiffNode("<root>", false);
    nodes = new HashMap<String, DiffNode>();

    ds = new DirectoryScanner();
    ds.setShowStateOn(true);
    ds.setBasedir(mineDirectory);
    if (filter != null)
    {
      ds.setIncludes(filter.getIncludes());
      ds.setExcludes(filter.getExcludes());
    }
    ds.setCaseSensitive(true);
    ds.scan();

    for (FileNode fileNode : ds.getIncludedFilesMap().values())
    {
      node = addNode(fileNode.getName());
      node.setBufferNode1(fileNode);
    }

   ds = new DirectoryScanner();
   ds.setShowStateOn(true);
   ds.setBasedir(originalDirectory);
   if (filter != null)
   {
     ds.setIncludes(filter.getIncludes());
     ds.setExcludes(filter.getExcludes());
   }
   ds.setCaseSensitive(true);
   ds.scan();

    for (FileNode fileNode : ds.getIncludedFilesMap().values())
    {
      node = addNode(fileNode.getName());
      node.setBufferNode2(fileNode);
    }

/*
    for(DiffNode node : nodes)
    {
      node.compareContents();
    }
    */

    StatusBar.setState("Ready comparing directories");
    StatusBar.stop();
  }

  private DiffNode addNode(String name)
  {
    DiffNode node;

    node = nodes.get(name);
    if (node == null)
    {
      node = addNode(new DiffNode(name, true));
    }

    return node;
  }

  private DiffNode addNode(DiffNode node)
  {
    String   parentName;
    DiffNode parent;

    nodes.put(
      node.getName(),
      node);

    parentName = node.getParentName();
    if (StringUtil.isEmpty(parentName))
    {
      parent = rootNode;
    }
    else
    {
      parent = nodes.get(parentName);
      if (parent == null)
      {
        parent = addNode(new DiffNode(parentName, false));
      }
    }

    parent.addChild(node);
    return node;
  }

  public void print()
  {
    rootNode.print("");
  }

  public static void main(String[] args)
  {
    DirectoryDiff2 diff;

    diff = new DirectoryDiff2(
        new File(args[0]),
        new File(args[1]),
        JMeldSettings.getInstance().getFilter().getFilter("java"));
    diff.diff();
    diff.print();
  }
}
