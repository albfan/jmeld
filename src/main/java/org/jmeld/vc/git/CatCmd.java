package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.BaseFile;
import org.jmeld.vc.util.VcCmd;

import java.io.File;

public class CatCmd extends VcCmd<BaseFile> {
    // Instance variables:
    private File file;

    public CatCmd(File file) {
        this.file = file;

        initWorkingDirectory(file);
    }

    public Result execute() {
        super.execute("git", "cat-file", file.getAbsolutePath());

        return getResult();
    }

    protected void build(byte[] data) {
        setResultData(new BaseFile(data));
    }
}
