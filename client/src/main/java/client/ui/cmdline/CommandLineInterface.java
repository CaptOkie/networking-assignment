package client.ui.cmdline;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import client.ui.Operation;
import common.ui.Console;

public class CommandLineInterface implements AutoCloseable {

    private final Console console;
    
    public CommandLineInterface() {
        this.console = new Console();
    }
    
    public Command getCommand() {
        
        final String line = console.readLine("> ");
        if (line == null) {
            return null;
        }
        
        final List<String> items = Arrays.asList(line.split("\\s+"));
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
    
    public CommandLineInterface showError(final CommandLineError error) {
        console.writeLine(error.getMsg());
        return this;
    }
    
    public CommandLineInterface showHelp() {
        
        final List<String> lines = new ArrayList<>();
        for (final Operation operation : Operation.values()) {
            lines.add(operation + ": " + operation.getDesc());
        }
        
        console.writeLines(lines);
        return this;
    }
    
    public CommandLineInterface showPath(final Path path) {
        console.writeLine(path.toString());
        return this;
    }
    
    public CommandLineInterface showFiles(final Collection<? extends String> files) {
        console.writeLines(files);
        return this;
    }

    @Override
    public void close() {
        console.close();
    }
}
