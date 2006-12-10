package org.jmeld.ui.text;

import org.jmeld.*;

import java.io.*;

public class FileDocument
       extends AbstractBufferDocument
{
  // instance variables:
  private File file;

  public FileDocument(File file)
  {
    this.file = file;

    try
    {
      setName(file.getCanonicalPath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      setName(file.getName());
    }

    setShortName(file.getName());
  }

  protected int getBufferSize()
  {
    return (int) file.length();
  }

  protected Reader getReader()
    throws JMeldException
  {
    if (!file.isFile() || !file.canRead())
    {
      throw new JMeldException("Could not open file: " + file);
    }

    try
    {
      return new FileReader(file);
    }
    catch (Exception ex)
    {
      throw new JMeldException("Could not create FileReader for : "
        + file.getName(), ex);
    }
  }

  protected Writer getWriter()
    throws JMeldException
  {
    try
    {
      return new FileWriter(file);
    }
    catch (IOException ex)
    {
      throw new JMeldException("Cannot create FileWriter for file: "
        + file.getName(), ex);
    }
  }

  public static void main(String[] args)
  {
    FileDocument fd;

    try
    {
      fd = new FileDocument(new File(args[0]));
      fd.read();
      fd.print();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
