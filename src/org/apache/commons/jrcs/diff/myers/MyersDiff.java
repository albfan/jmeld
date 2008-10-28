/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.jrcs.diff.myers;

import org.apache.commons.jrcs.diff.*;

import java.util.*;

/**
 * A clean-room implementation of
 * <a href="http://www.cs.arizona.edu/people/gene/">
 * Eugene Myers</a> differencing algorithm.
 * <p>
 * See the paper at
 * <a href="http://www.cs.arizona.edu/people/gene/PAPERS/diff.ps">
 * http://www.cs.arizona.edu/people/gene/PAPERS/diff.ps</a>
 *
 * @version $Revision: 1.6 $ $Date: 2003/05/10 19:47:25 $
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @see Delta
 * @see Revision
 * @see Diff
 */
public class MyersDiff
    implements DiffAlgorithm
{
  private static int MAXTIME = 3000;
  public boolean checkMaxTime = false;

  /**
   * Constructs an instance of the Myers differencing algorithm.
   */
  public MyersDiff()
  {
  }

  public void checkMaxTime(boolean checkMaxTime)
  {
    this.checkMaxTime = checkMaxTime;
  }

  /**
   * {@inheritDoc}
   */
  public Revision diff(Object[] orig, Object[] rev)
      throws DifferentiationFailedException
  {
    PathNode path = buildPath(orig, rev);

    return buildRevision(path, orig, rev);
  }

  /**
   * Computes the minimum diffpath that expresses de differences
   * between the original and revised sequences, according
   * to Gene Myers differencing algorithm.
   *
   * @param orig The original sequence.
   * @param rev The revised sequence.
   * @return A minimum {@link PathNode Path} accross the differences graph.
   * @throws DifferentiationFailedException if a diff path could not be found.
   */
  public PathNode buildPath(Object[] orig, Object[] rev)
      throws DifferentiationFailedException
  {
    int N;
    int M;
    int MAX;
    int size;
    int middle;
    Map<Integer, PathNode> diagonal;
    PathNode d_kminus;
    PathNode d_kplus;
    PathNode path;
    PathNode node;
    int kmiddle;
    int kplus;
    int kminus;
    PathNode prev;
    int i;
    int j;
    long startTime;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    // these are local constants
    N = orig.length;
    M = rev.length;

    MAX = N + M + 1;
    size = 1 + 2 * MAX;
    middle = (size + 1) / 2;
    diagonal = new HashMap<Integer, PathNode>();

    path = null;

    startTime = System.currentTimeMillis();

    diagonal.put(middle + 1, new Snake(0, -1, null));
    for (int d = 0; d < MAX; d++)
    {
      if (checkMaxTime && System.currentTimeMillis() - startTime > MAXTIME)
      {
        throw new org.jmeld.diff.MaxTimeExceededException(
            "Algoritm is taking up to much time");
      }

      for (int k = -d; k <= d; k += 2)
      {
        kmiddle = middle + k;
        kplus = kmiddle + 1;
        kminus = kmiddle - 1;
        prev = null;

        d_kminus = diagonal.get(kminus);
        d_kplus = diagonal.get(kplus);
        if ((k == -d) || (k != d && d_kminus.i < d_kplus.i))
        {
          i = d_kplus.i;
          prev = d_kplus;
        }
        else
        {
          i = d_kminus.i + 1;
          prev = d_kminus;
        }

        diagonal.remove(kminus);

        j = i - k;

        node = new DiffNode(i, j, prev);

        // orig and rev are zero-based
        // but the algorithm is one-based
        // that's why there's no +1 when indexing the sequences
        while (i < N && j < M && orig[i].equals(rev[j]))
        {
          i++;
          j++;
        }

        if (i > node.i)
        {
          node = new Snake(i, j, node);
        }

        diagonal.put(kmiddle, node);

        if (i >= N && j >= M)
        {
          return diagonal.get(kmiddle);
        }
      }

      diagonal.put(middle + d - 1, null);
    }

    // According to Myers, this cannot happen
    throw new DifferentiationFailedException("could not find a diff path");
  }

  private boolean isEmpty(Object o)
  {
    String s;

    if (!(o instanceof String))
    {
      return false;
    }

    s = (String) o;

    return (s == null || s.trim().compareTo("") == 0);
  }

  /**
   * Constructs a {@link Revision} from a difference path.
   *
   * @param path The path.
   * @param orig The original sequence.
   * @param rev The revised sequence.
   * @return A {@link Revision} script corresponding to the path.
   * @throws DifferentiationFailedException if a {@link Revision} could
   *         not be built from the given path.
   */
  public Revision buildRevision(PathNode path, Object[] orig, Object[] rev)
  {
    if (path == null)
    {
      throw new IllegalArgumentException("path is null");
    }

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    Revision revision = new Revision();

    if (path.isSnake())
    {
      path = path.prev;
    }
    while (path != null && path.prev != null && path.prev.j >= 0)
    {
      if (path.isSnake())
      {
        throw new IllegalStateException(
            "bad diffpath: found snake when looking for diff");
      }

      int i = path.i;
      int j = path.j;

      path = path.prev;
      int ianchor = path.i;
      int janchor = path.j;

      Delta delta = Delta.newDelta(new Chunk(orig, ianchor, i - ianchor),
        new Chunk(rev, janchor, j - janchor));

      revision.insertDelta(delta);
      if (path.isSnake())
      {
        path = path.prev;
      }
    }

    return revision;
  }
}
