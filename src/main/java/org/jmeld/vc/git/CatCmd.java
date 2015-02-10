package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.BaseFile;
import org.jmeld.vc.util.VcCmd;

import java.io.File;

public class CatCmd extends VcCmd<BaseFile> {
    private File file;
    private String reference;

    public CatCmd(File file) {
        this(file, "HEAD");
    }

    public CatCmd(File file, String reference) {
        this.file = file;
        this.reference = reference;
        initWorkingDirectory(file);
    }

    public Result execute() {
        super.execute("git", "show", getReferencePoint() +":"+file.getName());

        return getResult();
    }

    private String getReferencePoint() {
        if (reference.equals("index")) {
            return "";
        } else if (reference.equals("worktree")) {
            return "";
        } else {
            return reference;
        }
    }

    protected void build(byte[] data) {
        setResultData(new BaseFile(data));
    }
}
