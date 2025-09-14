package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Graphical player adapter class, which implements all Player methods and makes the links between the
 * visual and technical part of the game
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer; // Associated graphical player instance
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue; // Blocking queue containing ticket choices
    private final BlockingQueue<SortedBag<Card>> cardsQueue; // Blocking queue containing card choices
    private final BlockingQueue<Route> routeQueue; // Blocking queue which contains the route to claim
    private final BlockingQueue<Integer> slotQueue; // Blocking queue which contains the slot from which to draw cards
    private final BlockingQueue<TurnKind> turnKindQueue; // Blocking queue which contains the turn kind

    private static final int CAPACITY = 1; // Represents the capacity of the queue

    /**
     * Graphical player adapter constructor
     */
    public GraphicalPlayerAdapter() {
        ticketsQueue = new ArrayBlockingQueue<>(CAPACITY);
        cardsQueue = new ArrayBlockingQueue<>(CAPACITY);
        routeQueue = new ArrayBlockingQueue<>(CAPACITY);
        slotQueue = new ArrayBlockingQueue<>(CAPACITY);
        turnKindQueue = new ArrayBlockingQueue<>(CAPACITY);
    }

    /**
     * Method which defines the initial players by creating a new GraphicalPlayer
     *
     * @param ownId       own player id
     * @param playerNames map with player id and player name string relationship
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames);
    }

    /**
     * Receive info method pushed to graphical output
     *
     * @param info information string
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Method to update the graphical player state
     *
     * @param newState new public game state
     * @param ownState new own player state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Method to let the player choose the tickets
     *
     * @param tickets ticket options
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsQueue::add));
    }

    /**
     * Method which blocks the main thread while the player chooses his tickets and returns them once the choice is made
     *
     * @return sorted bag of tickets
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method which blocks the main thread and returns the turn kind once the player has done an action
     *
     * @return corresponding turn kind
     */
    @Override
    public TurnKind nextTurn() {
        try {
            runLater(() -> graphicalPlayer.startTurn(
                    () -> turnKindQueue.add(TurnKind.DRAW_TICKETS),
                    (slot) -> {
                        turnKindQueue.add(TurnKind.DRAW_CARDS);
                        slotQueue.add(slot);
                    },
                    (route, cards) -> {
                        turnKindQueue.add(TurnKind.CLAIM_ROUTE);
                        routeQueue.add(route);
                        cardsQueue.add(cards);
                    }));
            return turnKindQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method which blocks the main thread while the player chooses tickets
     *
     * @param options ticket bag options
     * @return sorted bag of tickets chosen by the player
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        try {
            runLater(() -> graphicalPlayer.chooseTickets(options, ticketsQueue::add));
            return ticketsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method which blocks the main thread while the player chooses from which slot to draw. If the queue is empty
     * (the player draws his second card), the graphical player ensures that a second card handler is replenished
     *
     * @return integer with the draw slot, as chosen by the player
     */
    @Override
    public int drawSlot() {
        try {
            if (slotQueue.isEmpty()) {
                runLater(() -> graphicalPlayer.drawCard(slotQueue::add));
            }
            return slotQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2; // Return an invalid value as 0 (null) or -1 would be valid
        }
    }

    /**
     * Method which blocks the main thread until the player has chosen which route to claim
     *
     * @return the route the player has chosen
     */
    @Override
    public Route claimedRoute() {
        try {
            return routeQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method which blocks the main thread while the player chooses his initial claim cards
     *
     * @return a sorted bag with the chosen cards
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return cardsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method which blocks the main thread while the player chooses his additional cards
     *
     * @param options options given to the player
     * @return a sorted bag with the chosen cards
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        try {
            runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardsQueue::add));
            return cardsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
