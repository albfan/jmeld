/*
   JMeld is a visual diff and merge tool.
   Copyright (C) 2007  Kees Kuip
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA  02110-1301  USA
 */
package org.jmeld.diff;

import org.gnu.diff.*;
import org.jmeld.*;

public class GNUDiff
    extends AbstractJMDiffAlgorithm
{
  public GNUDiff()
  {
  }

  public JMRevision diff(Object[] orig, Object[] rev)
      throws JMeldException
  {
    Diff diff;
    Diff.change change;

    try
    {
      diff = new Diff(orig, rev);
      change = diff.diff_2();
    }
    catch (Exception ex)
    {
      throw new JMeldException("Diff failed [" + getClass() + "]", ex);
    }

    return buildRevision(change, orig, rev);
  }

  private JMRevision buildRevision(Diff.change change, Object[] orig,
      Object[] rev)
  {
    JMRevision result;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    result = new JMRevision(orig, rev);
    while (change != null)
    {
      result.add(new JMDelta(new JMChunk(change.line0, change.deleted),
          new JMChunk(change.line1, change.inserted)));

      change = change.link;
    }

    return result;
  }
}
