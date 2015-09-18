package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import common.net.msg.Message;

public class Main {

    public static void main(final String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        
        System.out.println("Connecting");
        try (final Socket socket = new Socket("127.0.0.1", 8080); final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            
            final Message message = (Message) inputStream.readObject();
            System.out.println(message.getInstruction());
        }
    }
}
