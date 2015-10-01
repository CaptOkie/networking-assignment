package client;

import java.io.IOException;
import java.net.UnknownHostException;

import client.ctrl.Controller;
import common.ui.Console;

public class Client {

	static boolean run;
	static boolean err;
	static Console console;
	
    public static void main(final String[] args) throws UnknownHostException, IOException, ClassNotFoundException { // TODO Maybe handle these exceptions
    	run = true;
    	console = new Console();
    	
    	while (run) {
    		err = false;
	    	try (final Controller controller = new Controller()) {
	            controller.run();
    			controller.close();
	        }
	    	catch (UnknownHostException e) {
	    		err = true;
	    		console.writeLine("UnknownHostException: " + e.getLocalizedMessage());
	    	}
	    	catch (IOException e) {
	    		err = true;
	    		console.writeLine("IOException: " + e.getLocalizedMessage());
	    	}
	    	catch (ClassNotFoundException e) {
	    		err = true;
	    		console.writeLine("ClassNotFoundException: " + e.getLocalizedMessage());
	    	}
	    	finally {
	    		if (err) {
	    			do {
	    				err = false;
	    				String quit = console.readLine("Would you like to quit [Y/n]? ").toLowerCase();
	    				switch (quit) {
	    				case "y":
	    				case "yes":
	    					run = false;
	    					break;
	    				case "n":
	    				case "no":
	    					break;
	    				default:
	    					err = true;
	    					console.writeLine("Invalid input.");
	    					break;
	    				}
	    			} while (err);
	    		}
	    	}
    	}
    	
    	console.close();
    }
}
