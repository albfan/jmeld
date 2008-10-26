package org.jmeld.util.vc.svn;

import org.jmeld.util.*;
import org.jmeld.util.vc.*;

import java.io.*;

public class CatCmd
    extends SvnCmd<BaseFile>
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
    byte[]   byteArray;

    try
    {
      result = new SubversionVersionControl().getBaseFile(new File(args[0]));
      byteArray = result.getByteArray();
      System.out.write(byteArray, 0, byteArray.length);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
