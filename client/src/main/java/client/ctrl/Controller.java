package client.ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.Optional;

import client.ui.cmdline.Command;
import client.ui.cmdline.CommandLineError;
import client.ui.cmdline.CommandLineInterface;
import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.PathChange;

public class Controller implements AutoCloseable {

    private final CommandLineInterface ui;
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    
    private Path path;
    
    public Controller() throws UnknownHostException, IOException {
        this.ui = new CommandLineInterface();
        this.socket = new Socket("127.0.0.1", 8080);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.path = null;
    }
    
    public void run() throws ClassNotFoundException, IOException {
        
        path = ((PathChange) inputStream.readObject()).getPath();
        while (true) {

            Optional<Command> command;
            for (command = ui.getCommand(); !command.isPresent(); command = ui.getCommand()) {
                ui.showError(CommandLineError.UNRECOGNIZED_COMMAND);
            }

            switch (command.get().getOperation()) {
                case HELP:
                    ui.showHelp();
                    break;
                case PWD:
                    ui.showPath(path);
                    break;
                default:
                    send(command.get().toRequest(path), outputStream, inputStream);
                    break;
            }
        }
    }
    
    private void send(final Request request, final ObjectOutputStream outputStream, final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        
        outputStream.writeObject(request);
        switch (request.getInstruction()) {
            case CD:
                final PathChange change = (PathChange) inputStream.readObject();
                path = change.getPath();
                ui.showPath(path);
                break;
            case GET:
                // TODO
                break;
            case LS:
                final FileList fileList = (FileList) inputStream.readObject();
                ui.showFiles(fileList.getFiles());
                break;
            case MKDIR:
                // TODO
                break;
            case PUT:
                // TODO
                break;
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
        ui.close();
    }
}
