package org.jmeld.util.scan;

import java.io.*;
import java.util.*;

public class FileNode
       extends JMeldNode
{
  private File file;

  public FileNode(String name, File file)
  {
    super(name, file.isDirectory());
    this.file = file;
  }

  public File getFile()
  {
    return file;
  }
}
