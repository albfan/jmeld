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
import org.jmeld.util.node.*;

import java.io.*;
import java.util.*;

public class DirectoryDiff
       extends FolderDiff
{
  private File            rightDirectory;
  private File            leftDirectory;
  private List<JMeldNode> right;
  private List<JMeldNode> left;
  private Filter          filter;

  public DirectoryDiff(
    File   leftDirectory,
    File   rightDirectory,
    Filter filter)
  {
    this.leftDirectory = leftDirectory;
    this.rightDirectory = rightDirectory;
    this.filter = filter;

    try
    {
      setLeftFolderShortName(leftDirectory.getName());
      setRightFolderShortName(rightDirectory.getName());
      setLeftFolderName(leftDirectory.getCanonicalPath());
      setRightFolderName(rightDirectory.getCanonicalPath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public String getRightNodeName(int index)
  {
    if (index < 0 || index >= right.size())
    {
      return null;
    }

    return getRightFolderName() + File.separator + right.get(index).getName();
  }

  public File getRightFolder()
  {
    return rightDirectory;
  }

  public List<JMeldNode> getRightNodes()
  {
    return right;
  }

  public String getLeftNodeName(int index)
  {
    if (index < 0 || index >= right.size())
    {
      return null;
    }

    return getLeftFolderName() + File.separator + left.get(index).getName();
  }

  public File getLeftFolder()
  {
    return leftDirectory;
  }

  public List<JMeldNode> getLeftNodes()
  {
    return left;
  }

  public void diff()
  {
    FileNode              newNode;
    String                name;
    Map<String, FileNode> rightMap;
    Map<String, FileNode> leftMap;
    JMeldNode             rightNode;
    JMeldNode             leftNode;
    DirectoryScanner      ds;

    StatusBar.start();
    StatusBar.setState("Start scanning directories...");

    ds = new DirectoryScanner();
    ds.setShowStateOn(true);
    ds.setBasedir(rightDirectory);
    if (filter != null)
    {
      ds.setIncludes(filter.getIncludes());
      ds.setExcludes(filter.getExcludes());
    }
    ds.setCaseSensitive(true);
    ds.scan();

    rightMap = ds.getIncludedFilesMap();
    rightMap.putAll(ds.getIncludedDirectoriesMap());

    ds = new DirectoryScanner();
    ds.setShowStateOn(true);
    ds.setBasedir(leftDirectory);
    if (filter != null)
    {
      ds.setIncludes(filter.getIncludes());
      ds.setExcludes(filter.getExcludes());
    }
    ds.setCaseSensitive(true);
    ds.scan();

    leftMap = ds.getIncludedFilesMap();
    leftMap.putAll(ds.getIncludedDirectoriesMap());

    for (JMeldNode node : rightMap.values())
    {
      name = node.getName();
      if (!leftMap.containsKey(name))
      {
        newNode = new FileNode(
            name,
            new File(leftDirectory, name));
        newNode.setLeaf(node.isLeaf());
        newNode.setState(JMeldNode.DELETED);

        node.setState(JMeldNode.ADDED);

        leftMap.put(name, newNode);
      }
    }

    for (JMeldNode node : leftMap.values())
    {
      name = node.getName();
      if (!rightMap.containsKey(name))
      {
        newNode = new FileNode(
            name,
            new File(leftDirectory, name));
        newNode.setLeaf(node.isLeaf());
        newNode.setState(JMeldNode.ADDED);

        node.setState(JMeldNode.DELETED);

        rightMap.put(name, newNode);
      }
    }

    right = new ArrayList(rightMap.values());
    Collections.sort(right);

    left = new ArrayList(leftMap.values());
    Collections.sort(left);

    for (int i = 0; i < right.size(); i++)
    {
      rightNode = right.get(i);
      leftNode = left.get(i);

      if (rightNode.getState() == JMeldNode.EQUAL
        && leftNode.getState() == JMeldNode.EQUAL)
      {
        StatusBar.setState(
          "Comparing file : %s",
          rightNode.getName());
        if (!rightNode.contentEquals(leftNode))
        {
          rightNode.setState(JMeldNode.CHANGED);
          leftNode.setState(JMeldNode.CHANGED);
        }
      }
    }
    StatusBar.setState("Ready comparing directories");
    StatusBar.stop();
  }

  public void print()
  {
    for (JMeldNode node : left)
    {
      node.print();
    }

    for (JMeldNode node : right)
    {
      node.print();
    }
  }

  public static void main(String[] args)
  {
    DirectoryDiff diff;

    diff = new DirectoryDiff(
        new File(args[0]),
        new File(args[1]),
        JMeldSettings.getInstance().getFilter().getFilter("ini"));
    diff.diff();
    diff.print();
  }
}
