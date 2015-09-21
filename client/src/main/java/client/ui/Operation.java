package client.ui;

public enum Operation {
    LS("List all files in the directory."),
    GET("Get the file."),
    PUT("Put the file."),
    CD("Change directories."),
    MKDIR("Create a directory."),
    PWD("Show the current directory path."),
    HELP("Show help instructions.");

    private final String desc;
    
    private Operation(final String desc) {
        this.desc = desc;
    }
    
    public String getDesc() {
        return desc;
    }
}
