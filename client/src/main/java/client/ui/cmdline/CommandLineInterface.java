package client.ui.cmdline;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import client.ui.Operation;
import common.ui.Console;

public class CommandLineInterface implements AutoCloseable {

    private final Console console;
    
    public CommandLineInterface() {
        this.console = new Console();
    }
    
    public Optional<Command> getCommand() {
        
        final String line = console.readLine("> ");
        if (line == null) {
            return Optional.empty();
        }
        
        final List<String> items = Arrays.asList(line.split("\\s+"));
        if (items.size() <= 0) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(new Command(Operation.valueOf(items.get(0).toUpperCase()), items.subList(1, items.size())));
        }
        catch (final IllegalArgumentException e) {
            return Optional.empty();
        }
    }
    
    public CommandLineInterface showError(final CommandLineError error) {
        console.writeLine(error.getMsg());
        return this;
    }
    
    public CommandLineInterface showHelp() {
        console.writeLines(Arrays.stream(Operation.values()).map(operation -> operation + ": " + operation.getDesc()).collect(Collectors.toList()));
        return this;
    }
    
    public CommandLineInterface showPath(final Path path) {
        console.writeLine(path.toString());
        return this;
    }
    
    public CommandLineInterface showFiles(final Collection<String> files) {
        console.writeLines(files);
        return this;
    }

    @Override
    public void close() {
        console.close();
    }
}
