package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.net.msg.Request;

public class Server {

    private static final String USER_HOME = "user.home";
    
    public static void main(final String[] args) throws IOException, ClassNotFoundException { // TODO Maybe handle these exceptions
        
        System.out.println("Listening");
        try (final ServerSocket serverSocket = new ServerSocket(8080); final Socket socket = serverSocket.accept(); final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            
            outputStream.writeObject(System.getProperty(USER_HOME));
            while (true) {
                final Request message = (Request) inputStream.readObject();
                System.err.println("Instruction: " + message.getInstruction() + ", Data: " + message.getData());
            }
        }
    }
}
