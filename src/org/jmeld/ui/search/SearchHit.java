package org.jmeld.ui.search;

public class SearchHit
{
  int line;
  int fromOffset;
  int toOffset;
  int size;

  public SearchHit(
    int line,
    int offset,
    int size)
  {
    this.line = line;
    this.fromOffset = offset;
    this.size = size;
    this.toOffset = offset + size;
  }

  public int getLine()
  {
    return line;
  }

  public int getFromOffset()
  {
    return fromOffset;
  }

  public int getSize()
  {
    return size;
  }

  public int getToOffset()
  {
    return toOffset;
  }

  public boolean equals(Object o)
  {
    SearchHit sh;

    sh = (SearchHit) o;

    return (sh.getFromOffset() == getFromOffset()
    && sh.getToOffset() == getToOffset());
  }
}
