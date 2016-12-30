package net.doughughes.testifier.output;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.ArrayList;


public class OutputStreamInterceptor extends PrintStream {

    private PrintStream out;
    private ArrayList printed = new ArrayList();

    public OutputStreamInterceptor(PrintStream out) {
        super(out);
        this.out = out;
    }

    public PrintStream getOut() {
        return out;
    }

    public ArrayList getPrinted() {
        return printed;
    }

    // overridden methods

    @Override
    public void print(boolean b) {
        printed.add(b);
        super.print(b);
    }

    @Override
    public void print(char c) {
        printed.add(c);
        super.print(c);
    }

    @Override
    public void print(int i) {
        printed.add(i);
        super.print(i);
    }

    @Override
    public void print(long l) {
        printed.add(l);
        super.print(l);
    }

    @Override
    public void print(float f) {
        printed.add(f);
        super.print(f);
    }

    @Override
    public void print(double d) {
        printed.add(d);
        super.print(d);
    }

    @Override
    public void print(char[] s) {
        printed.add(s);
        super.print(s);
    }

    @Override
    public void print(String s) {
        printed.add(s);
        super.print(s);
    }

    @Override
    public void print(Object obj) {
        printed.add(obj);
        super.print(obj);
    }

    @Override
    public void println(Object obj) {
        printed.add(obj);
        printed.add("\n");
        String s = String.valueOf(obj);
        synchronized (this) {
            super.print(s + "\n");
        }
    }

    @Override
    public void println(){
        printed.add("\n");
        String s = String.valueOf("\n");
        super.println();
    }


}
