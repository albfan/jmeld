package org.gnu.diff;

import java.util.*;

/** A class to compare vectors of objects.  The result of comparison
   is a list of <code>change</code> objects which form an
   edit script.  The objects compared are traditionally lines
   of text from two files.  Comparison options such as "ignore
   whitespace" are implemented by modifying the <code>equals</code>
   and <code>hashcode</code> methods for the objects compared.
   <p>
   The basic algorithm is described in: </br>
   "An O(ND) Difference Algorithm and its Variations", Eugene Myers,
   Algorithmica Vol. 1 No. 2, 1986, p 251.
   <p>
   This class outputs different results from GNU diff 1.15 on some
   inputs.  Our results are actually better (smaller change list, smaller
   total size of changes), but it would be nice to know why.  Perhaps
   there is a memory overwrite bug in GNU diff 1.15.
   @author Stuart D. Gathman, translated from GNU diff 1.15
   Copyright (C) 2000  Business Management Systems, Inc.
   <p>
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 1, or (at your option)
   any later version.
   <p>
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   <p>
   You should have received a copy of the <a href=COPYING.txt>
   GNU General Public License</a>
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
public class Diff
{
  private static int SNAKE_LIMIT = 20;

  /** 1 more than the maximum equivalence value used for this or its
     sibling file. */
  private int equiv_max = 1;

  /** When set to true, the comparison uses a speed_large_files to speed it up.
     With this speed_large_files, for files with a constant small density
     of changes, the algorithm is linear in the file size.  */
  public boolean speed_large_files = false;

  /** When set to true, the algorithm returns a guarranteed minimal
     set of changes.  This makes things slower, sometimes much slower. */
  private int[] xvec; /* Vectors being compared. */
  private int[] yvec; /* Vectors being compared. */
  private int[] fdiag;
  /* Vector, indexed by diagonal, containing
     the X coordinate of the point furthest
     along the given diagonal in the forward
     search of the edit matrix. */
  private int[] bdiag;
  /* Vector, indexed by diagonal, containing
     the X coordinate of the point furthest
     along the given diagonal in the backward
     search of the edit matrix. */
  private int fdiagoff;
  /* Vector, indexed by diagonal, containing
     the X coordinate of the point furthest
     along the given diagonal in the backward
     search of the edit matrix. */
  private int bdiagoff;
  private final file_data[] filevec = new file_data[2];
  private int cost;
  private int too_expensive = 100;
  private boolean minimal = true;

  /** Prepare to find differences between two arrays.  Each element of
     the arrays is translated to an "equivalence number" based on
     the result of <code>equals</code>.  The original Object arrays
     are no longer needed for computing the differences.  They will
     be needed again later to print the results of the comparison as
     an edit script, if desired.
   */
  public Diff(Object[] a, Object[] b)
  {
    Map h = new HashMap(a.length + b.length);

    filevec[0] = new file_data(a, h);
    filevec[1] = new file_data(b, h);
  }

  /** Find the midpoint of the shortest edit script for a specified
     portion of the two files.
     We scan from the beginnings of the files, and simultaneously from the ends,
     doing a breadth-first search through the space of edit-sequence.
     When the two searches meet, we have found the midpoint of the shortest
     edit sequence.
     The value returned is the number of the diagonal on which the midpoint lies.
     The diagonal number equals the number of inserted lines minus the number
     of deleted lines (counting only lines before the midpoint).
     The edit cost is stored into COST; this is the total number of
     lines inserted or deleted (counting only lines before the midpoint).
     This function assumes that the first lines of the specified portions
     of the two files do not match, and likewise that the last lines do not
     match.  The caller must trim matching lines from the beginning and end
     of the portions it is going to specify.
     Note that if we return the "wrong" diagonal value, or if
     the value of bdiag at that diagonal is "wrong",
     the worst this can do is cause suboptimal diff output.
     It cannot cause incorrect diff output.  */
  private void diag(int xoff, int xlim, int yoff, int ylim,
      boolean find_minimal, Partition part)
  {
    int dmin = xoff - ylim; // Minimum valid diagonal.
    int dmax = xlim - yoff; // Maximum valid diagonal.
    int fmid = xoff - yoff; // Center diagonal of top-down search.
    int bmid = xlim - ylim; // Center diagonal of bottom-up search.
    int fmin = fmid; // Limits of top-down search.
    int fmax = fmid; // Limits of top-down search.
    int bmin = bmid; // Limits of bottom-up search.
    /* True if southeast corner is on an odd
    diagonal with respect to the northwest. */

    int bmax = bmid; // Limits of bottom-up search.
    /* True if southeast corner is on an odd
    diagonal with respect to the northwest. */

    boolean odd = (fmid - bmid & 1) != 0;

    fdiag[fdiagoff + fmid] = xoff;
    bdiag[bdiagoff + bmid] = xlim;

    for (int c = 1;; ++c)
    {
      int d; /* Active diagonal. */
      boolean big_snake = false;

      /* Extend the top-down search by an edit step in each diagonal. */
      if (fmin > dmin)
      {
        fdiag[fdiagoff + --fmin - 1] = -1;
      }
      else
      {
        ++fmin;
      }

      if (fmax < dmax)
      {
        fdiag[fdiagoff + ++fmax + 1] = -1;
      }
      else
      {
        --fmax;
      }

      for (d = fmax; d >= fmin; d -= 2)
      {
        int x;
        int y;
        int oldx;
        int tlo = fdiag[fdiagoff + d - 1];
        int thi = fdiag[fdiagoff + d + 1];

        if (tlo >= thi)
        {
          x = tlo + 1;
        }
        else
        {
          x = thi;
        }

        oldx = x;
        y = x - d;
        while (x < xlim && y < ylim && xvec[x] == yvec[y])
        {
          ++x;
          ++y;
        }

        if (x - oldx > SNAKE_LIMIT)
        {
          big_snake = true;
        }

        fdiag[fdiagoff + d] = x;
        if (odd && bmin <= d && d <= bmax
            && bdiag[bdiagoff + d] <= fdiag[fdiagoff + d])
        {
          part.xmid = x;
          part.ymid = y;
          part.lo_minimal = true;
          part.hi_minimal = true;
          return;
        }
      }

      /* Similar extend the bottom-up search. */
      if (bmin > dmin)
      {
        bdiag[bdiagoff + --bmin - 1] = Integer.MAX_VALUE;
      }
      else
      {
        ++bmin;
      }

      if (bmax < dmax)
      {
        bdiag[bdiagoff + ++bmax + 1] = Integer.MAX_VALUE;
      }
      else
      {
        --bmax;
      }

      for (d = bmax; d >= bmin; d -= 2)
      {
        int x;
        int y;
        int oldx;
        int tlo = bdiag[bdiagoff + d - 1];
        int thi = bdiag[bdiagoff + d + 1];

        if (tlo < thi)
        {
          x = tlo;
        }
        else
        {
          x = thi - 1;
        }

        oldx = x;
        y = x - d;
        while (x > xoff && y > yoff && xvec[x - 1] == yvec[y - 1])
        {
          --x;
          --y;
        }

        if (oldx - x > SNAKE_LIMIT)
        {
          big_snake = true;
        }

        bdiag[bdiagoff + d] = x;
        if (!odd && fmin <= d && d <= fmax && x <= fdiag[fdiagoff + d])
        {
          part.xmid = x;
          part.ymid = y;
          part.lo_minimal = true;
          part.hi_minimal = true;
          return;
        }
      }

      if (find_minimal)
      {
        continue;
      }

      /* Heuristic: check occasionally for a diagonal that has made
         lots of progress compared with the edit distance.
         If we have any such, find the one that has made the most
         progress and return it as if it had succeeded.
         With this speed_large_files, for files with a constant small density
         of changes, the algorithm is linear in the file size.  */
      if (c > 200 && big_snake && speed_large_files)
      {
        int best = 0;

        for (d = fmax; d >= fmin; d -= 2)
        {
          int dd = d - fmid;
          int x = fdiag[fdiagoff + d];
          int y = x - d;
          int v = (x - xoff) * 2 - dd;

          if (v > 12 * (c + (dd < 0 ? -dd : dd)))
          {
            if (v > best && xoff + SNAKE_LIMIT <= x && x < xlim
                && yoff + SNAKE_LIMIT <= y && y < ylim)
            {
              /* We have a good enough best diagonal;
                 now insist that it end with a significant snake.  */
              for (int k = 1; xvec[x - k] == yvec[y - k]; k++)
              {
                if (k == SNAKE_LIMIT)
                {
                  best = v;
                  part.xmid = x;
                  part.ymid = y;
                  break;
                }
              }
            }
          }
        }

        if (best > 0)
        {
          part.lo_minimal = true;
          part.hi_minimal = false;
          return;
        }

        best = 0;
        for (d = bmax; d >= bmin; d -= 2)
        {
          int dd = d - bmid;
          int x = bdiag[bdiagoff + d];
          int y = x - d;
          int v = (xlim - x) * 2 + dd;

          if (v > 12 * (c + (dd < 0 ? -dd : dd)))
          {
            if (v > best && xoff < x && x <= xlim - SNAKE_LIMIT && yoff < y
                && y <= ylim - SNAKE_LIMIT)
            {
              /* We have a good enough best diagonal;
                 now insist that it end with a significant snake.  */
              for (int k = 0; xvec[x + k] == yvec[y + k]; k++)
              {
                if (k == SNAKE_LIMIT - 1)
                {
                  best = v;
                  part.xmid = x;
                  part.ymid = y;
                  break;
                }
              }
            }
          }
        }

        if (best > 0)
        {
          part.lo_minimal = false;
          part.hi_minimal = true;
          return;
        }
      }

      /* Heuristic: if we've gone well beyond the call of duty,
         give up and report halfway between our best results so far.  */
      if (c >= too_expensive)
      {
        int fxybest;
        int bxybest;
        int fxbest = 0;
        int bxbest = 0;

        /* Find forward diagonal that maximizes X + Y.  */
        fxybest = -1;
        for (d = fmax; d >= fmin; d -= 2)
        {
          int x = Math.min(fdiag[d], xlim);
          int y = x - d;
          if (ylim < y)
          {
            x = ylim + d;
            y = ylim;
          }
          if (fxybest < x + y)
          {
            fxybest = x + y;
            fxbest = x;
          }
        }

        /* Find backward diagonal that minimizes X + Y.  */
        bxybest = Integer.MAX_VALUE;
        for (d = bmax; d >= bmin; d -= 2)
        {
          int x = Math.max(xoff, bdiag[d]);
          int y = x - d;
          if (y < yoff)
          {
            x = yoff + d;
            y = yoff;
          }
          if (x + y < bxybest)
          {
            bxybest = x + y;
            bxbest = x;
          }
        }

        /* Use the better of the two diagonals.  */
        if ((xlim + ylim) - bxybest < fxybest - (xoff + yoff))
        {
          part.xmid = fxbest;
          part.ymid = fxybest - fxbest;
          part.lo_minimal = true;
          part.hi_minimal = false;
        }
        else
        {
          part.xmid = bxbest;
          part.ymid = bxybest - bxbest;
          part.lo_minimal = false;
          part.hi_minimal = true;
        }
        return;
      }
    }
  }

  /** Compare in detail contiguous subsequences of the two files
     which are known, as a whole, to match each other.
     The results are recorded in the vectors filevec[N].changed_flag, by
     storing a 1 in the element for each line that is an insertion or deletion.
     The subsequence of file 0 is [XOFF, XLIM) and likewise for file 1.
     Note that XLIM, YLIM are exclusive bounds.
     All line numbers are origin-0 and discarded lines are not counted.  */
  private void compareseq(int xoff, int xlim, int yoff, int ylim,
      boolean find_minimal)
  {
    /* Slide down the bottom initial diagonal. */
    while (xoff < xlim && yoff < ylim && xvec[xoff] == yvec[yoff])
    {
      ++xoff;
      ++yoff;
    }
    /* Slide up the top initial diagonal. */
    while (xlim > xoff && ylim > yoff && xvec[xlim - 1] == yvec[ylim - 1])
    {
      --xlim;
      --ylim;
    }

    /* Handle simple cases. */
    if (xoff == xlim)
    {
      while (yoff < ylim)
      {
        filevec[1].changed_flag[1 + filevec[1].realindexes[yoff++]] = true;
      }
    }
    else if (yoff == ylim)
    {
      while (xoff < xlim)
      {
        filevec[0].changed_flag[1 + filevec[0].realindexes[xoff++]] = true;
      }
    }
    else
    {
      Partition part = new Partition();

      /* Find a point of correspondence in the middle of the files.  */
      diag(xoff, xlim, yoff, ylim, find_minimal, part);

      /* Use the partitions to split this problem into subproblems.  */
      compareseq(xoff, part.xmid, yoff, part.ymid, part.lo_minimal);
      compareseq(part.xmid, xlim, part.ymid, ylim, part.hi_minimal);
    }
  }

  /** Discard lines from one file that have no matches in the other file.
   */
  private void discard_confusing_lines()
  {
    filevec[0].discard_confusing_lines(filevec[1]);
    filevec[1].discard_confusing_lines(filevec[0]);
  }

  /** Adjust inserts/deletes of blank lines to join changes
     as much as possible.
   */
  private void shift_boundaries()
  {
    filevec[0].shift_boundaries(filevec[1]);
    filevec[1].shift_boundaries(filevec[0]);
  }

  /** Scan the tables of which lines are inserted and deleted,
     producing an edit script in forward order.  */
  private change build_script()
  {
    change script = null;
    final boolean[] changed0 = filevec[0].changed_flag;
    final boolean[] changed1 = filevec[1].changed_flag;
    final int len0 = filevec[0].buffered_lines;
    final int len1 = filevec[1].buffered_lines;
    int i0 = len0;
    int i1 = len1;

    /* Note that changedN[-1] does exist, and contains 0.  */
    while (i0 >= 0 || i1 >= 0)
    {
      if (changed0[i0] || changed1[i1])
      {
        int line0 = i0;
        int line1 = i1;

        /* Find # lines changed here in each file.  */
        while (changed0[i0])
        {
          --i0;
        }
        while (changed1[i1])
        {
          --i1;
        }

        /* Record this change.  */
        script = new change(i0, i1, line0 - i0, line1 - i1, script);
      }

      /* We have reached lines in the two files that match each other.  */
      i0--;
      i1--;
    }

    return script;
  }

  /* Report the differences of two files.  DEPTH is the current directory
     depth. */
  public change diff_2()
  {
    /* Some lines are obviously insertions or deletions
       because they don't match anything.  Detect them now,
       and avoid even thinking about them in the main comparison algorithm.  */
    long time = System.currentTimeMillis();

    discard_confusing_lines();
    /*
       System.out.println("discard took : " + (System.currentTimeMillis() - time)
         + " msec.");
     */

    /* Now do the main comparison algorithm, considering just the
       undiscarded lines.  */
    xvec = filevec[0].undiscarded;
    yvec = filevec[1].undiscarded;

    int diags = filevec[0].nondiscarded_lines + filevec[1].nondiscarded_lines
                + 3;

    fdiag = new int[diags];
    fdiagoff = filevec[1].nondiscarded_lines + 1;
    bdiag = new int[diags];
    bdiagoff = filevec[1].nondiscarded_lines + 1;

    compareseq(0, filevec[0].nondiscarded_lines, 0,
      filevec[1].nondiscarded_lines, minimal);
    fdiag = null;
    bdiag = null;

    /* Modify the results slightly to make them prettier
       in cases where that can validly be done.  */
    shift_boundaries();

    /* Get the results of comparison in the form of a chain
       of `struct change's -- an edit script.  */
    return build_script();
  }

  /** The result of comparison is an "edit script": a chain of change objects.
     Each change represents one place where some lines are deleted
     and some are inserted.
     LINE0 and LINE1 are the first affected lines in the two files (origin 0).
     DELETED is the number of lines deleted here from file 0.
     INSERTED is the number of lines inserted here in file 1.
     If DELETED is 0 then LINE0 is the number of the line before
     which the insertion was done; vice versa for INSERTED and LINE1.  */
  public static class change
  {
    /** Previous or next edit command. */
    public change link;
    /** # lines of file 1 changed here.  */
    public final int inserted;
    /** # lines of file 0 changed here.  */
    public final int deleted;
    /** Line number of 1st deleted line.  */
    public final int line0;
    /** Line number of 1st inserted line.  */
    public final int line1;

    /** Cons an additional entry onto the front of an edit script OLD.
       LINE0 and LINE1 are the first affected lines in the two files (origin 0).
       DELETED is the number of lines deleted here from file 0.
       INSERTED is the number of lines inserted here in file 1.
       If DELETED is 0 then LINE0 is the number of the line before
       which the insertion was done; vice versa for INSERTED and LINE1.  */
    change(int line0, int line1, int deleted, int inserted, change old)
    {
      this.line0 = line0;
      this.line1 = line1;
      this.inserted = inserted;
      this.deleted = deleted;
      this.link = old;
      //System.err.println(line0+","+line1+","+inserted+","+deleted);
    }

    public void print()
    {
      System.out.println(this);
      if (link != null)
      {
        link.print();
      }
    }

    public String toString()
    {
      return "line0=" + line0 + ", line1=" + line1 + ", inserted=" + inserted
             + ", deleted=" + deleted;
    }
  }

  /** Data on one input file being compared.
   */
  class file_data
  {
    /** Allocate changed array for the results of comparison.  */
    void clear()
    {
      /* Allocate a flag for each line of each file, saying whether that line
         is an insertion or deletion.
         Allocate an extra element, always zero, at each end of each vector.
       */
      changed_flag = new boolean[buffered_lines + 2];
    }

    /** Return equiv_count[I] as the number of lines in this file
       that fall in equivalence class I.
       @return the array of equivalence class counts.
     */
    int[] equivCount()
    {
      int[] equiv_count = new int[equiv_max];

      for (int i = 0; i < buffered_lines; ++i)
      {
        ++equiv_count[equivs[i]];
      }

      return equiv_count;
    }

    /** Discard lines that have no matches in another file.
       A line which is discarded will not be considered by the actual
       comparison algorithm; it will be as if that line were not in the file.
       The file's `realindexes' table maps virtual line numbers
       (which don't count the discarded lines) into real line numbers;
       this is how the actual comparison algorithm produces results
       that are comprehensible when the discarded lines are counted.
       <p>
       When we discard a line, we also mark it as a deletion or insertion
       so that it will be printed in the output.
       @param f the other file
     */
    void discard_confusing_lines(file_data f)
    {
      clear();
      /* Set up table of which lines are going to be discarded. */
      byte[] discarded = discardable(f.equivCount());

      /* Don't really discard the provisional lines except when they occur
         in a run of discardables, with nonprovisionals at the beginning
         and end.  */
      filterDiscards(discarded);

      /* Actually discard the lines. */
      discard(discarded);
    }

    /** Mark to be discarded each line that matches no line of another file.
       If a line matches many lines, mark it as provisionally discardable.
       @see equivCount()
       @param counts The count of each equivalence number for the other file.
       @return 0=nondiscardable, 1=discardable or 2=provisionally discardable
       for each line
     */
    private byte[] discardable(final int[] counts)
    {
      final int end = buffered_lines;
      final byte[] discards = new byte[end];
      final int[] equivs = this.equivs;
      int many = 5;
      int tem = end / 64;

      /* Multiply MANY by approximate square root of number of lines.
         That is the threshold for provisionally discardable lines.  */
      while ((tem = tem >> 2) > 0)
      {
        many *= 2;
      }

      for (int i = 0; i < end; i++)
      {
        int nmatch;

        if (equivs[i] == 0)
        {
          continue;
        }

        nmatch = counts[equivs[i]];
        if (nmatch == 0)
        {
          discards[i] = 1;
        }
        else if (nmatch > many)
        {
          discards[i] = 2;
        }
      }

      return discards;
    }

    /** Don't really discard the provisional lines except when they occur
       in a run of discardables, with nonprovisionals at the beginning
       and end.  */
    private void filterDiscards(final byte[] discards)
    {
      final int end = buffered_lines;

      for (int i = 0; i < end; i++)
      {
        /* Cancel provisional discards not in middle of run of discards.  */
        if (discards[i] == 2)
        {
          discards[i] = 0;
        }
        else if (discards[i] != 0)
        {
          /* We have found a nonprovisional discard.  */
          int j;
          int length;
          int provisional = 0;

          /* Find end of this run of discardable lines.
             Count how many are provisionally discardable.  */
          for (j = i; j < end; j++)
          {
            if (discards[j] == 0)
            {
              break;
            }

            if (discards[j] == 2)
            {
              ++provisional;
            }
          }

          /* Cancel provisional discards at end, and shrink the run.  */
          while (j > i && discards[j - 1] == 2)
          {
            discards[--j] = 0;
            --provisional;
          }

          /* Now we have the length of a run of discardable lines
             whose first and last are not provisional.  */
          length = j - i;

          /* If 1/4 of the lines in the run are provisional,
             cancel discarding of all provisional lines in the run.  */
          if (provisional * 4 > length)
          {
            while (j > i)
            {
              if (discards[--j] == 2)
              {
                discards[j] = 0;
              }
            }
          }
          else
          {
            int consec;
            int minimum = 1;
            int tem = length / 4;

            /* MINIMUM is approximate square root of LENGTH/4.
               A subrun of two or more provisionals can stand
               when LENGTH is at least 16.
               A subrun of 4 or more can stand when LENGTH >= 64.  */
            while ((tem = tem >> 2) > 0)
            {
              minimum *= 2;
            }

            minimum++;

            /* Cancel any subrun of MINIMUM or more provisionals
               within the larger run.  */
            for (j = 0, consec = 0; j < length; j++)
            {
              if (discards[i + j] != 2)
              {
                consec = 0;
              }
              else if (minimum == ++consec)
              {
                /* Back up to start of subrun, to cancel it all.  */
                j -= consec;
              }
              else if (minimum < consec)
              {
                discards[i + j] = 0;
              }
            }

            /* Scan from beginning of run
               until we find 3 or more nonprovisionals in a row
               or until the first nonprovisional at least 8 lines in.
               Until that point, cancel any provisionals.  */
            for (j = 0, consec = 0; j < length; j++)
            {
              if (j >= 8 && discards[i + j] == 1)
              {
                break;
              }

              if (discards[i + j] == 2)
              {
                consec = 0;
                discards[i + j] = 0;
              }
              else if (discards[i + j] == 0)
              {
                consec = 0;
              }
              else
              {
                consec++;
              }

              if (consec == 3)
              {
                break;
              }
            }

            /* I advances to the last line of the run.  */
            i += length - 1;

            /* Same thing, from end.  */
            for (j = 0, consec = 0; j < length; j++)
            {
              if (j >= 8 && discards[i - j] == 1)
              {
                break;
              }

              if (discards[i - j] == 2)
              {
                consec = 0;
                discards[i - j] = 0;
              }
              else if (discards[i - j] == 0)
              {
                consec = 0;
              }
              else
              {
                consec++;
              }

              if (consec == 3)
              {
                break;
              }
            }
          }
        }
      }
    }

    /** Actually discard the lines.
       @param discards flags lines to be discarded
     */
    private void discard(byte[] discards)
    {
      int end = buffered_lines;
      int j = 0;

      for (int i = 0; i < end; ++i)
      {
        if (minimal || discards[i] == 0)
        {
          undiscarded[j] = equivs[i];
          realindexes[j++] = i;
        }
        else
        {
          changed_flag[1 + i] = true;
        }
      }

      nondiscarded_lines = j;
    }

    file_data(Object[] data, Map h)
    {
      buffered_lines = data.length;

      equivs = new int[buffered_lines];
      undiscarded = new int[buffered_lines];
      realindexes = new int[buffered_lines];

      for (int i = 0; i < data.length; ++i)
      {
        Integer ir = (Integer) h.get(data[i]);

        if (ir == null)
        {
          h.put(data[i], new Integer(equivs[i] = equiv_max++));
        }
        else
        {
          equivs[i] = ir.intValue();
        }
      }
    }

    /** Adjust inserts/deletes of blank lines to join changes
       as much as possible.
       We do something when a run of changed lines include a blank
       line at one end and have an excluded blank line at the other.
       We are free to choose which blank line is included.
       `compareseq' always chooses the one at the beginning,
       but usually it is cleaner to consider the following blank line
       to be the "change".  The only exception is if the preceding blank line
       would join this change to other changes.
       @param f the file being compared against
     */
    void shift_boundaries(file_data f)
    {
      final boolean[] changed = changed_flag;
      final boolean[] other_changed = f.changed_flag;
      int i = 0;
      int j = 0;
      int i_end = buffered_lines;
      int preceding = -1;
      int other_preceding = -1;

      for (;;)
      {
        int start;
        int end;
        int other_start;

        /* Scan forwards to find beginning of another run of changes.
           Also keep track of the corresponding point in the other file.  */
        while (i < i_end && !changed[1 + i])
        {
          while (other_changed[1 + j++])
          {
            /* Non-corresponding lines in the other file
               will count as the preceding batch of changes.  */
            other_preceding = j;
          }

          i++;
        }

        if (i == i_end)
        {
          break;
        }

        start = i;
        other_start = j;

        for (;;)
        {
          /* Now find the end of this run of changes.  */
          while (i < i_end && changed[1 + i])
          {
            i++;
          }

          end = i;

          /* If the first changed line matches the following unchanged one,
             and this run does not follow right after a previous run,
             and there are no lines deleted from the other file here,
             then classify the first changed line as unchanged
             and the following line as changed in its place.  */

          /* You might ask, how could this run follow right after another?
             Only because the previous run was shifted here.  */
          if (end != i_end
              && equivs[start] == equivs[end]
              && !other_changed[1 + j]
              && end != i_end
              && !((preceding >= 0 && start == preceding) || (other_preceding >= 0 && other_start == other_preceding)))
          {
            changed[1 + end++] = true;
            changed[1 + start++] = false;
            ++i;
            /* Since one line-that-matches is now before this run
               instead of after, we must advance in the other file
               to keep in synch.  */
            ++j;
          }
          else
          {
            break;
          }
        }

        preceding = i;
        other_preceding = j;
      }
    }

    /** Number of elements (lines) in this file. */
    final int buffered_lines;

    /** Vector, indexed by line number, containing an equivalence code for
       each line.  It is this vector that is actually compared with that
       of another file to generate differences. */
    private final int[] equivs;

    /** Vector, like the previous one except that
       the elements for discarded lines have been squeezed out.  */
    final int[] undiscarded;

    /** Vector mapping virtual line numbers (not counting discarded lines)
       to real ones (counting those lines).  Both are origin-0.  */
    final int[] realindexes;

    /** Total number of nondiscarded lines. */
    int nondiscarded_lines;

    /** Array, indexed by real origin-1 line number,
       containing true for a line that is an insertion or a deletion.
       The results of comparison are stored here.  */
    boolean[] changed_flag;
  }

  class Partition
  {
    int xmid;
    int ymid;
    boolean lo_minimal;
    boolean hi_minimal;
  }
}
