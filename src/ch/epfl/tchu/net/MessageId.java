package ch.epfl.tchu.net;

/**
 * enumeration MessageId represents the types of messages that the server can send to clients
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS

}
