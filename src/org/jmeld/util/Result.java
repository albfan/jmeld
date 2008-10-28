package org.jmeld.util;

import java.io.*;
import java.util.*;

public class Result
{
  // instance variables
  private boolean result;
  private String description = "";
  private Exception exception;

  private Result(boolean result, String description, Exception exception)
  {
    this.result = result;
    this.description = description;
    this.exception = exception;
  }

  public static Result TRUE()
  {
    return new Result(true, null, null);
  }

  public static Result FALSE(String description)
  {
    return new Result(false, description, null);
  }

  public static Result FALSE(String description, Exception ex)
  {
    return new Result(false, description, ex);
  }

  public boolean isTrue()
  {
    return getResult();
  }

  public boolean isFalse()
  {
    return !getResult();
  }

  public boolean getResult()
  {
    return result;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    if (description == null)
    {
      return "";
    }

    return description.toString();
  }

  public boolean hasException()
  {
    return exception != null;
  }

  public Exception getException()
  {
    return exception;
  }

  public String toString()
  {
    return result + " :" + description;
  }
}
