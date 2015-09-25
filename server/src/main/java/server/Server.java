package server;

import java.io.IOException;

import server.ctrl.Controller;

public class Server {

    public static void main(final String[] args) throws IOException, ClassNotFoundException { // TODO Maybe handle these exceptions

        System.out.println("Listening");
        try (final Controller controller = new Controller()) {
            controller.run();
        }
    }
}
