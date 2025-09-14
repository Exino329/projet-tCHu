package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Class Serde <T> creates all the Serdes that will be used for the interaction between the client and the server
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Serdes<T> {

    private final static String COMMA_SEPARATOR = ",";
    private final static String SEMICOLON_SEPARATOR = ";";
    private final static String COLON_SEPARATOR = ":";
    private final static String EMPTY_STRING = "";

    /**
     * Anonymous class for serializing / deserializing integers
     */
    public final static Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     * Anonymous class for serializing / deserializing strings
     */
    public final static Serde<String> STRING_SERDE = Serde.of(
            i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            i -> new String(Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
    );

    /**
     * Anonymous class for serializing / deserializing player ids
     */
    public final static Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Anonymous class for serializing / deserializing turn kind
     */
    public final static Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Anonymous class for serializing / deserializing a card
     */
    public final static Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    /**
     * Anonymous class for serializing / deserializing a route
     */
    public final static Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    /**
     * Anonymous class for serializing / deserializing a ticket
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Anonymous class for serializing / deserializing a list of strings
     */
    public final static Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE, COMMA_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing a list of cards
     */
    public final static Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, COMMA_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing a list of routes
     */
    public final static Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, COMMA_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing a sorted bag of cards
     */
    public final static Serde<SortedBag<Card>> CARD_BAG_SERDE = Serde.bagOf(CARD_SERDE, COMMA_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing a sorted bag of tickets
     */
    public final static Serde<SortedBag<Ticket>> TICKET_BAG_SERDE = Serde.bagOf(TICKET_SERDE, COMMA_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing a list of bags of cards
     */
    public final static Serde<List<SortedBag<Card>>> CARD_BAG_LIST_SERDE = Serde.listOf(CARD_BAG_SERDE, SEMICOLON_SEPARATOR);

    /**
     * Anonymous class for serializing / deserializing public card state
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<>() {
        /**
         * Method which serializes a public card state
         *
         * @param type is the PublicCardState type to serialize
         * @return the serialized string of the type PublicCardState with the separator ; between each element
         */
        @Override
        public String serialize(PublicCardState type) {
            List<String> serializedList = List.of(
                    CARD_LIST_SERDE.serialize(type.faceUpCards()),
                    INTEGER_SERDE.serialize(type.deckSize()),
                    INTEGER_SERDE.serialize(type.discardsSize()));
            return String.join(SEMICOLON_SEPARATOR, serializedList);
        }

        /**
         * Method which deserializes a public card state
         *
         * @param serializedText is the serialized text of the type PublicCardState
         * @return the deserialized string (deserialized type : PublicCardState)
         */
        @Override
        public PublicCardState deserialize(String serializedText) {

            // remove the separator and store each serialized element in a string array
            String[] stringWithoutSeparator = serializedText.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);

            return new PublicCardState(
                    CARD_LIST_SERDE.deserialize(stringWithoutSeparator[0]), // List<Card> faceUpCards
                    INTEGER_SERDE.deserialize(stringWithoutSeparator[1]), // int deckSize
                    INTEGER_SERDE.deserialize(stringWithoutSeparator[2])); // int discardsSize
        }
    };

    /**
     * Anonymous class for serializing / deserializing public player state
     */
    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<>() {
        /**
         * Method which serializes a public player state
         *
         * @param type is the PublicPlayerState type to serialize
         * @return the serialized string of the type PublicPlayerState with the separator ; between each element
         */
        @Override
        public String serialize(PublicPlayerState type) {
            List<String> serializedList = List.of(
                    INTEGER_SERDE.serialize(type.ticketCount()),
                    INTEGER_SERDE.serialize(type.cardCount()),
                    ROUTE_LIST_SERDE.serialize(type.routes()));
            return String.join(SEMICOLON_SEPARATOR, serializedList);
        }

        /**
         * Method which deserializes a public player state
         *
         * @param serializedText is the serialized text of the type PublicPlayerState
         * @return the deserialized string (deserialized type : PublicPlayerState)
         */
        @Override
        public PublicPlayerState deserialize(String serializedText) {

            // remove the separator and store each serialized element in a string array
            String[] stringWithoutSeparator = serializedText.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);

            return new PublicPlayerState(
                    INTEGER_SERDE.deserialize(stringWithoutSeparator[0]), // int ticketCount
                    INTEGER_SERDE.deserialize(stringWithoutSeparator[1]), // int cardCount
                    ROUTE_LIST_SERDE.deserialize(stringWithoutSeparator[2])); // List<Route> routes
        }
    };

    /**
     * Anonymous class for serializing / deserializing player state
     */
    public final static Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<>() {
        /**
         * Method which serializes a player state
         *
         * @param type is the PlayerState type to serialize
         * @return the serialized string of the type PlayerState with the separator ; between each element
         */
        @Override
        public String serialize(PlayerState type) {
            List<String> serializedList = List.of(
                    TICKET_BAG_SERDE.serialize(type.tickets()),
                    CARD_BAG_SERDE.serialize(type.cards()),
                    ROUTE_LIST_SERDE.serialize(type.routes()));
            return String.join(SEMICOLON_SEPARATOR, serializedList);
        }

        /**
         * Method which deserializes a player state
         *
         * @param serializedText is the serialized text of the type PlayerState
         * @return the deserialized string (deserialized type : PlayerState)
         */
        @Override
        public PlayerState deserialize(String serializedText) {

            // remove the separator and store each serialized element in a string array
            String[] stringWithoutSeparator = serializedText.split(Pattern.quote(SEMICOLON_SEPARATOR), -1);

            return new PlayerState(
                    TICKET_BAG_SERDE.deserialize(stringWithoutSeparator[0]), // SortedBag<Ticket> tickets
                    CARD_BAG_SERDE.deserialize(stringWithoutSeparator[1]), // SortedBag<Card> cards
                    ROUTE_LIST_SERDE.deserialize(stringWithoutSeparator[2])); // List<Route> route
        }
    };

    /**
     * Anonymous class for serializing / deserializing public game state
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<>() {
        /**
         * Method which serializes a public game state
         *
         * @param type is the type PublicGameState to serialize
         * @return the serialized string of the type PublicGameState with the separator : between each element
         */
        @Override
        public String serialize(PublicGameState type) {

            List<String> serializedList = List.of(
                    INTEGER_SERDE.serialize(type.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(type.cardState()),
                    PLAYER_ID_SERDE.serialize(type.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(type.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(type.playerState(PlayerId.PLAYER_2)),
                    type.lastPlayer() == null
                            ? STRING_SERDE.serialize(EMPTY_STRING) //serialize an empty String if lastPlayer is null
                            : PLAYER_ID_SERDE.serialize(type.currentPlayerId().next()));
            return String.join(COLON_SEPARATOR, serializedList);
        }

        /**
         * Method which deserializes a public game state
         *
         * @param serializedText is the serialized text of the type PublicGameState
         * @return the deserialized PublicGameState
         */
        @Override
        public PublicGameState deserialize(String serializedText) {

            // remove the separator and store each serialized element in a string array
            String[] stringWithoutSeparator = serializedText.split(Pattern.quote(COLON_SEPARATOR), -1);

            Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new TreeMap<>();
            publicPlayerStateMap.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(stringWithoutSeparator[3])); // PublicPlayerState playerState(PLAYER_1)
            publicPlayerStateMap.put(PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(stringWithoutSeparator[4])); // PublicPlayerState playerState(PLAYER_2)

            return new PublicGameState(
                    INTEGER_SERDE.deserialize(stringWithoutSeparator[0]), // int ticketsCount
                    PUBLIC_CARD_STATE_SERDE.deserialize(stringWithoutSeparator[1]), // PublicCardState cardState
                    PLAYER_ID_SERDE.deserialize(stringWithoutSeparator[2]), // PlayerId currentPlayerId
                    publicPlayerStateMap, // Map<PlayerId,PublicPlayerState> playerState
                    stringWithoutSeparator[5].equals(EMPTY_STRING)
                            ? null // if the index 5 is an empty string, it means that the lastPlayer is null
                            : PLAYER_ID_SERDE.deserialize(stringWithoutSeparator[5]));  // PlayerId lastPlayer
        }
    };

    /**
     * Private default constructor to make this class not instantiable
     */
    private Serdes() {}

}

