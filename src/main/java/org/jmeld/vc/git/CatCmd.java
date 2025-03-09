package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.BaseFile;
import org.jmeld.vc.util.VcCmd;

import java.io.File;

public class CatCmd extends VcCmd<BaseFile> {
    private final GitVersionControl vc;
    private File file;
    private String reference;

    public CatCmd(GitVersionControl vc ,File file) {
        this(vc, file, "HEAD");
    }

    public CatCmd(GitVersionControl vc, File file, String reference) {
        this.vc = vc;
        this.file = file;
        this.reference = reference;
        initWorkingDirectory(file);
    }

    public Result execute() {
        String rootPath = vc.getRootPath();
        super.execute("git", "-C", rootPath, "show", getReferencePoint() +":"+file.getPath().replaceFirst(rootPath+"/", ""));

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
