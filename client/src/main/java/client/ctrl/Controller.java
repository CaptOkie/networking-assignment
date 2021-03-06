package client.ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import client.ui.cmdline.Command;
import client.ui.cmdline.CommandLineError;
import client.ui.cmdline.CommandLineInterface;
import client.ui.cmdline.YesOrNo;
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
    
    /**
     * Runs the client
     */
    public void run() {
        
        boolean run = true;
        while (run) {
        
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
                                if (Files.isDirectory(change)) {
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
                run = askQuestion("Would you like to try again?");
            }
            catch (IOException | ClassNotFoundException e) {
                ui.showError(CommandLineError.FATAL_ERROR);
                run = askQuestion("Would you like to restart?");
            }
        }
    }
    
    /**
     * Asks a yes or no question
     * @param question The question to ask
     * @return true if yes, false otherwise
     */
    private boolean askQuestion(final String question) {
        YesOrNo yesOrNo = null;
        for (yesOrNo = ui.getYesOrNo(question); yesOrNo == null; yesOrNo = ui.getYesOrNo(question)) {
            ui.showError(CommandLineError.INVALID_OPTION);
        }
        switch (yesOrNo) {
            case N:
            case NAH:
            case NO:
            case NOPE:
                return false;
            case Y:
            case YEAH:
            case YES:
            case YUP:
                return true;
            default:
                throw new RuntimeException("Unrecognized option");
        }
    }
    
    /**
     * Sends a message to the server
     * @param request request to send
     * @param socket socket on which to send
     * @param outputStream object stream to send messages
     * @param inputStream object stream to receive messages
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void send(final Request request, final Socket socket, final ObjectOutputStream outputStream, final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        
        outputStream.writeObject(request);
        switch (request.getInstruction()) {
            case CD:
                cd(inputStream);
                break;
            case GET:
                get(request, socket, inputStream, outputStream);
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

    /**
     * Processes the change directory request
     * @param inputStream object stream on which it receives messages
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void cd(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final PathChange change = (PathChange) inputStream.readObject();
        path = change.getPath();
        ui.showPath(path);
    }
    
    /**
     * Gets a file from the server
     * @param request the request that was sent
     * @param socket the current connection
     * @param inputStream object stream on which messages are received
     * @param outputStream object stream on which it sends messages
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void get(final Request request, final Socket socket, final ObjectInputStream inputStream, ObjectOutputStream outputStream) throws ClassNotFoundException, IOException {
        switch ((GetStatus) inputStream.readObject()) {
            case SUCCESS:
                if (!request.getData().isEmpty()) {
                    try (OutputStream file = Files.newOutputStream(getDir.resolve(Paths.get(request.getData().get(0))), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                        outputStream.writeObject(GetStatus.SUCCESS);
                        fileTransfer.receive(socket.getInputStream(), file);
                    }
                    catch (IOException e) {
                        outputStream.writeObject(GetStatus.FAIL);
                    }
                }
                else {
                    outputStream.writeObject(GetStatus.FAIL);
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
    
    /**
     * Process the list directory request
     * @param inputStream object stream on which messages are received
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void ls(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final FileList fileList = (FileList) inputStream.readObject();
        ui.showFiles(fileList.getFiles());
    }
    
    /**
     * Process the make directory request
     * @param inputStream object stream on which messages are received
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void mkdir(final ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        final MakeDirectory makeDirectory = (MakeDirectory) inputStream.readObject();
        if (!makeDirectory.isSuccess()) {
            ui.showError(CommandLineError.MAKE_DIR_FAILED);
        }
    }
    
    /**
     * Puts a file on the server
     * @param request the request from the client
     * @param socket the current connection
     * @param inputStream object stream on which messages are received
     * @throws ClassNotFoundException
     * @throws IOException
     */
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
