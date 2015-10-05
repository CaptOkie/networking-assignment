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
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;
    private final FileTransfer fileTransfer;
    
    private Path path;
    private Path getDir;
    
    public Controller() throws UnknownHostException, IOException {
        this.ui = new CommandLineInterface();
        
        String ipAddress;
        for (ipAddress = ui.getIPAddress(); ipAddress == null; ipAddress = ui.getIPAddress()) {
            ui.showError(CommandLineError.INVALID_IP_ADDRESS);
        }
        
        this.socket = new Socket(ipAddress, Constants.PORT);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.fileTransfer = new FileTransfer();        

        this.path = null;
        this.getDir = Paths.get(System.getProperty(Constants.USER_HOME));
    }
    
    public void run() throws ClassNotFoundException, IOException {
        boolean run = true;
        System.out.println("Connection Successful");
        path = ((PathChange) inputStream.readObject()).getPath();
        System.out.println("Current Directory: " +path.toString());
        while (run) {

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
                    run = false;
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
                if (!request.getData().isEmpty()) {
                    //sending the success or fail status of the transfer back to the server.
                    outputStream.writeObject(fileTransfer.receive(getDir.resolve(Paths.get(request.getData().get(0))), socket.getInputStream()));
                }
                final GetStatus getStatus = (GetStatus) inputStream.readObject();
                switch (getStatus) {
                    case NO_PATH:
                        ui.showError(CommandLineError.ARGUMENT_MISSING);
                        break;
                    case FAIL:
                        ui.showError(CommandLineError.DOWNLOAD_FAILED);
                    case SUCCESS:
                        break;
                }
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
			default:
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
