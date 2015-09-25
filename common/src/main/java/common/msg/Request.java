package common.msg;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Request implements Serializable {

    private static final long serialVersionUID = -1384590467857491672L;
    
    private String path;
    private Instruction instruction;
    private List<String> data;
    
    public Request(final Path path, final Instruction instruction) {
        this(path, instruction, Collections.emptyList());
    }
    
    public Request(final Path path, final Instruction instruction, final List<? extends String> data) {
        this.path = path.toString();
        this.instruction = instruction;
        this.data = new ArrayList<>(data);
    }
    
    public Path getPath() {
        return Paths.get(path);
    }
    
    public void setPath(final Path path) {
        this.path = path.toString();
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
