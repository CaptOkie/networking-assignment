package client.ui.cmdline;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import client.ui.Operation;
import common.ui.Console;

/**
 * The user interface for the client 
 *
 */
public class CommandLineInterface implements AutoCloseable {

    private final Console console;
    
    public CommandLineInterface() {
        this.console = new Console();
    }
    
    /**
     * gets a command (string) from the client
     * @return a command (object)
     */
    public Command getCommand() {
        
        final String line = console.readLine("> ");
        if (line == null) {
            return null;
        }
        
        final List<String> items = Arrays.asList(line.split("\\s+", 2));
        if (items.size() <= 0) {
            return null;
        }
        
        try {
            return new Command(Operation.valueOf(items.get(0).toUpperCase()), items.subList(1, items.size()));
        }
        catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * gets ip address from user
     * @return the ip
     */
    public String getIPAddress() {
        final String line = console.readLine("Connect To: > ");
        return line == null ? "" : line;
    }
    
    /**
     * Asks the user a yes or no question
     * @param question the question being asked
     * @return the answer
     */
    public YesOrNo getYesOrNo(final String question) {
        final String line = console.readLine(question + " [Y/n] > ");
        try {
            return YesOrNo.valueOf(line.toUpperCase());
        }
        catch (final IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Shows errors
     * @param error the error
     * @return this for method chaining
     */
    public CommandLineInterface showError(final CommandLineError error) {
        console.writeLine(error.getMsg());
        return this;
    }
    
    /**
     * shows the command line help
     * @return this for method chaining
     */
    public CommandLineInterface showHelp() {
        
        final List<String> lines = new ArrayList<>();
        for (final Operation operation : Operation.values()) {
            lines.add(operation + ": " + operation.getDesc());
        }
        
        console.writeLines(lines);
        return this;
    }
    
    /**
     * Shows the path
     * @param path the path
     * @return this for method chaining
     */
    public CommandLineInterface showPath(final Path path) {
        console.writeLine(path.toString());
        return this;
    }
    
    /**
     * Shows the files
     * @param files the files
     * @return this for method chaining
     */
    public CommandLineInterface showFiles(final Collection<? extends String> files) {
        console.writeLines(files);
        return this;
    }

    @Override
    public void close() {
        console.close();
    }
}
