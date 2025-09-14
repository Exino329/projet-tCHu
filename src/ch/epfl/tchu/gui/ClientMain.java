package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Class ClientMain represents the main program of tCHu Client
 *
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class ClientMain extends Application {

    private static final int DEFAULT_PORT = 5108; // number of the default port (defined by an integer)

    private static final String DEFAULT_HOST = "localhost"; // name of the default host

    private static final int MAX_ARGUMENTS = 2; // maximum number of arguments


    /**
     * main method just launch it's arguments
     *
     * @param args is the arguments given to the method main
     *             - first element of the array args is the name of the host
     *             - second element of the array args is the port
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * method start launch the tCHu client
     *
     * @param primaryStage is the stage of the game
     */
    @Override
    public void start(Stage primaryStage) {

        // list of arguments of the method main (if the list is not empty, it's index 0 is the name of the host and index 1 is the number of the port)
        List<String> arguments = getParameters().getRaw();

        RemotePlayerClient playerClient = new RemotePlayerClient(
                new GraphicalPlayerAdapter(),
                arguments.isEmpty() ? DEFAULT_HOST : arguments.get(0), // index 0  of the list of arguments is the name of the host
                arguments.size() < MAX_ARGUMENTS ? DEFAULT_PORT : Integer.parseInt(arguments.get(1)) // index 1 of the list of arguments is the port
        );

        // launch the thread managing network access
        new Thread(playerClient::run).start();

    }
}
