package org.jmeld.util.vc.svn;

import org.jmeld.util.*;

import java.io.*;
import java.util.*;

public class SvnXmlCmd<T>
{
  private T         resultData;
  private Result    result;

  public Result execute(
    Class<T>  clazz,
    String... command)
  {
    result = _execute(clazz, command);
    return result;
  }

  private Result _execute(
    Class<T>  clazz,
    String... command)
  {
    ProcessBuilder        pb;
    Process               p;
    BufferedReader        br;
    InputStream           is;
    int                   c;
    ByteArrayOutputStream baos;
    String                text;
    StringBuilder         errorText;

    try
    {
      pb = new ProcessBuilder(command);
      p = pb.start();

      System.out.println("execute: " + Arrays.asList(command));

      baos = new ByteArrayOutputStream();
      br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      while ((c = br.read()) != -1)
      {
        baos.write(c);
      }
      br.close();

      p.waitFor();
      if (p.exitValue() != 0)
      {
        errorText = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((text = br.readLine()) != null)
        {
          errorText.append(text);
        }
        br.close();
        return Result.FALSE(errorText.toString());
      }

      is = new ByteArrayInputStream(baos.toByteArray());
      resultData = JaxbPersister.getInstance().load(clazz, is);
      baos = null;
    }
    catch (Exception ex)
    {
      result = Result.FALSE(
          ex.getMessage(),
          ex);
      baos = null;
      return result;
    }

    return Result.TRUE();
  }

  public void printError()
  {
    System.out.println(result.getDescription());
    if (result.hasException())
    {
      result.getException().printStackTrace();
    }
  }

  public Result getResult()
  {
    return result;
  }

  protected T getResultData()
  {
    return resultData;
  }
}
