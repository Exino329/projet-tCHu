package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Interface Player represents a player of the game
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public interface Player {

    /**
     * Method which communicates to the player it's own ID and the name if the both players
     * @param ownId own player id
     * @param playerNames map with player id and player name string relationship
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Method which communicates an information to the player
     * @param info information string
     */
    void receiveInfo(String info);

    /**
     * Method used to inform the player about the new state of the game and it's own state
     * @param newState new public game state
     * @param ownState new own player state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Communicates to the player the 5 tickets that have been distributed to him
     * @param tickets ticket options
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Method which lets the player choose his initial tickets
     * @return sorted bag of tickets
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * determine the action that the player want to do during his turn
     * @return turn kind
     */
    TurnKind nextTurn();

    /**
     * Method which lets the player choose some tickets from the given options
     * @param options ticket bag options
     * @return ticket bag containing the players' choice
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Method which returns the draw slot the player has chosen
     * @return slot id int
     */
    int drawSlot();

    /**
     * Method which returns the players' chosen route they want to claim
     * @return chosen route
     */
    Route claimedRoute();

    /**
     * Method which returns the players' chosen cards to claim a given route
     * @return sorted bag of cards
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Method which lets the player choose from the given additional cards
     * @param options options given to the player
     * @return sorted bag of cards, chosen by the player
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Enumeration TurnKind represents the 3 different actions that a player can do during a turn
     * he can :
     *  - draw tickets (DRAW_TICKETS)
     *  - draw cards(DRAW_CARDS)
     *  - try to claim a route (CLAIM_ROUTE)
     */
    enum TurnKind{

        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        private static final TurnKind[] turnKindsArray = TurnKind.values();
        public static final List<TurnKind> ALL = List.of(turnKindsArray); // List of all turnKinds

    }

}
