package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.PlayerId.*;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * Observable game state class, which regroups all the JavaFX properties of information available to a specific players.
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class ObservableGameState {

    private final PlayerId ownId; // The players' own ID

    private PublicGameState publicGameState; // Current public game state
    private PlayerState playerState; // Current player state of the given player

    private final IntegerProperty ticketsPercentage = new SimpleIntegerProperty(); // Percentage of remaining tickets
    private final IntegerProperty deckPercentage = new SimpleIntegerProperty(); // Percentage of remaining cards in the cards
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>(); // List of face up cards
    private final List<ObjectProperty<PlayerId>> routeOwners = new ArrayList<>(); // List of route owners. Routes are listed in order as given in ChMap

    private final Map<PlayerId, IntegerProperty> playerTickets = new TreeMap<>(); // Number of tickets each player has
    private final Map<PlayerId, IntegerProperty> playerCards = new TreeMap<>(); // Number of cards each player has
    private final Map<PlayerId, IntegerProperty> playerCars = new TreeMap<>(); // Number of cars each player has
    private final Map<PlayerId, IntegerProperty> playerPoints = new TreeMap<>(); // Amount of points each player has

    private final ObservableList<Ticket> ownTickets = FXCollections.observableArrayList(); // List of own tickets
    private final List<IntegerProperty> ownCardCounts = new ArrayList<>(); // Number of each type of card the player has
    private final List<BooleanProperty> claimableRoutes = new ArrayList<>(); // List of booleans for every route, if they are claimable or not

    private static final int PERCENT = 100; // represents 100 percent

    /**
     * Observable game state constructor. Generates a blank game state with all properties initialised to null. Note that
     * for routes and cards, these objects are stored in lists where the order is identical to the parent collections,
     * i.e. ChMap.routes() and Card.values()
     *
     * @param ownId Own player ID
     */
    public ObservableGameState(PlayerId ownId) {
        this.ownId = ownId; // Set own ID

        // Create all property instances, initialised at null
        for (int faceUpCardsSlot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.add(new SimpleObjectProperty<>());
        }

        for (Route route : ChMap.routes()) {
            routeOwners.add(new SimpleObjectProperty<>());
        }

        for (PlayerId id : PlayerId.values()) {
            playerTickets.put(id, new SimpleIntegerProperty());
            playerCards.put(id, new SimpleIntegerProperty());
            playerCars.put(id, new SimpleIntegerProperty());
            playerPoints.put(id, new SimpleIntegerProperty());
        }

        for (Card card : Card.values()) {
            ownCardCounts.add(new SimpleIntegerProperty());
        }

        for (Route route : ChMap.routes()) {
            claimableRoutes.add(new SimpleBooleanProperty());
        }
    }

    /**
     * Method which updates the observable game state to keep up with the flow of the game
     *
     * @param publicGameState public game state, accessible to all players
     * @param playerState     player state of the specific player
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        // Store the given states
        this.publicGameState = publicGameState;
        this.playerState = playerState;

        // Compute the remaining ticket and card percentages
        ticketsPercentage.set(publicGameState.ticketsCount() * PERCENT / ChMap.tickets().size());
        deckPercentage.set(publicGameState.cardState().deckSize() * PERCENT / Constants.TOTAL_CARDS_COUNT);

        // Update the face up cards
        for (int faceUpCardIndex : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(faceUpCardIndex).set(publicGameState.cardState().faceUpCard(faceUpCardIndex));
        }

        // Update the route owners
        for (Route currentRoute : ChMap.routes()) {
            if (publicGameState.playerState(PLAYER_1).routes().contains(currentRoute)) {
                routeOwners.get(ChMap.routes().indexOf(currentRoute)).set(PLAYER_1);
            }
            if (publicGameState.playerState(PLAYER_2).routes().contains(currentRoute)) {
                routeOwners.get(ChMap.routes().indexOf(currentRoute)).set(PLAYER_2);
            }
        }

        // Update all players' number of tickets, cards, cars and points
        for (PlayerId id : playerTickets.keySet()) {
            playerTickets.get(id).set(publicGameState.playerState(id).ticketCount());
            playerCards.get(id).set(publicGameState.playerState(id).cardCount());
            playerCars.get(id).set(publicGameState.playerState(id).carCount());
            playerPoints.get(id).set(publicGameState.playerState(id).claimPoints());
        }

        // Update own tickets
        ownTickets.setAll(playerState.tickets().toList());

        // Update own cards
        for (Card card : Card.values()) {
            ownCardCounts.get(card.ordinal()).set(playerState.cards().countOf(Card.values()[card.ordinal()]));
        }

        // Update claimable routes
        for (Route currentRoute : ChMap.routes()) {
            boolean hasCards = playerState.canClaimRoute(currentRoute); // If the player has the necessary cards / cars

            // Generate the list of routes which are in the same group (for double, triple, etc. routes) by checking
            // if they have the same stations
            List<Route> otherRoutesInGroup = ChMap.routes().stream().filter((route) ->
                    (route.station1() == currentRoute.station1() && route.station2() == currentRoute.station2())
                            // Redundant check if the double route has been initialised backwards
                            || (route.station1() == currentRoute.station2() && route.station2() == currentRoute.station1()))
                    .collect(Collectors.toList());

            boolean alreadyClaimedInGroup = false; // If another player has already claimed any of the routes
            for (Route r : otherRoutesInGroup) {
                alreadyClaimedInGroup = publicGameState.claimedRoutes().contains(r);
                if (alreadyClaimedInGroup) break;
            }

            claimableRoutes.get(ChMap.routes().indexOf(currentRoute)).set(hasCards && !alreadyClaimedInGroup);
        }
    }

    /**
     * Tickets percentage getter
     *
     * @return Read-Only Integer Property
     */
    public ReadOnlyIntegerProperty getTicketsPercentage() {
        return ticketsPercentage;
    }

    /**
     * Cards percentage getter
     *
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getDeckPercentage() {
        return deckPercentage;
    }

    /**
     * Face up card getter
     *
     * @param slot integer for the slot number
     * @return Read-only object property (object of type Card)
     * @throws IllegalArgumentException if the given slot is not valid
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCards(int slot) {
        checkArgument(Constants.FACE_UP_CARD_SLOTS.contains(slot));
        return faceUpCards.get(slot);
    }

    /**
     * Route owner getter
     *
     * @param route specified route
     * @return Read-only object property (object of type PlayerId)
     * @throws IllegalArgumentException if the route is not in the routes given in ChMap
     */
    public ReadOnlyObjectProperty<PlayerId> getRouteOwner(Route route) {
        checkArgument(ChMap.routes().contains(route));
        int index = ChMap.routes().indexOf(route);
        return routeOwners.get(index);
    }

    /**
     * Tickets count getter for a specific player
     *
     * @param id player id
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getPlayerTicketsCount(PlayerId id) {
        return playerTickets.get(id);
    }

    /**
     * Cards count getter for a specific player
     *
     * @param id player id
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getPlayerCardsCount(PlayerId id) {
        return playerCards.get(id);
    }

    /**
     * Car count getter for a specific player
     *
     * @param id player id
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getPlayerCarsCount(PlayerId id) {
        return playerCars.get(id);
    }

    /**
     * Points getter for a specific player
     *
     * @param id player id
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getPlayerPoints(PlayerId id) {
        return playerPoints.get(id);
    }

    /**
     * Own ticket list getter
     *
     * @return Read-only list property (containing objects of type Ticket)
     */
    public ObservableList<Ticket> getOwnTickets() {
        return unmodifiableObservableList(ownTickets);
    }

    /**
     * Own card count getter
     *
     * @param card Specified card type
     * @return Read-only integer property
     */
    public ReadOnlyIntegerProperty getOwnCardCount(Card card) {
        int index = Arrays.asList(Card.values()).indexOf(card);
        return ownCardCounts.get(index);
    }

    /**
     * Boolean getter if a specific route is claimable by the current player or not
     *
     * @param route specified route
     * @return Read-only boolean property
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        int index = ChMap.routes().indexOf(route);
        return claimableRoutes.get(index);
    }

    /**
     * Method which returns true if tickets can be drawn
     *
     * @return boolean
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * Method which returns true if cards can be drawn
     *
     * @return boolean
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    /**
     * Method which gives the possible claim cards the current player can use to claim a route
     *
     * @param route specified route
     * @return <pre>List<SortedBag<Card>></pre>
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

}
