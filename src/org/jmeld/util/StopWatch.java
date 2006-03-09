package org.jmeld.util;

public class StopWatch
{
  private long    startTime;
  private long    stopTime;
  private boolean running;

  public StopWatch()
  {
    reset();
  }

  public StopWatch start()
  {
    startTime = System.currentTimeMillis();
    running = true;
    return this;
  }

  public StopWatch stop()
  {
    stopTime = System.currentTimeMillis();
    running = false;
    return this;
  }

  public long getElapsedTime()
  {
    if (startTime == -1)
    {
      return 0;
    }

    if (running)
    {
      return System.currentTimeMillis() - startTime;
    }
    else
    {
      return stopTime - startTime;
    }
  }

  public StopWatch reset()
  {
    startTime = -1;
    stopTime = -1;
    running = false;
    return this;
  }
}
