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
import org.jmeld.util.CharsetDetector;
import org.jmeld.util.node.FileNode;
import org.jmeld.vc.BaseFile;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.VersionControlIF;

import java.io.*;
import java.nio.charset.Charset;

public class VersionControlBaseDocument extends AbstractBufferDocument {

    private VersionControlIF versionControl;
    private StatusResult.Entry entry;
    private FileNode fileNode;
    private File file;
    private BaseFile baseFile;
    private boolean baseFileInitialized;

    public VersionControlBaseDocument(VersionControlIF versionControl, StatusResult.Entry entry, FileNode fileNode
            , File file) {
        this.versionControl = versionControl;
        this.entry = entry;
        this.fileNode = fileNode;
        this.file = file;

        try {
            setName(file.getCanonicalPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            setName(file.getName());
        }

        setShortName(file.getName());
    }

    public boolean isBaseFileInitialized() {
        return baseFileInitialized;
    }

    public void setBaseFileInitialized(boolean baseFileInitialized) {
        this.baseFileInitialized = baseFileInitialized;
    }

    @Override
    public int getBufferSize() {
        if (useBaseFile()) {
            BaseFile baseFile = getBaseFile();
            return baseFile == null ? -1 : baseFile.getLength();
        } else {
            return fileNode.getDocument().getBufferSize();
        }
    }

    @Override
    public Reader getReader() throws JMeldException {
        BufferedInputStream bais;

        if (useBaseFile()) {
            try {
                BaseFile baseFile = getBaseFile();
                bais = new BufferedInputStream(new ByteArrayInputStream(baseFile.getByteArray()));
                Charset charset = CharsetDetector.getInstance().getCharset(bais);
                return new BufferedReader(new InputStreamReader(bais, charset));
            } catch (Exception ex) {
                throw new JMeldException("Could not create FileReader for : " + file.getName(), ex);
            }
        } else if (entry.getStatus() == StatusResult.Status.unversioned
                || entry.getStatus() == StatusResult.Status.added) {
            return new StringReader("");
        } else {
            return fileNode.getDocument().getReader();
        }
    }

    @Override
    protected Writer getWriter() throws JMeldException {
        return null;
    }

    private boolean useBaseFile() {
        switch (entry.getStatus()) {
            case modified:
            case removed:
            case missing:
            case index_modified:
            case index_removed:
                return true;
            default:
                return false;
        }
    }

    private BaseFile getBaseFile() {
        //if (!isBaseFileInitialized()) {
            baseFile = versionControl.getBaseFile(file);
            setBaseFileInitialized(true);
        //}
        return baseFile;
    }

    @Override
    public boolean isReadonly() {
        return true;
    }
}
