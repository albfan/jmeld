package org.gnu.diff;

import org.apache.commons.jrcs.diff.*;

public class GNUDiff
       implements DiffAlgorithm
{
  public GNUDiff()
  {
  }

  public Revision diff(Object[] orig, Object[] rev)
    throws DifferentiationFailedException
  {
    Diff        diff;
    Diff.change change;

    diff = new Diff(orig, rev);
    change = diff.diff_2(false);

    return buildRevision(change, orig, rev);
  }

  /**
   * Constructs a {@link Revision} from a difference path.
   *
   * @param change The change script.
   * @param orig The original sequence.
   * @param rev The revised sequence.
   * @return A {@link Revision} script corresponding to the path.
   * @throws DifferentiationFailedException if a {@link Revision} could
   *         not be built from the given path.
   */
  private Revision buildRevision(Diff.change change, Object[] orig,
    Object[] rev)
  {
    Revision revision;
    Delta    delta;

    if (orig == null)
    {
      throw new IllegalArgumentException("original sequence is null");
    }

    if (rev == null)
    {
      throw new IllegalArgumentException("revised sequence is null");
    }

    revision = new Revision();

    while (change != null)
    {
      delta = Delta.newDelta(new Chunk(orig, change.line0, change.deleted),
          new Chunk(rev, change.line1, change.inserted));
      revision.addDelta(delta);

      change = change.link;
    }

    return revision;
  }
}
