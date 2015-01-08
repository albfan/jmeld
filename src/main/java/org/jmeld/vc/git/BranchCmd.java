package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.util.VcCmd;

import java.io.*;
import java.util.Vector;

public class BranchCmd extends VcCmd<BranchData> {
    private File file;

    public BranchCmd(File file) {
        this.file = file;
        initWorkingDirectory(file);
    }

    public Result execute() {
        super.execute("git", "branch");
        return getResult();
    }

    protected void build(byte[] data) {
        BranchData branchData = new BranchData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        try {
            String text;
            while ((text = reader.readLine()) != null) {
                String branch = text.substring(2);
                branchData.add(branch);
            }
        } catch (IOException ex) {
        }

        setResultData(branchData);
    }

    public static void main(String[] args) {
        BranchCmd cmd;

        File file = parseFile(args);
        if (file == null) {
            return;
        }
        cmd = new BranchCmd(file);
        if (cmd.execute().isTrue()) {
            for (String branch : cmd.getResultData().getBranchs()) {
                System.out.println(branch);
            }
        } else {
            cmd.printError();
        }
    }
}

class BranchData {
    Vector<String> branchs;

    public BranchData() {
        branchs = new Vector<String>();
    }

    public void add(String branch) {
        branchs.add(branch);
    }

    public Vector<String> getBranchs() {
        return branchs;
    }
}



