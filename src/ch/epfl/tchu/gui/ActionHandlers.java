package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Action handler interface
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public interface ActionHandlers {

    /**
     * Draw tickets handler
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Method called when the player chooses to draw tickets
         */
        void onDrawTickets();
    }

    /**
     * Card drawing handler
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Method called when the player draws a card from the given slot
         * @param slot the slot from which the player draws
         */
        void onDrawCard(int slot);
    }

    /**
     * Claim route handler
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Method called when the player chooses to claim a route
         * @param route the route he wants to claim
         * @param cards the cards he decides to use
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * Tickets chooser handler
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Method called when the player has chosen a given bag of tickets
         * @param tickets the tickets chosen by the player
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * Card chooser handler
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Method which is called when the player has chosen a given bag of cards
         * @param cards the cards chosen by the player
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}
