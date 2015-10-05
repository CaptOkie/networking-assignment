package client.ui.cmdline;

/**
 * All the erros that can be shown at the command line 
 *
 */
public enum CommandLineError {
    INVALID_HOST("Invalid host"),
    UNRECOGNIZED_COMMAND("Unrecognized command"),
    MAKE_DIR_FAILED("Unable to create the directory"),
    ARGUMENT_MISSING("Argument required"),
    DOWNLOAD_FAILED("File download failed"),
    UPLOAD_FAILED("File upload failed"),
    FATAL_ERROR("Fatal error occurred...shutting down"),
    INVALID_OPTION("Invalid option");

    private final String msg;

    private CommandLineError(final String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
