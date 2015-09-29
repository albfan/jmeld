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

import org.apache.commons.jrcs.diff.*;
import org.jmeld.*;

public class MyersDiff
    extends AbstractJMDiffAlgorithm
{
  public MyersDiff()
  {
  }

  public JMRevision diff(Object[] orig, Object[] rev)
      throws JMeldException
  {
    org.apache.commons.jrcs.diff.myers.MyersDiff diff;
    Revision revision;

    try
    {
      diff = new org.apache.commons.jrcs.diff.myers.MyersDiff();
      diff.checkMaxTime(isMaxTimeChecked());
      revision = diff.diff(orig, rev);
    }
    catch (Exception ex)
    {
      throw new JMeldException("Diff failed [" + getClass() + "]", ex);
    }

    return buildRevision(revision, orig, rev);
  }

  private JMRevision buildRevision(Revision revision, Object[] orig,
      Object[] rev)
  {
    JMRevision result;
    Delta delta;
    Chunk original;
    Chunk revised;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    result = new JMRevision(orig, rev);
    for (int i = 0; i < revision.size(); i++)
    {
      delta = revision.getDelta(i);
      original = delta.getOriginal();
      revised = delta.getRevised();

      result.add(new JMDelta(new JMChunk(original.anchor(), original.size()),
          new JMChunk(revised.anchor(), revised.size())));
    }

    return result;
  }
}
