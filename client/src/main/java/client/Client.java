package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import client.ui.cmdline.Command;
import client.ui.cmdline.CommandLineError;
import client.ui.cmdline.CommandLineInterface;
import common.msg.Request;

public class Client {

    public static void main(final String[] args) throws UnknownHostException, IOException, ClassNotFoundException { // TODO Maybe handle these exceptions

        final CommandLineInterface ui = new CommandLineInterface();

        System.out.println("Connecting");
        try (final Socket socket = new Socket("127.0.0.1", 8080);
                final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            Path path = Paths.get((String) inputStream.readObject());
            while (true) {

                Optional<Command> command;
                for (command = ui.getCommand(); !command.isPresent(); command = ui.getCommand()) {
                    ui.showError(CommandLineError.UNRECOGNIZED_COMMAND);
                }

                switch (command.get().getOperation()) {
                    case HELP:
                        ui.showHelp();
                        break;
                    case PWD:
                        ui.showPath(path);
                        break;
                    default:
                        send(command.get().toRequest(path), outputStream);
                        break;
                }
            }
        }
    }
    
    private static void send(final Request request, final ObjectOutputStream outputStream) throws IOException {
        
        outputStream.writeObject(request);
        switch (request.getInstruction()) {
            case CD:
                break;
            case GET:
                break;
            case LS:
                break;
            case MKDIR:
                break;
            case PUT:
                break;
        }
    }
}
