package client.ui.cmdline;

public enum CommandLineError {
    UNRECOGNIZED_COMMAND("Unrecognized command"),
    MAKE_DIR_FAILED("Unable to create the directory"),
    ARGUMENT_MISSING("Argument required"),
    UPLOAD_FAILED("File upload failed");

    private final String msg;

    private CommandLineError(final String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
