package org.jmeld;

public class JMeldException
       extends Exception
{
  public JMeldException(String m)
  {
    super(m);
  }

  public JMeldException(
    String    m,
    Throwable t)
  {
    super(m, t);
  }
}
