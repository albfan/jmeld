package org.jmeld.util.file;

import org.jmeld.util.scan.*;

import java.io.*;
import java.util.*;

public class DirectoryDiff
{
  private File           mineDirectory;
  private File           originalDirectory;
  private List<FileNode> mine;
  private List<FileNode> original;

  public DirectoryDiff(File originalDirectory, File mineDirectory)
  {
    this.originalDirectory = originalDirectory;
    this.mineDirectory = mineDirectory;
  }

  public String getMineFileName(int index)
  {
    if (index < 0 || index >= mine.size())
    {
      return null;
    }

    return mineDirectory.getName() + File.separator + mine.get(index).getName();
  }

  public File getMineDirectory()
  {
    return mineDirectory;
  }

  public List<FileNode> getMineNodes()
  {
    return mine;
  }

  public String getOriginalFileName(int index)
  {
    if (index < 0 || index >= mine.size())
    {
      return null;
    }

    return originalDirectory.getName() + File.separator + original.get(index).getName();
  }

  public File getOriginalDirectory()
  {
    return originalDirectory;
  }

  public List<FileNode> getOriginalNodes()
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
    FileNode              mineNode;
    FileNode              originalNode;

    filter = getFilter();

    mineMap = new DirectoryScan(mineDirectory, filter).scan();
    originalMap = new DirectoryScan(originalDirectory, filter).scan();

    for (FileNode node : mineMap.values())
    {
      name = node.getName();
      if (!originalMap.containsKey(name))
      {
        newNode = new FileNode(name, new File(originalDirectory, name));
        newNode.setState(FileNode.DELETED);

        node.setState(FileNode.ADDED);

        originalMap.put(name, newNode);
      }
    }

    for (FileNode node : originalMap.values())
    {
      name = node.getName();
      if (!mineMap.containsKey(name))
      {
        newNode = new FileNode(name, new File(originalDirectory, name));
        newNode.setState(FileNode.ADDED);

        node.setState(FileNode.DELETED);

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
        if (!mineNode.contentEquals(originalNode))
        {
          mineNode.setState(JMeldNode.CHANGED);
          originalNode.setState(JMeldNode.CHANGED);
        }
      }
    }
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
    for (FileNode node : original)
    {
      node.print();
    }

    System.out.println();
    System.out.println("mine:");
    for (FileNode node : mine)
    {
      node.print();
    }
  }

  public static void main(String[] args)
  {
    DirectoryDiff diff;

    diff = new DirectoryDiff(new File(args[0]), new File(args[1]));
    diff.diff();
    diff.print();
  }
}
