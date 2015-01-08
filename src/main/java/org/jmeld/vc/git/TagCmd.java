package org.jmeld.vc.git;

import org.jmeld.util.Result;
import org.jmeld.vc.util.VcCmd;

import java.io.*;
import java.util.Vector;

public class TagCmd extends VcCmd<TagData> {
    private File file;

    public TagCmd(File file) {
        this.file = file;
        initWorkingDirectory(file);
    }

    public Result execute() {
        super.execute("git", "tag");
        return getResult();
    }

    protected void build(byte[] data) {
        TagData tagData = new TagData();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        try {
            String text;
            while ((text = reader.readLine()) != null) {
                String tag = text;
                tagData.add(tag);
            }
        } catch (IOException ex) {
        }

        setResultData(tagData);
    }

    public static void main(String[] args) {
        TagCmd cmd;

        File file = parseFile(args);
        if (file == null) {
            return;
        }
        cmd = new TagCmd(file);
        if (cmd.execute().isTrue()) {
            for (String tag : cmd.getResultData().getTags()) {
                System.out.println(tag);
            }
        } else {
            cmd.printError();
        }
    }
}

class TagData {
    Vector<String> tags;

    public TagData() {
        tags = new Vector<String>();
    }

    public void add(String tag) {
        tags.add(tag);
    }

    public Vector<String> getTags() {
        return tags;
    }
}



