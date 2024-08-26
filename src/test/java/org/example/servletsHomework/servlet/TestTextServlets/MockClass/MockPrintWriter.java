package org.example.servletsHomework.servlet.TestTextServlets.MockClass;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MockPrintWriter extends PrintWriter {
    public List<String> writtenStrings = new ArrayList<>();

    public MockPrintWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String s) {
        writtenStrings.add(s);
        super.write(s);
    }

    public void clearWrittenStrings() {
        writtenStrings.clear();
    }

    public List<String> getWrittenStrings() {
        return writtenStrings;
    }

}
