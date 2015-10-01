package server;

import java.io.IOException;

import common.ui.Console;
import server.ctrl.Controller;

public class Server {

	static Console console;

    public static void main(final String[] args) throws IOException, ClassNotFoundException { // TODO Maybe handle these exceptions
    	console = new Console();
    	
    	while (true) {
	        try (final Controller controller = new Controller()) {
	            console.writeLine("Listening");
	            controller.run();
	        }
	        catch (IOException e) {
	        	console.writeLine("IOException: " + e.getLocalizedMessage());
	        }
	        catch (ClassNotFoundException e) {
	        	console.writeLine("ClassNotFoundException: " + e.getLocalizedMessage());
	        }
    	}
    }
}
