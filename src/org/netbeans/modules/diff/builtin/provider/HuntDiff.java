/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

  /**
   * @param lines1 array of lines from the first source
   * @param lines2 array of lines from the second source
   * @return computed diff
   */
  public static Difference[] diff(
    Object[] lines1,
    Object[] lines2)
  {
    int      m = lines1.length;
    int      n = lines2.length;
    Object[] lines1_original = lines1;
    Object[] lines2_original = lines2;
    Line[]   l2s = new Line[n + 1];

    // In l2s we have sorted lines of the second file <1, n>
    for (int i = 1; i <= n; i++)
    {
      l2s[i] = new Line(i, lines2[i - 1]);
    }
    Arrays.sort(
      l2s,
      1,
      n + 1,
      new Comparator<Line>()
      {
        public int compare(
          Line l1,
          Line l2)
        {
          return l1.line.compareTo(l2.line);
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
      equivalenceAssoc[i] = findAssoc((Comparable) lines1[i - 1], l2s, equivalence);
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

    List<Difference> differences = getDifferences(J, lines1_original,
        lines2_original);
    cleanup(differences);
    return differences.toArray(new Difference[0]);
  }

  private static int findAssoc(
    Comparable line1,
    Line[]     l2s,
    boolean[]  equivalence)
  {
    int idx = binarySearch(l2s, line1, 1, l2s.length - 1);
    if (idx < 1)
    {
      return 0;
    }
    else
    {
      int lastGoodIdx = 0;
      for (; idx >= 1 && l2s[idx].line.equals(line1); idx--)
      {
        if (equivalence[idx - 1])
        {
          lastGoodIdx = idx;
        }
      }
      return lastGoodIdx;
    }
  }

  private static int binarySearch(
    Line[]     L,
    Comparable key,
    int        low,
    int        high)
  {
    while (low <= high)
    {
      int        mid = (low + high) >> 1;
      Comparable midVal = L[mid].line;
      int        comparison = midVal.compareTo(key);
      if (comparison < 0)
      {
        low = mid + 1;
      }
      else if (comparison > 0)
      {
        high = mid - 1;
      }
      else
      {
        return mid;
      }
    }
    return -(low + 1);
  }

  private static int binarySearch(
    Candidate[] K,
    int         key,
    int         low,
    int         high)
  {
    while (low <= high)
    {
      int mid = (low + high) >> 1;
      int midVal = K[mid].b;
      if (midVal < key)
      {
        low = mid + 1;
      }
      else if (midVal > key)
      {
        high = mid - 1;
      }
      else
      {
        return mid;
      }
    }
    return -(low + 1);
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
      int s = binarySearch(K, j, r, k);
      if (s >= 0)
      {
        // j was found in K[]
        s = k + 1;
      }
      else
      {
        s = -s - 2;
        if (s < r || s > k)
        {
          s = k + 1;
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

  private static List<Difference> getDifferences(
    int[]    J,
    Object[] lines1,
    Object[] lines2)
  {
    List<Difference> differences = new ArrayList<Difference>();
    int              n = lines1.length;
    int              m = lines2.length;
    int              start1 = 1;
    int              start2 = 1;
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
      int           end2 = start2 + 1;
      StringBuilder addedText = new StringBuilder();
      addedText.append(lines2[start2 - 1]).append('\n');
      while (end2 <= m)
      {
        Object line = lines2[end2 - 1];
        addedText.append(line).append('\n');
        end2++;
      }
      differences.add(
        new Difference(
          Difference.ADD,
          n,
          0,
          start2,
          m,
          null,
          addedText.toString()));
    }
    return differences;
  }

  private static void cleanup(List<Difference> diffs)
  {
    Difference last = null;
    for (int i = 0; i < diffs.size(); i++)
    {
      Difference diff = diffs.get(i);
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
    public int        lineNo;
    public Comparable line;
    public int        hash;

    public Line(
      int    lineNo,
      Object line)
    {
      this.lineNo = lineNo;
      this.line = (Comparable) line;
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
