package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


/**
 * Class ServerMain represents the main program of tCHu server
 *
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class ServerMain extends Application {

    private static final int PORT = 5108; // number of the port


    // list of default names of the players
    private static final List<String> defaultNames = List.of("Ada", "Charles");


    /**
     * Main method just launch it's arguments
     *
     * @param args is the arguments given to the method main
     *             each elements of the array correspond to the name of a player
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method start launch the tCHu server
     *
     * @param primaryStage is the stage of the game
     * @throws Exception if there's a problem with the connexion
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //list of arguments of the method main
        List<String> argNames = getParameters().getRaw();

        Map<PlayerId, Player> mapOfPlayers = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> mapOfPlayerNames = new EnumMap<>(PlayerId.class);

        try (ServerSocket s0 = new ServerSocket(PORT)) {

            // wait for connexion of the client on the port
            Socket socket = s0.accept();

            for (PlayerId playerId : PlayerId.values()) {
                mapOfPlayers.put(playerId, playerId.ordinal() == 0 ? new GraphicalPlayerAdapter() : new RemotePlayerProxy(socket));
                mapOfPlayerNames.put(playerId, playerId.ordinal() < argNames.size() ? argNames.get(playerId.ordinal()) : defaultNames.get(playerId.ordinal()));
            }

            // launch the thread managing the game
            new Thread(() -> Game.play(
                    mapOfPlayers,
                    mapOfPlayerNames,
                    SortedBag.of(ChMap.tickets()),
                    new Random())
            ).start();
        }
    }
}
