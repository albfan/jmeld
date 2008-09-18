package org.jmeld.util.vc.svn;

import org.jmeld.util.*;

import java.io.*;
import java.util.*;

public abstract class SvnCmd<T>
{
  private Result result;
  private T      resultData;

  public void execute(String... command)
  {
    setResult(_execute(command));
  }

  private final Result _execute(String... command)
  {
    ProcessBuilder pb;
    Process p;
    BufferedReader br;
    InputStream is;
    int c;
    ByteArrayOutputStream baos;
    String text;
    StringBuilder errorText;

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
        return Result.FALSE(errorText.toString() + " (exitvalue=" + p.exitValue() + ")");
      }

      build(baos.toByteArray());
      baos.close();
      baos = null;
    }
    catch (Exception ex)
    {
      result = Result.FALSE(ex.getMessage(), ex);
      baos = null;
      return result;
    }

    return Result.TRUE();
  }

  protected abstract void build(byte[] data);

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

  protected void setResult(Result result)
  {
    this.result = result;
  }

  protected void setResultData(T resultData)
  {
    this.resultData = resultData;
  }

  protected T getResultData()
  {
    return resultData;
  }
}
