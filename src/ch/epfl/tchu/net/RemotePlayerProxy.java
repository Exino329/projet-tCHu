package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * The class RemotePlayerProxy represents the proxy of the remote players
 * It allows communication between the class  Game and the remote player's client by acting as an intermediary between them
 *
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class RemotePlayerProxy implements Player{


    private final BufferedWriter writer; // Writer head
    private final BufferedReader reader; // Reader head
    private final static String SPACE = " "; // Space character
    private final static String RETURN_SYMBOL = "\n"; // Return symbol


    /**
     * RemotePlayerProxy Constructor
     *
     * @param socket allows the server to wait for TCP connections on a given port,
     * @throws UncheckedIOException if there is a problem with the socket
     * @throws IllegalArgumentException if there is a problem with the socket
     */
    public RemotePlayerProxy(Socket socket) {
        try{
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * This method sends the serialized message given in argument to the Client of the remote player
     *
     * @param messageToSend is the message to send
     * @throws UncheckedIOException if there is a problem with sending messages
     * @throws IllegalArgumentException if the sent message's type is not recognised
     */
    private void sendMessage(String messageToSend){
        try {
            writer.write(messageToSend);
            writer.write(RETURN_SYMBOL);
            writer.flush();
        }
        catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Internal method which reads the last received line
     *
     * @return the specific part of the message to deserialize
     * @throws UncheckedIOException if there is a problem with receiving messages
     * @throws IllegalArgumentException if the received message's type is not recognised
     */
    private String receiveMessage() {
        try {
            String[] messageToReceive =  reader.readLine().split(Pattern.quote(SPACE), -1); // Split the string;
            return messageToReceive[0]; // the index 0 of the string will be the message to deserialize

        }catch (IOException e){
            throw new UncheckedIOException(e);
        }

    }

    /**
     * This method send the serialized message of the initialization of the player map
     *
     * @param ownId is the id of the player
     * @param playerNames is the name of both players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        // creates the list to serialize the name of the players
        List<String> listStringSerde = List.of(playerNames.get(PlayerId.PLAYER_1),playerNames.get(PlayerId.PLAYER_2));

        sendMessage(
                String.join(
                    SPACE,
                    INIT_PLAYERS.name(),
                    PLAYER_ID_SERDE.serialize(ownId),
                    STRING_LIST_SERDE.serialize(listStringSerde)));

    }

    /**
     * Send the serialized message of the received information by the player
     *
     * @param info is the information to communicate to the player
     */
    @Override
    public void receiveInfo(String info) {
        sendMessage(
                String.join(
                    SPACE,
                    RECEIVE_INFO.name(),
                    STRING_SERDE.serialize(info)));
    }

    /**
     * send the update state serialized message
     *
     * @param newState is the public game state
     * @param ownState ownState is the state of the player on which the method is called
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        sendMessage(
                String.join(
                    SPACE,
                    UPDATE_STATE.name(),
                    PUBLIC_GAME_STATE_SERDE.serialize(newState),
                    PLAYER_STATE_SERDE.serialize(ownState)));

    }

    /**
     * send the serialized message of the 5 initial tickets that are distributed to a player
     *
     * @param tickets are the 5 tickets distributed to a player at the beginning of the game
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        sendMessage(
                String.join(
                    SPACE,
                    SET_INITIAL_TICKETS.name(),
                    TICKET_BAG_SERDE.serialize(tickets)));
    }

    /**
     * Method which asks for the initial chosen tickets
     *
     * @return the tickets that the player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(CHOOSE_INITIAL_TICKETS.toString());
        return TICKET_BAG_SERDE.deserialize(receiveMessage());
    }

    /**
     * Method which asks for the turn kind
     *
     * @return the action that the player will do during the turn
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(NEXT_TURN.toString());
        return TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    /**
     * send the serialized message that the player choose additional tickets during a turn
     *
     * @param options is the tickets that the player has draw during the game
     * @return the tickets that the player decides to keep
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        sendMessage(
                String.join(
                    SPACE,
                    CHOOSE_TICKETS.name(),
                    TICKET_BAG_SERDE.serialize(options)));
        return TICKET_BAG_SERDE.deserialize(receiveMessage());

    }

    /**
     * Method which asks for the draw slot
     *
     * @return  an integer with a value in range from 0 to 4 (included) if the player has drawn one of the face up Cards
     * or returns -1 if the player draws a card from the deck
     */
    @Override
    public int drawSlot() {
        sendMessage(DRAW_SLOT.toString());
        return INTEGER_SERDE.deserialize(receiveMessage());
    }

    /**
     * Method which asks for the route to claim
     *
     * @return the route that the player is trying to claim
     */
    @Override
    public Route claimedRoute() {
        sendMessage(ROUTE.toString());
        return ROUTE_SERDE.deserialize(receiveMessage());
    }

    /**
     * Method which asks for the initial claim cards
     *
     * @return the cards that the player will initially use to claim the route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(CARDS.toString());
        return CARD_BAG_SERDE.deserialize(receiveMessage());
    }

    /**
     * send the serialized message that a player take additional cards to try to claim a tunnel
     *
     * @param options are the card that the option of cards that the player can use to claim a tunnel
     * @return the cards that the player will use to claim a tunnel
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        sendMessage(
            String.join(
                SPACE,
                CHOOSE_ADDITIONAL_CARDS.name(),
                CARD_BAG_LIST_SERDE.serialize(options)));
        return CARD_BAG_SERDE.deserialize(receiveMessage());
    }
}
