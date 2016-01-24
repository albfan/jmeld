/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.diff.builtin.provider;

import org.netbeans.api.diff.Difference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HuntDiff
{
  private HuntDiff()
  {
  }

  public static Difference[] diff(
    Object[] lines1,
    Object[] lines2)
  {
    int    m = lines1.length;
    int    n = lines2.length;
    Line[] l2s = new Line[n + 1];

    // In l2s we have sorted lines of the second file <1, n>
    for (int i = 1; i <= n; i++)
    {
      l2s[i] = new Line(i, lines2[i - 1]);
    }
    Arrays.sort(
      l2s,
      1,
      n + 1,
      new Comparator()
      {
        public int compare(
          Object o1,
          Object o2)
        {
          return ((Comparable) ((Line) o1).line).compareTo((Comparable) ((Line) o2).line);
        }

        public boolean equals(Object obj)
        {
          return obj == this;
        }
      });

    int[]     equvalenceLines = new int[n + 1];
    boolean[] equivalence = new boolean[n + 1];
    for (int i = 1; i <= n; i++)
    {
      Line l = l2s[i];
      equvalenceLines[i] = l.lineNo;
      equivalence[i] = (i == n) || !l.line.equals(l2s[i + 1].line);  //((Line) l2s.get(i)).line);
    }
    equvalenceLines[0] = 0;
    equivalence[0] = true;
    int[] equivalenceAssoc = new int[m + 1];
    for (int i = 1; i <= m; i++)
    {
      equivalenceAssoc[i] = findAssoc(lines1[i - 1], l2s, equivalence);
    }

    l2s = null;
    Candidate[] K = new Candidate[Math.min(m, n) + 2];
    K[0] = new Candidate(0, 0, null);
    K[1] = new Candidate(m + 1, n + 1, null);
    int k = 0;
    for (int i = 1; i <= m; i++)
    {
      if (equivalenceAssoc[i] != 0)
      {
        k = merge(K, k, i, equvalenceLines, equivalence, equivalenceAssoc[i]);
      }
    }
    int[]     J = new int[m + 2];  // Initialized with zeros

    Candidate c = K[k];
    while (c != null)
    {
      J[c.a] = c.b;
      c = c.c;
    }

    List differences = getDifferences(J, lines1, lines2);
    cleanup(differences);
    return (Difference[]) differences.toArray(new Difference[0]);
  }

  private static int findAssoc(
    Object    line1,
    Line[]    l2s,
    boolean[] equivalence)
  {
    // TODO use binary search
    for (int j = 1; j < l2s.length; j++)
    {
      if (equivalence[j - 1] && line1.equals(l2s[j].line))
      {
        return j;
      }
    }
    return 0;
  }

  private static int merge(
    Candidate[] K,
    int         k,
    int         i,
    int[]       equvalenceLines,
    boolean[]   equivalence,
    int         p)
  {
    int       r = 0;
    Candidate c = K[0];
    do
    {
      int j = equvalenceLines[p];
      int s = r;

      // TODO use binary search
      for (; s <= k; s++)
      {
        if (K[s].b < j && K[s + 1].b > j)
        {
          break;
        }
      }
      if (s <= k)
      {
        if (K[s + 1].b > j)
        {
          Candidate newc = new Candidate(i, j, K[s]);
          K[r] = c;
          r = s + 1;
          c = newc;
        }
        if (s == k)
        {
          K[k + 2] = K[k + 1];
          k++;
          break;
        }
      }
      if (equivalence[p] == true)
      {
        break;
      }
      else
      {
        p++;
      }
    }
    while (true);
    K[r] = c;
    return k;
  }

  private static List getDifferences(
    int[]    J,
    Object[] lines1,
    Object[] lines2)
  {
    List differences = new ArrayList();
    int  n = lines1.length;
    int  m = lines2.length;
    int  start1 = 1;
    int  start2 = 1;
    do
    {
      while (start1 <= n && J[start1] == start2)
      {
        start1++;
        start2++;
      }
      if (start1 > n)
      {
        break;
      }
      if (J[start1] < start2)
      {  // There's something extra in the first file
        int          end1 = start1 + 1;
        StringBuffer deletedText = new StringBuffer();
        deletedText.append(lines1[start1 - 1]).append('\n');
        while (end1 <= n && J[end1] < start2)
        {
          Object line = lines1[end1 - 1];
          deletedText.append(line).append('\n');
          end1++;
        }
        differences.add(
          new Difference(
            Difference.DELETE,
            start1,
            end1 - 1,
            start2 - 1,
            0,
            deletedText.toString(),
            null));
        start1 = end1;
      }
      else
      {  // There's something extra in the second file
        int          end2 = J[start1];
        StringBuffer addedText = new StringBuffer();
        for (int i = start2; i < end2; i++)
        {
          Object line = lines2[i - 1];
          addedText.append(line).append('\n');
        }
        differences.add(
          new Difference(
            Difference.ADD,
            (start1 - 1),
            0,
            start2,
            (end2 - 1),
            null,
            addedText.toString()));
        start2 = end2;
      }
    }
    while (start1 <= n);
    if (start2 <= m)
    {  // There's something extra at the end of the second file
      differences.add(
        new Difference(Difference.ADD, n, 0, start2, m, null, null));
    }
    return differences;
  }

  private static void cleanup(List diffs)
  {
    Difference last = null;
    for (int i = 0; i < diffs.size(); i++)
    {
      Difference diff = (Difference) diffs.get(i);
      if (last != null)
      {
        if (diff.getType() == Difference.ADD
          && last.getType() == Difference.DELETE
          || diff.getType() == Difference.DELETE
          && last.getType() == Difference.ADD)
        {
          Difference add;
          Difference del;
          if (Difference.ADD == diff.getType())
          {
            add = diff;
            del = last;
          }
          else
          {
            add = last;
            del = diff;
          }
          int d1f1l1 = add.getFirstStart()
            - (del.getFirstEnd() - del.getFirstStart());
          int d2f1l1 = del.getFirstStart();
          if (d1f1l1 == d2f1l1)
          {
            int        d1f2l1 = add.getSecondStart()
              - (del.getFirstEnd() - del.getFirstStart());
            int        d2f2l1 = del.getSecondStart() + 1;

            Difference newDiff = new Difference(Difference.CHANGE, d1f1l1,
                del.getFirstEnd(),
                add.getSecondStart(),
                add.getSecondEnd(),
                del.getFirstText(),
                add.getSecondText());
            diffs.set(i - 1, newDiff);
            diffs.remove(i);
            i--;
            diff = newDiff;
          }
        }
      }
      last = diff;
    }
  }

  private static class Line
  {
    public int    lineNo;
    public Object line;
    public int    hash;

    public Line(
      int    lineNo,
      Object line)
    {
      this.lineNo = lineNo;
      this.line = line;
      this.hash = line.hashCode();
    }
  }

  private static class Candidate
  {
    private int       a;
    private int       b;
    private Candidate c;

    public Candidate(
      int       a,
      int       b,
      Candidate c)
    {
      this.a = a;
      this.b = b;
      this.c = c;
    }
  }
}
