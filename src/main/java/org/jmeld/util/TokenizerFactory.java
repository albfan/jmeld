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
package org.jmeld.util;

import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.util.TypeTokenizerManager;
import org.jmeld.util.conf.ConfigurationListenerIF;

public class TokenizerFactory implements ConfigurationListenerIF {
    private static TokenizerFactory instance;

    private Tokenizer innerDiffTokenizer;
    private Tokenizer fileNameTokenizer;

    public static TokenizerFactory getInstance() {
        if (instance == null) {
            instance = new TokenizerFactory();
        }
        return instance;
    }

    private TokenizerFactory() {
        JMeldSettings.getInstance().addConfigurationListener(this);
    }

    public synchronized Tokenizer getInnerDiffTokenizer() {
        if (innerDiffTokenizer == null) {
            Tokenizer innerDiffTokenizer;
            String tokenizerName = JMeldSettings.getInstance().getEditor().getTypeTokenizerName();
            if (TypeTokenizerManager.CHAR_TOKENIZER.equals(tokenizerName)) {
                innerDiffTokenizer = new WordTokenizer(".");
            } else if (TypeTokenizerManager.WORD_TOKENIZER.equals(tokenizerName)) {
                innerDiffTokenizer = new WordTokenizer("\\b\\B*");
            } else if (TypeTokenizerManager.NONWORD_TOKENIZER.equals(tokenizerName)) {
                innerDiffTokenizer = new WordTokenizer("\\s|;|:|\\(|\\)|\\[|\\]|[-+*&^%\\/}{=<>`'\"|]+|\\.");
            } else {
                //TODO: Unknown
                innerDiffTokenizer = null;
            }
            setInnerDiffTokenizer(innerDiffTokenizer);
        }

        return innerDiffTokenizer;
    }

    public synchronized Tokenizer getFileNameTokenizer() {
        if (fileNameTokenizer == null) {
            setFileNameTokenizer(new WordTokenizer("[ /\\\\]+"));
        }

        return fileNameTokenizer;
    }

    private void setInnerDiffTokenizer(Tokenizer innerDiffTokenizer) {
        this.innerDiffTokenizer = innerDiffTokenizer;
    }

    private void setFileNameTokenizer(Tokenizer fileNameTokenizer) {
        this.fileNameTokenizer = fileNameTokenizer;
    }

    @Override
    public void configurationChanged() {
        setInnerDiffTokenizer(null);
        setFileNameTokenizer(null);
    }
}
