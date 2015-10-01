package server.ctrl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.GetStatus;
import common.msg.response.MakeDirectory;
import common.msg.response.PathChange;
import common.msg.response.PutStatus;
import common.tcp.FileTransfer;
import common.utils.Constants;

public class Controller implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final FileTransfer fileTransfer;

    public Controller() throws IOException {
        serverSocket = new ServerSocket(Constants.port);
        fileTransfer = new FileTransfer();
    }

    public void run() throws IOException, ClassNotFoundException {

        boolean run = true;
        while (run) {
            try (final Socket socket = serverSocket.accept();
                    final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                
                System.out.println("Client Connected");
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
                                outputStream.writeObject(getFile(request, socket.getOutputStream()));
                                break;
                            case LS:
                                outputStream.writeObject(getFileList(request));
                                break;
                            case MKDIR:
                                outputStream.writeObject(makeDirectory(request));
                                break;
                            case PUT:
                                outputStream.writeObject(putFile(request, socket.getInputStream()));
                                break;
                            case EXIT:
                                connected = false;
                                System.out.println("Client Disconnected.");
                                break;
                        }
                    }
                    catch (Exception e) {
                        connected = false;
                        System.out.println("Client Disconnected.");
                    }
                }
            }
            catch (Exception e) {
                run = false;
            }
        }
    }

    private PathChange changePath(final Request request) throws IOException {

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

    private FileList getFileList(final Request request) throws IOException {

        final Path path = request.getPath();
        final List<String> files = new ArrayList<>();
        for (final Path file : Files.newDirectoryStream(path)) {
            files.add(file.getFileName() + (Files.isDirectory(file) ? File.separator : ""));
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

    private PutStatus putFile(final Request request, InputStream inputStream) throws UnsupportedEncodingException, ClassNotFoundException, IOException {
        if (request.getData().isEmpty()) {
            return PutStatus.NO_PATH;
        }

        fileTransfer.receive(request.getPath().resolve(Paths.get(request.getData().get(0)).getFileName()), inputStream);
        return PutStatus.SUCCESS;
    }

    private GetStatus getFile(final Request request, final OutputStream outputStream) throws IOException {
        if (request.getData().isEmpty()) {
            return GetStatus.NO_PATH;
        }

        fileTransfer.send(request.getPath().resolve(request.getData().get(0)), outputStream);
        return GetStatus.SUCCESS;
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
