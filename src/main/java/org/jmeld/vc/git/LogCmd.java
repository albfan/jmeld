package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.StatusResult;
import org.jmeld.vc.svn.SvnXmlCmd;
import org.jmeld.vc.util.VcCmd;

import java.io.*;
import java.util.Vector;

public class LogCmd extends VcCmd<LogData> {
    private File file;

    public LogCmd(File file) {
        this.file = file;
        initWorkingDirectory(file);
    }

    public Result execute() {
        super.execute("git", "log", "--oneline", "--all");
        return getResult();
    }

    protected void build(byte[] data) {
        LogData logData = new LogData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        try {
            String text;
            while ((text = reader.readLine()) != null) {
                String revision = text.substring(0, 7);
                logData.add(revision);
            }
        } catch (IOException ex) {
        }

        setResultData(logData);
    }

    public static void main(String[] args) {
        LogCmd cmd;

        File file = parseFile(args);
        if (file == null) {
            return;
        }
        cmd = new LogCmd(file);
        if (cmd.execute().isTrue()) {
            for (String revision : cmd.getResultData().getRevisions()) {
                System.out.println(revision);
            }
        } else {
            cmd.printError();
        }
    }
}

class LogData {
    Vector<String> revisions;

    public LogData() {
        revisions = new Vector<String>();
    }

    public void add(String revision) {
        revisions.add(revision);
    }

    public Vector<String> getRevisions() {
        return revisions;
    }
}
