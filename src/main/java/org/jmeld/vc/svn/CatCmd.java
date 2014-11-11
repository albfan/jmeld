package org.jmeld.vc.svn;

import org.jmeld.util.*;
import org.jmeld.vc.*;
import org.jmeld.vc.util.*;

import java.io.*;

public class CatCmd
    extends VcCmd<BaseFile>
{
  // Instance variables:
  private File file;

  public CatCmd(File file)
  {
    this.file = file;
  }

  public Result execute()
  {
    super.execute("svn", "cat", "--non-interactive", "-r", "BASE", file
        .getPath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    setResultData(new BaseFile(data));
  }

  public static void main(String[] args)
  {
    BaseFile result;
    byte[] byteArray;

    File file = parseFile(args);
    if (file == null) {
      return;
    }

    try
    {
      result = new SubversionVersionControl().getBaseFile(file);
      byteArray = result.getByteArray();
      System.out.write(byteArray, 0, byteArray.length);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
