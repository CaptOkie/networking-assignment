package common.ui;

import java.util.Formatter;
import java.util.Scanner;

public class Console implements AutoCloseable {

    private final Formatter formatter;
    private final Scanner scanner;
    
    public String readLine(final String str) {
        formatter.format(str).flush();
        return scanner.nextLine();
    }
    
    public void writeLine(final String str) {
        formatter.format(str).flush();
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
