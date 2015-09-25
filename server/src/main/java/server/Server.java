package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;

import common.msg.Request;
import common.msg.response.FileList;
import common.msg.response.PathChange;

public class Server {

    private static final String USER_HOME = "user.home";

    public static void main(final String[] args) throws IOException, ClassNotFoundException { // TODO Maybe handle these exceptions

        System.out.println("Listening");
        try (final ServerSocket serverSocket = new ServerSocket(8080);
                final Socket socket = serverSocket.accept();
                final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            outputStream.writeObject(new PathChange(Paths.get(System.getProperty(USER_HOME))));
            while (true) {
                final Request message = (Request) inputStream.readObject();
                System.err.println("Instruction: " + message.getInstruction() + ", Data: " + message.getData());
                
                switch (message.getInstruction()) {
                    case CD:
                        outputStream.writeObject(new PathChange(message.getPath().resolve("temp"))); // TODO
                        break;
                    case GET:
                        // TODO
                        break;
                    case LS:
                        outputStream.writeObject(new FileList(Arrays.asList(".", "..", "Blah", "Test"))); // TODO
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
}
