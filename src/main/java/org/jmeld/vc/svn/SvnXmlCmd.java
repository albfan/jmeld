package org.jmeld.vc.svn;

import org.jmeld.util.*;
import org.jmeld.vc.util.*;

import java.io.*;
import java.util.*;

public class SvnXmlCmd<T> extends VcCmd<T>
{
    private Class<T> clazz;

    public SvnXmlCmd(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void build(byte[] data)
    {
        try {
            InputStream is = new ByteArrayInputStream(data);
            setResultData(JaxbPersister.getInstance().load(clazz, is));
            is.close();
            setResult(Result.TRUE());
        } catch (Exception ex) {
            setResult(Result.FALSE(ex.getMessage(), ex));
        }
    }
}
