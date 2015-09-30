package client.ui;

import common.msg.Instruction;

public enum Operation {
    LS("List all files in the directory.", Instruction.LS),
    GET("Get the file.", Instruction.GET),
    PUT("Put the file.", Instruction.PUT),
    CD("Change directories.", Instruction.CD),
    MKDIR("Create a directory.", Instruction.MKDIR),
    GETDIR("Prints or sets the directory to download to.", null),
    PWD("Show the current directory path.", null),
    HELP("Show help instructions.", null),
    EXIT("Closes the Connection", Instruction.EXIT);

    private final String desc;
    private final Instruction instruction;
    
    private Operation(final String desc, final Instruction instruction) {
        this.desc = desc;
        this.instruction = instruction;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public Instruction getInstruction() {
        return instruction;
    }
}
