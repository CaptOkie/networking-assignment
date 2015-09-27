package client.ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import client.ui.cmdline.Command;
import client.ui.cmdline.CommandLineError;
import client.ui.cmdline.CommandLineInterface;

import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.MakeDirectory;
import common.msg.response.PathChange;
import common.msg.response.PutStatus;
import common.tcp.FileTransfer;

public class Controller implements AutoCloseable {

    private final CommandLineInterface ui;
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final FileTransfer fileTransfer;
    
    private Path path;
    
    public Controller() throws UnknownHostException, IOException {
        this.ui = new CommandLineInterface();
        this.socket = new Socket("127.0.0.1", 8080);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.fileTransfer = new FileTransfer();        

        this.path = null;
    }
    
    public void run() throws ClassNotFoundException, IOException {
        
        path = ((PathChange) inputStream.readObject()).getPath();
        while (true) {

            Command command;
            for (command = ui.getCommand(); command == null; command = ui.getCommand()) {
                ui.showError(CommandLineError.UNRECOGNIZED_COMMAND);
            }

            switch (command.getOperation()) {
                case HELP:
                    ui.showHelp();
                    break;
                case PWD:
                    ui.showPath(path);
                    break;
                default:
                    send(command.toRequest(path), outputStream, inputStream);
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
                final MakeDirectory makeDirectory = (MakeDirectory) inputStream.readObject();
                if (!makeDirectory.isSuccess()) {
                    ui.showError(CommandLineError.MAKE_DIR_FAILED);
                }
                break;
            case PUT:
                if (!request.getData().isEmpty()) {
                    fileTransfer.send(Paths.get(request.getData().get(0)), socket.getOutputStream());
                }
                final PutStatus putStatus = (PutStatus) inputStream.readObject();
                switch(putStatus) {
                    case NO_PATH:
                        ui.showError(CommandLineError.ARGUMENT_MISSING);
                        break;
                    case FAIL:
                        ui.showError(CommandLineError.UPLOAD_FAILED);
                        break;
                    case SUCCESS:
                        break;
                }
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
