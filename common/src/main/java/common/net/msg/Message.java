package common.net.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Message implements Serializable {

    private static final long serialVersionUID = -1384590467857491672L;
    
    private Instruction instruction;
    private List<String> data;
    
    public Message(final Instruction instruction) {
        this(instruction, Collections.emptyList());
    }
    
    public Message(final Instruction instruction, final List<? extends String> data) {
        this.instruction = instruction;
        this.data = new ArrayList<>(data);
    }
    
    public Instruction getInstruction() {
        return instruction;
    }
    
    public void setInstruction(final Instruction instruction) {
        this.instruction = instruction;
    }
    
    public List<String> getData() {
        return data;
    }
}
