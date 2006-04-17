package org.jmeld.util.scan;

import java.util.*;
import java.io.*;

public class DirectoryScan
{
  private File           directory;
  private List<FileData> list;

  public DirectoryScan(File directory)
  {
    this.directory = directory;

    list = new ArrayList<FileData>();
  }

  public void scan()
  {
    ScanDir scan;
    Filter  filter;

    filter = new Filter();
    filter.exclude("classes");
    filter.exclude(".svn");
    filter.exclude("CVS");
    filter.exclude("tags");

    scan = new ScanDir(directory, filter);
    scan.visitRecursivly(new FileVisitor());

    for (FileData file : list)
    {
      System.out.println(file);
    }
  }

  class FileVisitor
         implements FileVisitorIF
  {
    public void visit(String directoryName, File file)
    {
      list.add(new FileData(directoryName, file));
    }
  }

  class FileData
  {
    private File   file;
    private String directoryName;

    public FileData(String directoryName, File file)
    {
      this.directoryName = directoryName;
      this.file = file;
    }

    public String toString()
    {
      return directoryName + File.separator + file.getName();
    }
  }

  public static void main(String[] args)
  {
    new DirectoryScan(new File(args[0])).scan();
  }
}
