package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import common.net.msg.Instruction;
import common.net.msg.Message;

public class Main {

    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        
        System.out.println("Listening");
        try (final ServerSocket serverSocket = new ServerSocket(8080); final Socket socket = serverSocket.accept(); final ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            out.writeObject(new Message(Instruction.LS));
        }
    }
}
