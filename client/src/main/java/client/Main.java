package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import client.ui.Command;
import client.ui.CommandLineInterface;
import client.ui.Operation;
import common.net.msg.Instruction;
import common.net.msg.Message;

public class Main {

    public static void main(final String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

        final CommandLineInterface ui = new CommandLineInterface();

        System.out.println("Connecting");
        try (final Socket socket = new Socket("127.0.0.1", 8080);
                final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            while (true) {

                Optional<Command> command;
                for (command = ui.getCommand(); !command.isPresent(); command = ui.getCommand()) {}

                final Optional<Message> message = command2Message(command.get());
                if (message.isPresent()) {
                    outputStream.writeObject(message.get());
                }
            }
        }
    }

    private static Optional<Message> command2Message(final Command command) {

        final Optional<Instruction> instruction = operation2Instruction(command.getOperation());
        if (instruction.isPresent()) {
            return Optional.of(new Message(instruction.get(), command.getData()));
        }

        return Optional.empty();
    }

    private static Optional<Instruction> operation2Instruction(final Operation operation) {

        switch (operation) {
            case CD:
                return Optional.of(Instruction.CD);
            case GET: 
                return Optional.of(Instruction.GET);
            case LS:
                return Optional.of(Instruction.LS);
            case MKDIR:
                return Optional.of(Instruction.MKDIR);
            case PUT:
                return Optional.of(Instruction.PUT);
            default:
                return Optional.empty();
        }
    }
}
