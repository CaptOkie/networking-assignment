package client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
