package client;

import client.ctrl.Controller;
import common.ui.Console;

public class Client {

    public static void main(final String[] args) {
        
        try (final Console console = new Console()) {
            boolean run = true;
            while (run) {
                boolean err = false;
                try (final Controller controller = new Controller()) {
                    controller.run();
                }
                catch (Exception e) {
                    err = true;
                    console.writeLine(e.getLocalizedMessage());
                }
                finally {
                    if (err) {
                        do {
                            err = false;
                            String quit = null;
                            try {
                            	quit = console.readLine("Would you like to quit [Y/n]? ").toLowerCase();
                            }
                            catch (Exception e) {
    	    					System.out.println("Exception: " + e.getLocalizedMessage());
    	    					System.out.println("Quitting...");
    	    					run = false;
                            }
                            if (quit != null) {
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
                            }
                        } while (err);
                    }
                    else {
                        run = false;
                    }
                }
            }
        }
    }
}
