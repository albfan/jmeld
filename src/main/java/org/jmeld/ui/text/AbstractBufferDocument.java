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
package org.jmeld.ui.text;

import org.jmeld.JMeldException;
import org.jmeld.util.StopWatch;
import org.jmeld.util.StringUtil;
import org.jmeld.vc.BlameIF;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractBufferDocument implements BufferDocumentIF, DocumentListener {

    private String name;
    private String shortName;
    private Line[] lineArray;
    private int[] lineOffsetArray;
    private PlainDocument document;
    private MyGapContent content;
    private List<BufferDocumentChangeListenerIF> listeners;

    // Variables to detect if this document has been changed (and needs to be saved!)
    private boolean changed;
    private int originalLength;
    private int digest;

    public AbstractBufferDocument() {
        listeners = new ArrayList<BufferDocumentChangeListenerIF>();
    }

    public void addChangeListener(BufferDocumentChangeListenerIF listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(BufferDocumentChangeListenerIF listener) {
        listeners.remove(listener);
    }

    abstract int getBufferSize();

    abstract public Reader getReader()
            throws JMeldException;

    abstract Writer getWriter()
            throws JMeldException;

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public PlainDocument getDocument() {
        return document;
    }

    public boolean isChanged() {
        return changed;
    }

    public Line[] getLines() {
        initLines();

        return lineArray;
    }

    public String getLineText(int lineNumber) {
        Line[] la;

        la = getLines();
        if (la == null) {
            return null;
        }

        if (lineNumber >= la.length || lineNumber < 0) {
            return "<NO LINE>";
        }

        return la[lineNumber].toString();
    }

    public int getNumberOfLines() {
        return getLines().length;
    }

    public int getOffsetForLine(int lineNumber) {
        Line[] la;

        if (lineNumber < 0) {
            return -1;
        }

        if (lineNumber == 0) {
            return 0;
        }

        la = getLines();
        if (la == null) {
            return -1;
        }

        if (lineNumber > la.length) {
            lineNumber = la.length;
        }

        return la[lineNumber - 1].getOffset();
    }

    public int getLineForOffset(int offset) {
        int searchIndex;
        Line[] la;

        if (offset < 0) {
            return 0;
        }

        la = getLines();
        if (la == null) {
            return 0;
        }

        if (offset >= lineOffsetArray[lineOffsetArray.length - 1]) {
            return lineOffsetArray.length - 1;
        }

        searchIndex = Arrays.binarySearch(lineOffsetArray, offset);
        if (searchIndex >= 0) {
            return searchIndex + 1;
        }

        return (-searchIndex) - 1;
    }

    public void read()
            throws JMeldException {
        try {
            Reader reader;
            StopWatch stopWatch;

            if (document != null) {
                document.removeDocumentListener(this);
            }

            stopWatch = new StopWatch();
            stopWatch.start();
            System.out.println("before read : " + this);

            content = new MyGapContent(getBufferSize() + 500);
            document = new PlainDocument(content);

            reader = getReader();
            new DefaultEditorKit().read(reader, document, 0);
            reader.close();

            System.out.println("create document took " + stopWatch.getElapsedTime());
            document.addDocumentListener(this);

            reset();

            initLines();
            initDigest();
        } catch (JMeldException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JMeldException("Problem reading document (name=" + getName()
                    + ") in buffer", ex);
        }
    }

    private void initLines() {
        Element paragraph;
        Element e;
        int size;
        Line line;

        if (lineArray != null) {
            return;
        }

        paragraph = document.getDefaultRootElement();
        size = paragraph.getElementCount();
        //lineArray = new Line[size - 1];
        lineArray = new Line[size];
        lineOffsetArray = new int[lineArray.length];
        for (int i = 0; i < lineArray.length; i++) {
            e = paragraph.getElement(i);
            line = new Line(e);

            lineArray[i] = line;
            lineOffsetArray[i] = line.getOffset();
        }
    }

    public void reset() {
        lineArray = null;
        lineOffsetArray = null;
    }

    public void write()
            throws JMeldException {
        Writer out;

        System.out.println("write : " + getName());
        try {
            out = getWriter();
            new DefaultEditorKit().write(out, document, 0, document.getLength());
            out.flush();
            out.close();

            initDigest();
        } catch (JMeldException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JMeldException("Problem writing document (name=" + getName()
                    + ") from buffer", ex);
        }
    }

    class MyGapContent
            extends GapContent {
        public MyGapContent(int length) {
            super(length);
        }

        char[] getCharArray() {
            return (char[]) getArray();
        }

        public char getChar(int offset) {
            int g0;
            int g1;

            g0 = getGapStart();
            g1 = getGapEnd();

            if (offset >= g0) {
                // Take into account the gap!
                // This offset is above the gap.
                offset = g1 + offset - g0;
            }

            return getCharArray()[offset];
        }

        public boolean equals(MyGapContent c2, int start1, int end1, int start2) {
            char[] array1;
            char[] array2;
            int g1_0;
            int g1_1;
            int g2_0;
            int g2_1;
            int size;
            int o1;
            int o2;

            array1 = getCharArray();
            array2 = c2.getCharArray();

            g1_0 = getGapStart();
            g1_1 = getGapEnd();
            g2_0 = c2.getGapStart();
            g2_1 = c2.getGapEnd();

            if (start1 >= g1_0) {
                o1 = start1 + g1_1 - g1_0;
            } else {
                o1 = start1;
            }

            if (start2 >= g2_0) {
                o2 = start2 + g2_1 - g2_0;
            } else {
                o2 = start2;
            }

            size = end1 - start1;
            for (int i = 0; i < size; i++, o1++, o2++) {
                if (o1 == g1_0) {
                    o1 += g1_1 - g1_0;
                }

                if (o2 == g2_0) {
                    o2 += g2_1 - g2_0;
                }

                if (array1[o1] != array2[o2]) {
                    return false;
                }
            }

            return true;
        }

        public int hashCode(int start, int end) {
            char[] array;
            int g0;
            int g1;
            int size;
            int h;
            int o;

            h = 0;

            array = getCharArray();

            g0 = getGapStart();
            g1 = getGapEnd();

            // Mind the gap!
            if (start >= g0) {
                o = start + g1 - g0;
            } else {
                o = start;
            }

            size = end - start;
            for (int i = 0; i < size; i++, o++) {
                // Mind the gap!
                if (o == g0) {
                    o += g1 - g0;
                }

                h = 31 * h + array[o];
            }

            if (h == 0) {
                h = 1;
            }

            return h;
        }

        public int getDigest() {
            return hashCode(0, document.getLength());
        }
    }

    public class Line
            implements Comparable {
        Element element;

        Line(Element element) {
            this.element = element;
        }

        MyGapContent getContent() {
            return content;
        }

        public int getOffset() {
            return element.getEndOffset();
        }

        public void print() {
            System.out.printf("[%08d]: %s\n", getOffset(), StringUtil
                    .replaceNewLines(toString()));
        }

        @Override
        public boolean equals(Object o) {
            Element element2;
            Line line2;
            int start1;
            int start2;
            int end1;
            int end2;

            if (!(o instanceof Line)) {
                return false;
            }

            line2 = ((Line) o);
            element2 = line2.element;

            start1 = element.getStartOffset();
            end1 = element.getEndOffset();
            start2 = element2.getStartOffset();
            end2 = element2.getEndOffset();

            // If the length is different the element is not equal!
            if ((end1 - start1) != (end2 - start2)) {
                return false;
            }

            return content.equals(line2.getContent(), start1, end1, start2);
        }

        @Override
        public int hashCode() {
            return content.hashCode(element.getStartOffset(), element.getEndOffset());
        }

        @Override
        public String toString() {
            try {
                return content.getString(element.getStartOffset(),
                        element.getEndOffset() - element.getStartOffset());
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }

        public int compareTo(Object line) {
            return toString().compareTo(((Line) line).toString());
        }
    }

    public void print() {
        Line[] la;

        la = getLines();
        if (la != null) {
            for (int lineNumber = 0; lineNumber < la.length; lineNumber++) {
                System.out.printf("[%05d]", lineNumber);
                la[lineNumber].print();
            }
        }
    }

    public void changedUpdate(DocumentEvent de) {
        documentChanged(de);
    }

    public void insertUpdate(DocumentEvent de) {
        documentChanged(de);
    }

    public void removeUpdate(DocumentEvent de) {
        documentChanged(de);
    }

    private void initDigest() {
        originalLength = document != null ? document.getLength() : 0;
        digest = createDigest();
        changed = false;

        fireDocumentChanged(new JMDocumentEvent(this));
    }

    public int createDigest() {
        return content.getDigest();
    }

    private void documentChanged(DocumentEvent de) {
        boolean newChanged;
        int newDigest;
        int startLine;
        int numberOfLinesChanged;
        JMDocumentEvent jmde;
        String text;

        jmde = new JMDocumentEvent(this, de);
        numberOfLinesChanged = 0;

        if (lineArray != null) {
            // Make large documents perform well!
            if (de.getType() == DocumentEvent.EventType.INSERT) {
                try {
                    text = document.getText(de.getOffset(), de.getLength());
                } catch (BadLocationException ex) {
                }
            }

            numberOfLinesChanged = getLines().length;
            reset();
            numberOfLinesChanged = getLines().length - numberOfLinesChanged;

            startLine = getLineForOffset(de.getOffset() + 1);
            if (startLine < 0) {
                System.out.println("haha");
            }

            jmde.setStartLine(startLine);
            jmde.setNumberOfLines(numberOfLinesChanged);
        }

        newChanged = false;
        if (document.getLength() != originalLength) {
            newChanged = true;
        } else {
            // Calculate the digest in order to see of a buffer has been
            //   changed (and should be saved)
            newDigest = createDigest();
            if (newDigest != digest) {
                newChanged = true;
            }
        }

        if (newChanged != changed) {
            changed = newChanged;
        }

        fireDocumentChanged(jmde);
    }

    private void fireDocumentChanged(JMDocumentEvent de) {
        for (BufferDocumentChangeListenerIF listener : listeners) {
            listener.documentChanged(de);
        }
    }

    public BlameIF getVersionControlBlame() {
        return null;
    }

    public boolean isReadonly() {
        return false;
    }

    public String toString() {
        return "Document[name=" + name + "]";
    }
}
