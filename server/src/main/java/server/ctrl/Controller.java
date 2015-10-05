package server.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.GetStatus;
import common.msg.response.MakeDirectory;
import common.msg.response.PathChange;
import common.msg.response.PutStatus;
import common.tcp.FileTransfer;
import common.ui.Console;
import common.utils.Constants;

public class Controller {

    private final FileTransfer fileTransfer;

    public Controller() {
        fileTransfer = new FileTransfer();
    }

    public void run() {
        try (final ServerSocket serverSocket = new ServerSocket(Constants.PORT); final Console console = new Console()) {
            
            boolean run = true;
            while (run) {
                console.writeLine("Listening...");
                
                try (final Socket socket = serverSocket.accept();
                        final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

                    console.writeLine("Client Connected");
                    outputStream.writeObject(new PathChange(Paths.get(System.getProperty(Constants.USER_HOME))));

                    boolean connected = true;
                    while (connected) {
                        try {
                            final Request request = (Request) inputStream.readObject();

                            switch (request.getInstruction()) {
                            case CD:
                                outputStream.writeObject(changePath(request));
                                break;
                            case GET:
                                outputStream.writeObject(getFile(request, socket.getOutputStream(), outputStream, inputStream));
                                break;
                            case LS:
                                outputStream.writeObject(getFileList(request));
                                break;
                            case MKDIR:
                                outputStream.writeObject(makeDirectory(request));
                                break;
                            case PUT:
                                outputStream.writeObject(putFile(request, socket.getInputStream(), outputStream));
                                break;
                            case EXIT:
                                connected = false;
                                console.writeLine("Client Disconnected.");
                                break;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace(System.err);
                            connected = false;
                        }
                    }
                }
                catch (Exception e) {
                    run = false;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private PathChange changePath(final Request request) {

        final Path path = request.getPath();
        if (request.getData().isEmpty()) {
            return new PathChange(path);
        }

        final Path newPath = path.resolve(request.getData().get(0)).normalize();
        if (Files.isDirectory(newPath)) {
            return new PathChange(newPath);
        }
        return new PathChange(path);
    }

    private FileList getFileList(final Request request) {

        final Path path = request.getPath();
        final List<String> files = new ArrayList<>();
        try {
            for (final Path file : Files.newDirectoryStream(path)) {
                files.add(file.getFileName() + (Files.isDirectory(file) ? File.separator : ""));
            }
        }
        catch (IOException e) {
            return new FileList(Collections.<String> emptyList());
        }

        return new FileList(files);
    }

    private MakeDirectory makeDirectory(final Request request) {

        final Path path = request.getPath();

        if (request.getData().isEmpty()) {
            return new MakeDirectory(false);
        }

        try {
            Files.createDirectory(path.resolve(request.getData().get(0)));
            return new MakeDirectory(true);
        }
        catch (IOException e) {
            return new MakeDirectory(false);
        }
    }

    private PutStatus putFile(final Request request, InputStream inputStream, final ObjectOutputStream objectOutputStream) throws IOException {
        if (request.getData().isEmpty()) {
            return PutStatus.NO_PATH;
        }
        

        try (OutputStream outputStream = Files.newOutputStream(request.getPath().resolve(Paths.get(request.getData().get(0)).getFileName()), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) { 
            objectOutputStream.writeObject(PutStatus.SUCCESS);
            fileTransfer.receive(inputStream, outputStream);
        }
        catch (IOException e) {
            return PutStatus.FAIL;
        }
        return PutStatus.SUCCESS;
    }

    private GetStatus getFile(final Request request, final OutputStream outputStream, final ObjectOutputStream objectOutputStream, ObjectInputStream inputStream) throws ClassNotFoundException {
        if (request.getData().isEmpty()) {
            return GetStatus.NO_PATH;
        }

        try {
            objectOutputStream.writeObject(GetStatus.SUCCESS);
            switch ((GetStatus) inputStream.readObject()) {
                case SUCCESS:
                    fileTransfer.send(request.getPath().resolve(request.getData().get(0)), outputStream);
                    break;
                default:
                    return GetStatus.FAIL;
            }
        }
        catch (IOException e) {
            return GetStatus.FAIL;
        }
        return GetStatus.SUCCESS;
    }
}
