package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Player client for player-proxy interactions
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class RemotePlayerClient {

    private final Player player; // Client player object
    private final BufferedReader reader; // Reader head
    private final BufferedWriter writer; // Writer head
    private final Socket socket; // Socket
    private final static String SPACE = " ";
    private final static String RETURN_SYMBOL = "\n";

    /**
     * Remote player client constructor
     *
     * @param player Local player instance
     * @param name Name of the remote socket
     * @param port Port of the remote socket
     * @throws UncheckedIOException for IO errors
     */
    public RemotePlayerClient(Player player, String name, int port)
    {
        this.player = player;
        try
        {
            socket = new Socket(name, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Main run method. This listens for the proxy's calls, executes corresponding commands on the local player instance,
     * and sends back information and player choices
     *
     * @throws UncheckedIOException if there is a problem with sending/receiving messages
     * @throws IllegalArgumentException if the received message's type is not recognised
     */
    public void run()
    {
        try {
            // Keep running / receiving messages. Exits on null input through break keyword
            while(socket.isConnected()) {
                String input = reader.readLine(); // Read the latest message

                // If the input is null, break out of the loop. This indicates that the server has cut the connection
                if(Objects.isNull(input))
                {
                    break;
                }

                String[] stringWithoutSeparator = input.split(Pattern.quote(SPACE), -1); // Split the string

                // Act according to the id received (this id will be index 0 of the array)
                switch(stringWithoutSeparator[0])
                {
                    case "INIT_PLAYERS":
                        PlayerId ownId = PLAYER_ID_SERDE.deserialize(stringWithoutSeparator[1]);
                        List<String> playerNames = STRING_LIST_SERDE.deserialize(stringWithoutSeparator[2]);
                        Map<PlayerId, String> playerMap = Map.of(PlayerId.PLAYER_1, playerNames.get(0),PlayerId.PLAYER_2, playerNames.get(1));
                        player.initPlayers(ownId, playerMap);
                        break;

                    case "RECEIVE_INFO":
                        String info = STRING_SERDE.deserialize(stringWithoutSeparator[1]);
                        player.receiveInfo(info);
                        break;

                    case "UPDATE_STATE":
                        PublicGameState newGameState = PUBLIC_GAME_STATE_SERDE.deserialize(stringWithoutSeparator[1]);
                        PlayerState newOwnState = PLAYER_STATE_SERDE.deserialize(stringWithoutSeparator[2]);
                        player.updateState(newGameState, newOwnState);
                        break;

                    case "SET_INITIAL_TICKETS":
                        SortedBag<Ticket> tickets = TICKET_BAG_SERDE.deserialize(stringWithoutSeparator[1]);
                        player.setInitialTicketChoice(tickets);
                        break;

                    case "CHOOSE_INITIAL_TICKETS":
                        SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();
                        String out = String.join(SPACE, TICKET_BAG_SERDE.serialize(chosenTickets));
                        flushMessage(out);
                        break;

                    case "NEXT_TURN":
                        Player.TurnKind turn = player.nextTurn();
                        out = String.join(SPACE, TURN_KIND_SERDE.serialize(turn));
                        flushMessage(out);
                        break;

                    case "CHOOSE_TICKETS":
                        SortedBag<Ticket> givenTickets = TICKET_BAG_SERDE.deserialize(stringWithoutSeparator[1]);

                        chosenTickets = player.chooseTickets(givenTickets);
                        out = String.join(SPACE, TICKET_BAG_SERDE.serialize(chosenTickets));
                        flushMessage(out);
                        break;

                    case "DRAW_SLOT":
                        int chosenSlot = player.drawSlot();
                        out = String.join(SPACE, INTEGER_SERDE.serialize(chosenSlot));
                        flushMessage(out);
                        break;

                    case "ROUTE":
                        Route routeToSend = player.claimedRoute();
                        out = String.join(SPACE, ROUTE_SERDE.serialize(routeToSend));
                        flushMessage(out);
                        break;

                    case "CARDS":
                        SortedBag<Card> cardsToSend = player.initialClaimCards();
                        out = String.join(SPACE, CARD_BAG_SERDE.serialize(cardsToSend));
                        flushMessage(out);
                        break;

                    case "CHOOSE_ADDITIONAL_CARDS":
                        List<SortedBag<Card>> possibleAdditionalCards = CARD_BAG_LIST_SERDE.deserialize(stringWithoutSeparator[1]);
                        cardsToSend = SortedBag.of(player.chooseAdditionalCards(possibleAdditionalCards));
                        out = String.join(SPACE, CARD_BAG_SERDE.serialize(cardsToSend));
                        flushMessage(out);
                        break;
                }

            }

        }
        catch(IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Internal method that sends a given message
     *
     * @param out is the message to flush
     * @throws IOException if there is a problem with the message to send
     */
    private void flushMessage(String out) throws IOException
    {
        writer.write(out);
        writer.write(RETURN_SYMBOL);
        writer.flush();
    }
}
