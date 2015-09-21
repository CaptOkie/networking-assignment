package client.ui.cmdline;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import client.ui.Operation;
import common.net.msg.Instruction;
import common.net.msg.Request;

public class Command {

    private Operation operation;
    private List<String> data;
    
    public Command(final Operation operation) {
        this(operation, Collections.emptyList());
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
        final Optional<Instruction> instruction = operation2Instruction(getOperation());
        if (instruction.isPresent()) {
            return new Request(path, instruction.get(), getData());
        }

        throw new CommandToRequestException("Cannot convert with the Operation " + getOperation());
    }
    
    private static Optional<Instruction> operation2Instruction(final Operation operation) {

        switch (operation) {
            case CD:
                return Optional.of(Instruction.CD);
            case GET: 
                return Optional.of(Instruction.GET);
            case LS:
                return Optional.of(Instruction.LS);
            case MKDIR:
                return Optional.of(Instruction.MKDIR);
            case PUT:
                return Optional.of(Instruction.PUT);
            default:
                return Optional.empty();
        }
    }
}
