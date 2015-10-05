package common.msg;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Request implements Serializable {

    private static final long serialVersionUID = -1384590467857491672L;
    
    private String path;
    private Instruction instruction;
    private List<String> data;
    
    /**
     * @param path The current path of the request.
     * @param instruction The instruction to perform.
     * @param data The extra data for the instruction.
     */
    public Request(final Path path, final Instruction instruction, final List<? extends String> data) {
        this.path = path.toString();
        this.instruction = instruction;
        this.data = new ArrayList<>(data);
    }
    
    /**
     * @return The current path.
     */
    public Path getPath() {
        return Paths.get(path);
    }
    
    /**
     * @param path The path to set it to.
     */
    public void setPath(final Path path) {
        this.path = path.toString();
    }
    
    /**
     * @return The current instruction.
     */
    public Instruction getInstruction() {
        return instruction;
    }
    
    /**
     * @param instruction The instruction to set it to.
     */
    public void setInstruction(final Instruction instruction) {
        this.instruction = instruction;
    }
    
    /**
     * @return The extra data in the request.
     */
    public List<String> getData() {
        return data;
    }
}
