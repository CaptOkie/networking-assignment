package client.ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import client.ui.cmdline.Command;
import client.ui.cmdline.CommandLineError;
import client.ui.cmdline.CommandLineInterface;
import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.GetStatus;
import common.msg.response.MakeDirectory;
import common.msg.response.PathChange;
import common.msg.response.PutStatus;
import common.tcp.FileTransfer;
import common.utils.Constants;

public class Controller implements AutoCloseable {
    
    private final CommandLineInterface ui;
    private final FileTransfer fileTransfer;
    
    private Path path;
    private Path getDir;
    
    public Controller() {
        this.ui = new CommandLineInterface();        
        this.fileTransfer = new FileTransfer();        

        this.path = null;
        this.getDir = Paths.get(System.getProperty(Constants.USER_HOME));
    }
    
    public void run() {
        
        try (final Socket socket = new Socket(ui.getIPAddress(), Constants.PORT); final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            
            boolean connected = true;
            path = ((PathChange) inputStream.readObject()).getPath();
            ui.showPath(path);
            while (connected) {
                
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
                    case GETDIR:
                        if (!command.getData().isEmpty()) {
                            final Path change = getDir.resolve(command.getData().get(0));
                            if (Files.isDirectory(getDir)) {
                                getDir = change;
                            }
                        }
                        ui.showPath(getDir);
                        break;
                    case EXIT: //marking for closure then exiting, we flow into default because we need to send to server.
                        connected = false;
                    default:
                        send(command.toRequest(path), socket, outputStream, inputStream);
                        break;
                }
            }
        }
        catch (UnknownHostException e) {
            ui.showError(CommandLineError.INVALID_HOST);
        }
        catch (IOException | ClassNotFoundException e) {
            ui.showError(CommandLineError.FATAL_ERROR);
        }
    }
    
    private void send(final Request request, final Socket socket, final ObjectOutputStream outputStream, final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        
        outputStream.writeObject(request);
        switch (request.getInstruction()) {
            case CD:
                cd(inputStream);
                break;
            case GET:
                get(request, socket, inputStream);
                break;
            case LS:
                ls(inputStream);
                break;
            case MKDIR:
                mkdir(inputStream);
                break;
            case PUT:
                put(request, socket, inputStream);
                break;
            case EXIT:
                break;
			default:
			    throw new RuntimeException("Unrecognized instruction");
        }
    }

    private void cd(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final PathChange change = (PathChange) inputStream.readObject();
        path = change.getPath();
        ui.showPath(path);
    }
    
    private void get(final Request request, final Socket socket, final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        switch ((GetStatus) inputStream.readObject()) {
            case SUCCESS:
                if (!request.getData().isEmpty()) {
                    try {
                        fileTransfer.receive(getDir.resolve(Paths.get(request.getData().get(0))), socket.getInputStream());
                    }
                    catch (IOException e) {}
                }
                switch ((GetStatus) inputStream.readObject()) {
                    case NO_PATH:
                        ui.showError(CommandLineError.ARGUMENT_MISSING);
                        break;
                    case FAIL:
                        ui.showError(CommandLineError.DOWNLOAD_FAILED);
                    case SUCCESS:
                        break;
                }
                break;
            case FAIL:
                ui.showError(CommandLineError.DOWNLOAD_FAILED);
                break;
            case NO_PATH:
                ui.showError(CommandLineError.ARGUMENT_MISSING);
                break;
        }                
    }
    
    private void ls(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final FileList fileList = (FileList) inputStream.readObject();
        ui.showFiles(fileList.getFiles());
    }
    
    private void mkdir(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final MakeDirectory makeDirectory = (MakeDirectory) inputStream.readObject();
        if (!makeDirectory.isSuccess()) {
            ui.showError(CommandLineError.MAKE_DIR_FAILED);
        }
    }
    
    private void put(final Request request, final Socket socket, final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        switch ((PutStatus) inputStream.readObject()) {
            case FAIL:
                ui.showError(CommandLineError.UPLOAD_FAILED);
                break;
            case NO_PATH:
                ui.showError(CommandLineError.ARGUMENT_MISSING);
                break;
            case SUCCESS:
                if (!request.getData().isEmpty()) {
                    try {
                        fileTransfer.send(Paths.get(request.getData().get(0)), socket.getOutputStream());
                    }
                    catch (IOException e) {
                    }
                }
                switch((PutStatus) inputStream.readObject()) {
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
        ui.close();
    }
}
