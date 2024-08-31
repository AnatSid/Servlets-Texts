package org.example.servletsHomework.servlet.stubTests.stubClass;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class StubPrintWriter extends PrintWriter {
    public List<String> writtenStrings = new ArrayList<>();

    public StubPrintWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String s) {
        writtenStrings.add(s);
        super.write(s);
    }


    public List<String> getWrittenStrings() {
        return writtenStrings;
    }

}
