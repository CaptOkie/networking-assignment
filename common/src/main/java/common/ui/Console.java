package common.ui;

import java.util.Collection;
import java.util.Formatter;
import java.util.Scanner;

import common.utils.StringUtils;

public class Console implements AutoCloseable {

    private final Formatter formatter;
    private final Scanner scanner;
    
    public Console() {
        this.formatter = new Formatter(System.out);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a line from the console, with the specified prompt.
     * @param prompt The prompt to show.
     * @return The line that was read.
     */
    public String readLine(final String prompt) {
        formatter.format(prompt).flush();
        return scanner.nextLine();
    }
    
    /**
     * Writes a line to the console.
     * @param line The line to write.
     * @return <code>this</code> to allow for chaining.
     */
    public Console writeLine(final String line) {
        formatter.format(line + System.lineSeparator()).flush();
        return this;
    }
    
    /**
     * Writes the lines to the console.
     * @param lines The lines to write.
     * @return <code>this</code> to allow for chaining.
     */
    public Console writeLines(final Collection<? extends String> lines) {
        
        return writeLine(StringUtils.join(System.lineSeparator(), lines));
    }
    
    @Override
    public void close() {
        try {
            formatter.close();
        }
        finally {
            scanner.close();
        }
    }
}
