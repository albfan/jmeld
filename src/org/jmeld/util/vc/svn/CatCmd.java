package org.jmeld.util.vc.svn;

import org.jmeld.diff.*;
import org.jmeld.util.*;
import org.jmeld.util.vc.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

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
    super.execute("svn", "cat", "--non-interactive", "-r", "BASE", file.getPath());

    return getResult();
  }

  protected void build(byte[] data)
  {
    System.out.println(data);
    setResultData(new BaseFile(new String(data).toCharArray()));
  }

  public static void main(String[] args)
  {
    BufferedReader reader;
    String line;
    BaseFile result;

    try
    {
      result = new SubversionVersionControl().getBaseFile(new File(args[0]));
      if (result != null)
      {
        reader = new BufferedReader(new CharArrayReader(result.getCharArray()));
        while ((line = reader.readLine()) != null)
        {
          System.out.println(line);
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
