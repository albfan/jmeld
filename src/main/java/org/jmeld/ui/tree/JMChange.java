package org.jmeld.ui.tree;

/**
 * Created by alberto on 16/11/14.
 */
public class JMChange {
    private int initLine;
    private int numLines;
    private int startCol;
    private int endCol;
    private int modifiedChars;

    public JMChange(int initLine, int numLines, int startCol, int endCol, int modifiedChars) {
        this.initLine = initLine;
        this.numLines = numLines;
        this.startCol = startCol;
        this.endCol = endCol;
        this.modifiedChars = modifiedChars;
    }

    @Override
    public String toString() {
        return "L:"+initLine+"-"+(initLine+(numLines-1))+",C:"+startCol+"-"+endCol+",<"+modifiedChars+">";
    }
}
