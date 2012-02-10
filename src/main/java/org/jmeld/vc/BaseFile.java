package org.jmeld.vc;

import java.util.*;

public class BaseFile
{
  private byte[] byteArray;

  public BaseFile(byte[] byteArray)
  {
    this.byteArray = byteArray;
  }

  public int getLength()
  {
    return byteArray.length;
  }

  public byte[] getByteArray()
  {
    return byteArray;
  }
}
