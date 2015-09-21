package common.ui;

import java.util.Formatter;
import java.util.List;
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
    
    public Console writeLines(final List<? extends String> lines) {
        return writeLine(String.join(System.lineSeparator(), lines));
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
