package common.ui;

import java.util.Collection;
import java.util.Formatter;
import java.util.Scanner;

public class Console implements AutoCloseable {

    private final Formatter formatter;
    private final Scanner scanner;
    
    public String readLine(final String prompt) {
        formatter.format(prompt).flush();
        return scanner.nextLine();
    }
    
    public Console writeLine(final String line) {
        formatter.format(line + System.lineSeparator()).flush();
        return this;
    }
    
    public Console writeLines(final Collection<? extends String> lines) {
        return writeLine(String.join(System.lineSeparator(), lines));
    }
    
    public Console() {
        this.formatter = new Formatter(System.out);
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void close() {
        formatter.close();
        scanner.close();
    }
}
