package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.util.VcCmd;

import java.io.File;

public class ActiveCmd extends VcCmd<Boolean> {
    private File file;

    public ActiveCmd(File file) {
        this.file = file;
    }

    public Result execute() {
        // If log can be issued then we have a git working directory!
        super.execute("git", "log", "-n 1", file.getAbsolutePath());

        return getResult();
    }

    protected void build(byte[] data) {
        setResultData(Boolean.TRUE);
    }
}
