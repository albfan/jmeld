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
