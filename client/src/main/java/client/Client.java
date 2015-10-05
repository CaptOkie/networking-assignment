package client;

import java.io.IOException;

import client.ctrl.Controller;

public class Client {

    public static void main(final String[] args) throws IOException {
        try (final Controller controller = new Controller()) {
            controller.run();
        }
    }
}
