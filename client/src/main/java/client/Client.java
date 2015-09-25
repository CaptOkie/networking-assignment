package client;

import java.io.IOException;
import java.net.UnknownHostException;

import client.ctrl.Controller;

public class Client {

    public static void main(final String[] args) throws UnknownHostException, IOException, ClassNotFoundException { // TODO Maybe handle these exceptions

        System.out.println("Connecting");
        try (final Controller controller = new Controller()) {
            controller.run();
        }
    }
}
