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
package org.jmeld.util.node;

import org.jmeld.vc.*;
import org.jmeld.ui.text.*;

import java.io.*;

public class VersionControlBaseNode extends JMeldNode implements BufferNode {
    private VersionControlIF versionControl;
    private StatusResult.Entry entry;
    private FileNode fileNode;
    private File file;
    private VersionControlBaseDocument document;

    public VersionControlBaseNode(VersionControlIF versionControl, StatusResult.Entry entry, FileNode fileNode
            , File file) {
        super(entry.getName(), !file.isDirectory());

        this.versionControl = versionControl;
        this.entry = entry;
        this.file = file;
        this.fileNode = fileNode;
    }

    public File getFile() {
        return file;
    }

    public StatusResult.Entry getEntry() {
        return entry;
    }

    @Override
    public void resetContent() {
        document = null;
        initialize();
    }

    public boolean exists() {
        return true;
    }

    public VersionControlBaseDocument getDocument() {
        if (document == null) {
            document = new VersionControlBaseDocument(versionControl, entry, fileNode, file);
        }
        return document;
    }

    @Override
    public long getSize() {
        return getDocument().getBufferSize();
    }

    private void initialize() { }
}
