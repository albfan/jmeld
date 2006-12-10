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
