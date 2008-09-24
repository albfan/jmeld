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
package org.jmeld.ui.text;

import org.jmeld.*;
import org.jmeld.util.*;

import java.io.*;
import java.nio.charset.*;

public class FileDocument
       extends AbstractBufferDocument
{
  // instance variables:
  private File    file;
  private Charset charset;

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

  public int getBufferSize()
  {
    return (int) file.length();
  }

  public Reader getReader()
    throws JMeldException
  {
    BufferedInputStream bis;

    if (!file.isFile() || !file.canRead())
    {
      throw new JMeldException("Could not open file: " + file);
    }

    try
    {
      // Try to create a reader that has the right charset.
      // If you use new FileReader(file) you get a reader
      //   with the default charset. 
      bis = new BufferedInputStream(new FileInputStream(file));
      charset = CharsetDetector.getInstance().getCharset(bis);
      return new BufferedReader(new InputStreamReader(bis, charset));
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
