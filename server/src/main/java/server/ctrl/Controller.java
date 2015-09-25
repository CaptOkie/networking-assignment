package server.ctrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.PathChange;

public class Controller implements AutoCloseable {

    private static final String USER_HOME = "user.home";

    public void run() throws IOException, ClassNotFoundException {

        try (final ServerSocket serverSocket = new ServerSocket(8080);
                final Socket socket = serverSocket.accept();
                final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            outputStream.writeObject(new PathChange(Paths.get(System.getProperty(USER_HOME))));
            while (true) {
                final Request request = (Request) inputStream.readObject();
                System.err.println("Instruction: " + request.getInstruction() + ", Data: " + request.getData());

                switch (request.getInstruction()) {
                    case CD:
                        outputStream.writeObject(changePath(request));
                        break;
                    case GET:
                        // TODO
                        break;
                    case LS:
                        outputStream.writeObject(getFileList(request));
                        break;
                    case MKDIR:
                        // TODO
                        break;
                    case PUT:
                        // TODO
                        break;
                }
            }
        }
    }

    private PathChange changePath(final Request request) throws IOException {

        final Path path = request.getPath();
        if (request.getData().isEmpty() || request.getData().get(0).equals(".")) {
            return new PathChange(path);
        }
        else if (request.getData().get(0).equals("..")) {
            final Path parent = path.getParent();
            if (parent == null) {
                return new PathChange(path);
            }
            return new PathChange(parent);
        }

        final String toResolve = Files.walk(path, 1).filter(file -> Files.isDirectory(file) && file.getFileName().toString().equals(request.getData().get(0)))
                .map(file -> file.getFileName().toString()).findFirst().orElse("");
        return new PathChange(path.resolve(toResolve));
    }

    private FileList getFileList(final Request request) throws IOException {
        
        final Path path = request.getPath();
        
        final List<String> files = Files.walk(path, 1).map(file -> {
            final String name = file.getFileName().toString();
            if (Files.isDirectory(file)) {
                return name + "/";
            }
            return name;
        }).collect(Collectors.toList());
        return new FileList(files);
    }
    
    @Override
    public void close() {}
}
