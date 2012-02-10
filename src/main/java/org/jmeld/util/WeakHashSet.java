package org.jmeld.util;

import java.util.*;

public class WeakHashSet<E>
    extends AbstractSet<E>
    implements Set<E>
{
  private Boolean value = Boolean.TRUE;
  private WeakHashMap<E, Boolean> map;

  public WeakHashSet()
  {
    map = new WeakHashMap<E, Boolean>();
  }

  public WeakHashSet(Collection<E> c)
  {
    map = new WeakHashMap<E, Boolean>();
    addAll(c);
  }

  public WeakHashSet(int initialCapacity, float loadFactor)
  {
    map = new WeakHashMap<E, Boolean>(initialCapacity, loadFactor);
  }

  public WeakHashSet(int initialCapacity)
  {
    map = new WeakHashMap<E, Boolean>(initialCapacity);
  }

  public Iterator<E> iterator()
  {
    return map.keySet().iterator();
  }

  public int size()
  {
    return map.size();
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  public boolean contains(Object o)
  {
    return map.containsKey(o);
  }

  public boolean add(E o)
  {
    return map.put(o, value) == null;
  }

  public boolean remove(Object o)
  {
    return map.remove(o) == value;
  }

  public void clear()
  {
    map.clear();
  }
}
