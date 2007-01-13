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
import org.jmeld.ui.*;
import org.jmeld.util.node.*;
import org.jmeld.util.scan.*;

import java.io.*;
import java.util.*;

public class DirectoryDiff
       extends FolderDiff
{
  private File            mineDirectory;
  private File            originalDirectory;
  private List<JMeldNode> mine;
  private List<JMeldNode> original;

  public DirectoryDiff(
    File originalDirectory,
    File mineDirectory)
  {
    this.originalDirectory = originalDirectory;
    this.mineDirectory = mineDirectory;

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

  public String getMineNodeName(int index)
  {
    if (index < 0 || index >= mine.size())
    {
      return null;
    }

    return getMineFolderName() + File.separator + mine.get(index).getName();
  }

  public File getMineFolder()
  {
    return mineDirectory;
  }

  public List<JMeldNode> getMineNodes()
  {
    return mine;
  }

  public String getOriginalNodeName(int index)
  {
    if (index < 0 || index >= mine.size())
    {
      return null;
    }

    return getOriginalFolderName() + File.separator
    + original.get(index).getName();
  }

  public File getOriginalFolder()
  {
    return originalDirectory;
  }

  public List<JMeldNode> getOriginalNodes()
  {
    return original;
  }

  public void diff()
  {
    Filter                filter;
    FileNode              newNode;
    String                name;
    Map<String, FileNode> mineMap;
    Map<String, FileNode> originalMap;
    JMeldNode             mineNode;
    JMeldNode             originalNode;
    DirectoryScanner      ds;

    filter = getFilter();

    StatusBar.start();
    StatusBar.setStatus("Start scanning directories...");

    ds = new DirectoryScanner();
    ds.setBasedir(mineDirectory);
    ds.setExcludes(new String[] { "**/*.class", "tags" });
    ds.addDefaultExcludes();
    ds.setCaseSensitive(true);
    ds.scan();
    mineMap = ds.getIncludedFilesMap();
    mineMap.putAll(ds.getIncludedDirectoriesMap());

    ds = new DirectoryScanner();
    ds.setBasedir(originalDirectory);
    ds.setExcludes(new String[] { "**/*.class", "tags" });
    ds.addDefaultExcludes();
    ds.setCaseSensitive(true);
    ds.scan();
    originalMap = ds.getIncludedFilesMap();
    originalMap.putAll(ds.getIncludedDirectoriesMap());

    //mineMap = new DirectoryScan(mineDirectory, filter).scan();
    //originalMap = new DirectoryScan(originalDirectory, filter).scan();
    for (JMeldNode node : mineMap.values())
    {
      name = node.getName();
      if (!originalMap.containsKey(name))
      {
        newNode = new FileNode(
            name,
            new File(originalDirectory, name));
        newNode.setLeaf(node.isLeaf());
        newNode.setState(JMeldNode.DELETED);

        node.setState(JMeldNode.ADDED);

        originalMap.put(name, newNode);
      }
    }

    for (JMeldNode node : originalMap.values())
    {
      name = node.getName();
      if (!mineMap.containsKey(name))
      {
        newNode = new FileNode(
            name,
            new File(originalDirectory, name));
        newNode.setLeaf(node.isLeaf());
        newNode.setState(JMeldNode.ADDED);

        node.setState(JMeldNode.DELETED);

        mineMap.put(name, newNode);
      }
    }

    mine = new ArrayList(mineMap.values());
    Collections.sort(mine);

    original = new ArrayList(originalMap.values());
    Collections.sort(original);

    for (int i = 0; i < mine.size(); i++)
    {
      mineNode = mine.get(i);
      originalNode = original.get(i);

      if (mineNode.getState() == JMeldNode.EQUAL
        && originalNode.getState() == JMeldNode.EQUAL)
      {
        StatusBar.setStatus("Comparing file : " + mineNode.getName());
        if (!mineNode.contentEquals(originalNode))
        {
          mineNode.setState(JMeldNode.CHANGED);
          originalNode.setState(JMeldNode.CHANGED);
        }
      }
    }
    StatusBar.setStatus("Ready comparing directories");
    StatusBar.stop();
  }

  private Filter getFilter()
  {
    Filter filter;

    filter = new Filter();
    filter.exclude("classes");
    filter.exclude(".svn");
    filter.exclude("CVS");
    filter.exclude("tags");

    return filter;
  }

  public void print()
  {
    System.out.println("original:");
    for (JMeldNode node : original)
    {
      node.print();
    }

    System.out.println();
    System.out.println("mine:");
    for (JMeldNode node : mine)
    {
      node.print();
    }
  }

  public static void main(String[] args)
  {
    DirectoryDiff diff;

    diff = new DirectoryDiff(
        new File(args[0]),
        new File(args[1]));
    diff.diff();
    diff.print();
  }
}
