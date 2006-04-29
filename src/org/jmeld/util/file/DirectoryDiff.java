package org.jmeld.util.file;

import org.jmeld.util.scan.*;
import org.jmeld.util.node.*;

import java.io.*;
import java.util.*;

public class DirectoryDiff
       extends FolderDiff
{
  private File            mineDirectory;
  private File            originalDirectory;
  private List<JMeldNode> mine;
  private List<JMeldNode> original;

  public DirectoryDiff(File originalDirectory, File mineDirectory)
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

    filter = getFilter();

    mineMap = new DirectoryScan(mineDirectory, filter).scan();
    originalMap = new DirectoryScan(originalDirectory, filter).scan();

    for (JMeldNode node : mineMap.values())
    {
      name = node.getName();
      if (!originalMap.containsKey(name))
      {
        newNode = new FileNode(name, new File(originalDirectory, name));
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
        newNode = new FileNode(name, new File(originalDirectory, name));
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

    diff = new DirectoryDiff(new File(args[0]), new File(args[1]));
    diff.diff();
    diff.print();
  }
}
