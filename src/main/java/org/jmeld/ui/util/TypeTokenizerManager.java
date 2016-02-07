package org.jmeld.ui.util;

import org.jmeld.util.ObjectUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TypeTokenizerManager {
    private static TypeTokenizerManager instance;

    public static final String CHAR_TOKENIZER = "char";
    public static final String WORD_TOKENIZER = "word";
    public static final String NONWORD_TOKENIZER = "nonword";

    public static TypeTokenizerManager getInstance() {
        if (instance == null) {
            instance = new TypeTokenizerManager();
        }
        return instance;
    }

    public Vector<String> getTypeTokenizers() {
        Vector<String> result;

        result = new Vector<>();
        result.add(CHAR_TOKENIZER);
        result.add(WORD_TOKENIZER);
        result.add(NONWORD_TOKENIZER);

        return result;
    }
}
