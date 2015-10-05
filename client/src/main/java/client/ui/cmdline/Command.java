package client.ui.cmdline;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.ui.Operation;
import common.msg.Request;

/**
 * Command from the client 
 *
 */
public class Command {

    private Operation operation;
    private List<String> data;
    
    public Command(final Operation operation) {
        this(operation, Collections.<String> emptyList());
    }
    
    public Command(final Operation operation, final List<? extends String> data) {
        this.operation = operation;
        this.data = new ArrayList<>(data);
    }
    
    public Operation getOperation() {
        return operation;
    }
    
    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    
    public List<String> getData() {
        return data;
    }
    
    public Request toRequest(final Path path) {
        if (getOperation().getInstruction() != null) {
            return new Request(path, getOperation().getInstruction(), getData());
        }

        throw new CommandToRequestException("Cannot convert with the Operation " + getOperation());
    }
}
