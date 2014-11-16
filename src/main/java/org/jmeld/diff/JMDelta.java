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

import org.jmeld.util.*;

import java.util.*;

public class JMDelta {

    private static boolean debug = false;

    // Instance variables:
    private JMChunk original;
    private JMChunk revised;
    private TypeDiff type;
    private JMRevision revision;
    private JMRevision changeRevision;

    public JMDelta(JMChunk original, JMChunk revised) {
        this.original = original;
        this.revised = revised;

        initType();
    }

    void setRevision(JMRevision revision) {
        this.revision = revision;
    }

    public JMChunk getOriginal() {
        return original;
    }

    public JMChunk getRevised() {
        return revised;
    }

    public boolean isAdd() {
        return type == TypeDiff.ADD;
    }

    public boolean isDelete() {
        return type == TypeDiff.DELETE;
    }

    public boolean isChange() {
        return type == TypeDiff.CHANGE;
    }

    public void invalidateChangeRevision() {
        changeRevision = null;
    }

    public JMRevision getChangeRevision() {
        if (changeRevision == null) {
            changeRevision = createChangeRevision();
        }

        return changeRevision;
    }

    //TODO: Creates a Delta with chunks from the algorithm
    private JMRevision createChangeRevision() {
        char[] original1;
        Character[] original2;
        char[] revised1;
        Character[] revised2;
        List<String> o2;
        List<String> r2;
        JMRevision rev;
        JMRevision rev2;
        JMChunk o;
        JMChunk r;
        JMDelta d2;
        int anchor;
        int size;
        WordTokenizer wt;
        int[] oIndex;
        int[] rIndex;
        int oAnchor;
        int oLength;
        int rAnchor;
        int rLength;

        original1 = revision.getOriginalString(original).toCharArray();
        original2 = new Character[original1.length];
        for (int j = 0; j < original1.length; j++) {
            original2[j] = original1[j];
        }

        revised1 = revision.getRevisedString(revised).toCharArray();

        revised2 = new Character[revised1.length];
        for (int j = 0; j < revised1.length; j++) {
            revised2[j] = revised1[j];
        }

        try {
            wt = TokenizerFactory.getInnerDiffTokenizer();
            o2 = wt.getTokens(revision.getOriginalString(original));
            r2 = wt.getTokens(revision.getRevisedString(revised));
            rev = new JMDiff().diff(o2, r2, Ignore.NULL_IGNORE);

            oIndex = new int[o2.size()];
            for (int i = 0; i < o2.size(); i++) {
                oIndex[i] = o2.get(i).length();
                if (i > 0) {
                    oIndex[i] += oIndex[i - 1];
                }
                debug("oIndex[" + i + "] = " + oIndex[i] + " \"" + o2.get(i) + "\"");
            }

            rIndex = new int[r2.size()];
            for (int i = 0; i < r2.size(); i++) {
                rIndex[i] = r2.get(i).length();
                if (i > 0) {
                    rIndex[i] += rIndex[i - 1];
                }
                debug("rIndex[" + i + "] = " + rIndex[i] + " \"" + r2.get(i) + "\"");
            }

            rev2 = new JMRevision(original2, revised2);
            rev2.setIgnore(Ignore.NULL_IGNORE);
            for (JMDelta d : rev.getDeltas()) {
                o = d.getOriginal();
                r = d.getRevised();

                anchor = o.getAnchor();
                size = o.getSize();
                oAnchor = anchor == 0 ? 0 : oIndex[anchor - 1];
                oLength = size > 0 ? (oIndex[anchor + size - 1] - oAnchor) : 0;

                anchor = r.getAnchor();
                size = r.getSize();
                rAnchor = anchor == 0 ? 0 : rIndex[anchor - 1];
                rLength = size > 0 ? (rIndex[anchor + size - 1] - rAnchor) : 0;

                d2 = new JMDelta(new JMChunk(oAnchor, oLength), new JMChunk(rAnchor,
                        rLength));
                rev2.add(d2);

                debug("delta = " + d + " -> " + d2);
            }

            return rev2;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    void initType() {
        if (original.getSize() > 0 && revised.getSize() == 0) {
            type = TypeDiff.DELETE;
        } else if (original.getSize() == 0 && revised.getSize() > 0) {
            type = TypeDiff.ADD;
        } else {
            type = TypeDiff.CHANGE;
        }
    }

    @Override
    public boolean equals(Object o) {
        JMDelta d;

        if (!(o instanceof JMDelta)) {
            return false;
        }

        d = (JMDelta) o;
        if (revision != d.revision) {
            return false;
        }

        if (!original.equals(d.original) || !revised.equals(d.revised)) {
            return false;
        }

        return true;
    }

    private void debug(String s) {
        if (debug) {
            System.out.println(s);
        }
    }

    public TypeDiff getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": org[" + original + "] rev[" + revised + "]";
    }
}
