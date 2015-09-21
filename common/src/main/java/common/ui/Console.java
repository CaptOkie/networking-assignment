package common.ui;

import java.util.Formatter;
import java.util.Scanner;

public class Console implements AutoCloseable {

    private final Formatter formatter;
    private final Scanner scanner;
    
    public String readLine(final String string) {
        formatter.format(string).flush();
        return scanner.nextLine();
    }
    
    public Console writeLine(final String string) {
        formatter.format(string + "\n").flush();
        return this;
    }
    
    public Console() {
        this.formatter = new Formatter(System.out);
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void close() throws Exception {
        formatter.close();
        scanner.close();
    }
}
