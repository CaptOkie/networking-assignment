package client.ui.cmdline;

public enum CommandLineError {
    UNRECOGNIZED_COMMAND("Unrecognized Command");

    private final String msg;

    private CommandLineError(final String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
