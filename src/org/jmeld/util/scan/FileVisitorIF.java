package org.jmeld.util.scan;

import java.io.*;

public interface FileVisitorIF
{
  public void visit(
    String directoryName,
    File   file);
}
