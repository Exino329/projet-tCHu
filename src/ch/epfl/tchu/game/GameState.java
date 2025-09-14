package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;


import java.util.*;


import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.*;

/**
 * Class GameState represents the private state of the game
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> ticketsDeck; // Represents the deck of tickets

    private final Map<PlayerId,PlayerState> playerState; /*Represents the state of the Player1 or Player2
             (the state of a player is defined by the tickets, the cards and the routes that he owns)*/

    private final CardState privateCardState; /*Represents the private state of the cards that is inaccessible to players
             (the card's state is defined by the 5 faceUpCards, the deck and the discard)*/

    private static final int CARS_COUNT = 2; // lower limit of cars a player can have after which the last turn begins

    /**
     * Private constructor of GameState that is called with the method initial(...) (the constructor represents the state of the game)
     * @param cardState current card state
     * @param currentPlayerId Id of the player that
     * @param playerState ID of the player whose turn it is
     * @param lastPlayer ID of the player that is not actually playing
     * @param ticketsDeck amount of tickets
     */
    private GameState(CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer,Deck<Ticket> ticketsDeck) {
        super(ticketsDeck.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);
        this.ticketsDeck = Objects.requireNonNull(ticketsDeck);
        this.playerState = Map.copyOf(playerState);
        this.privateCardState = Objects.requireNonNull(cardState);
    }

    /**
     * Internal method to make a map with public player states
     * @param stateMap represents the state of the Player1 or Player2
     * @return the StateMap with public information of the state of Player1 and Player2
     */
    private static Map<PlayerId,PublicPlayerState> makePublic(Map<PlayerId,PlayerState> stateMap){
        return Map.copyOf(stateMap);
    }

    /**
     * Method to initialise a new game state to when the game begins
     * @param tickets represents the deck of tickets at the beginning a party of tCHu
     * @param rng is a random number generator
     * @return the initial state of the game (it is defined by a deck of tickets, the number of the tickets in the deck,
     * a deck of cards without the first 8 cards because they are distributed to each player (each player has 4 cards at the beginning),
     * a player1, a player2 with their respective state
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){

        Deck<Card> deckInit = Deck.of(Constants.ALL_CARDS,rng); //creation of the deck with all cards
        Deck<Ticket> ticketInit = Deck.of(tickets,rng); // creation of the draw shuffling the tickets given in argument
        PlayerId currentPlayerId = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT)); // we randomly select the current player

        // We create a new Map that link the playerId modifying it state (we add the 4 top cards of the deck to each player)
        Map<PlayerId,PlayerState> playerState = new HashMap<>();
        for(PlayerId playerId : PlayerId.ALL){
            playerState.put(playerId,new PlayerState(SortedBag.of(),deckInit.topCards(INITIAL_CARDS_COUNT),new ArrayList<>()));
            deckInit = deckInit.withoutTopCards(INITIAL_CARDS_COUNT);
        }

        return new GameState(
                CardState.of(deckInit),
                currentPlayerId,
                playerState,
                null,
                ticketInit);
    }

    /**
     * @param playerId is the Id of the player (1 or 2)
     * @return the state of one of the two player (with ID = 1 or 2)
     */
    @Override
    public PlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     *
     *
     * @return the state of the current player
     */
    @Override
    public PlayerState currentPlayerState(){
        return playerState(currentPlayerId());
    }

    /**
     * Getter which returns a given number of top tickets
     * @param count is the number of tickets at the top of the deck
     * @return the count ticket(s) at the top of the ticket's deck
     * @throws IllegalArgumentException if the argument count is less than 0 or higher than the size of the ticket's deck
     */
    public SortedBag<Ticket> topTickets(int count){
        checkArgument(count <= ticketsDeck.size() && count >= 0);
        return ticketsDeck.topCards(count);
    }

    /**
     * Method which returns a game state without a given number of top tickets
     * @param count is the number of tickets to remove at the top of the deck
     * @return  the state of the game without the count ticket(s) at the top of the ticket's deck
     * @throws IllegalArgumentException if the argument count is less than 0 or higher than the size of the ticket's deck
     */
    public GameState withoutTopTickets(int count){
        checkArgument(count <= ticketsCount() && count >= 0);
        return new GameState(
                privateCardState,
                currentPlayerId(),
                playerState,
                lastPlayer(),
                ticketsDeck.withoutTopCards(count));

    }

    /**
     * Method which returns the top card of the deck if it is not empty
     * @return the card that is at the top of deck
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public Card topCard(){
        checkArgument(!cardState().isDeckEmpty());
        return privateCardState.topDeckCard();
    }

    /**
     * Method which returns a new game state with one less top card
     * @return the state of the game without the first card of the card's deck
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public GameState withoutTopCard() {
        checkArgument(!cardState().isDeckEmpty());
        return new GameState(
                privateCardState.withoutTopDeckCard(),
                currentPlayerId(),
                playerState,
                lastPlayer(),
                ticketsDeck);
    }

    /**
     * Method which returns a new game state with the given cards added to the discard
     * @param discardedCards the cards to add to the discard
     * @return the state of the game by having added the discardedCards to the discard
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(
                privateCardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(),
                playerState,
                lastPlayer(),
                ticketsDeck);
    }

    /**
     * Method which refills the deck with the discard if it is empty
     * @param rng is a random number generator
     * @return a new state of the game recreating the card's deck with the discard,
     * if the card's deck was not empty, the method return the actual state of the game (this)
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState().isDeckEmpty()){
            return new GameState(
                    privateCardState.withDeckRecreatedFromDiscards(rng),
                    currentPlayerId(),
                    playerState,
                    lastPlayer(),
                    ticketsDeck);
        } else{
            return this;
        }
    }

    /**
     * Method which sets the tickets the players have chosen at the start of the game
     * @param playerId is the Id of the player (1 or 2)
     * @param chosenTickets is the tickets that the player has chosen
     * @return a state of the game adding the tickets chosen by the player 1 or 2 in his hand
     * @throws IllegalArgumentException if the player already own at least 1 billet
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        checkArgument(playerState.get(playerId).ticketCount() == 0);

        // Change the ID of the player to his new state (he has chosen the chosenTickets)
        Map<PlayerId,PlayerState> playerWithChosenTickets = new TreeMap<>(playerState);
        playerWithChosenTickets.replace(playerId,playerState.get(playerId).withAddedTickets(chosenTickets));

        return new GameState(
                privateCardState,
                currentPlayerId(),
                playerWithChosenTickets,
                lastPlayer(),
                ticketsDeck);
    }

    /**
     * Method which sets the tickets a player may have chosen if he decided to draw some during his turn
     * @param drawnTickets represents the tickets that the player has drawn
     * @param chosenTickets is the tickets that the player has chosen among the drawnTickets
     * @return a state of the game where the current player has drawn the tickets from the top of the deck, and has chosen to keep those contained in chosenTickets
     * @throws IllegalArgumentException if the set of the chosen tickets is not contained in the set of drawn tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        checkArgument(drawnTickets.contains(chosenTickets));

        // We remove the drawnTicket of the ticket deck's top
        Deck<Ticket> ticketsWithoutDrawnTickets = ticketsDeck.withoutTopCards(drawnTickets.size());


        // Creation of a new player state with adding the chosen tickets to the player (1 or 2)
        PlayerState playerStateWithChosenTickets = currentPlayerState().withAddedTickets(chosenTickets);

        // Creation of a new map associating the ID of the player to his new state (he has chosen the chosenTickets)
        Map<PlayerId,PlayerState> playerIdPlayerStateMap = new TreeMap<>();
        playerIdPlayerStateMap.put(currentPlayerId(),playerStateWithChosenTickets);
        playerIdPlayerStateMap.put(currentPlayerId().next(),playerState.get(currentPlayerId().next()));

        return new GameState(
                privateCardState,
                currentPlayerId(),
                playerIdPlayerStateMap,
                lastPlayer(),
                ticketsWithoutDrawnTickets);

    }

    /**
     * Method which returns a new game state with a given player which has drawn a card from a face up slot
     * @param slot is the face-up card to place in the player's hand
     * @return a state of the game where the player took the face-up card (at slot position) in his hand.
     *
     */
    public GameState withDrawnFaceUpCard(int slot){

        // Associate the current player a new state where he took the card at the slot position
        Map<PlayerId,PlayerState> playerIdPlayerStateMap= new TreeMap<>(playerState);
        playerIdPlayerStateMap.replace(currentPlayerId(),playerState.get(currentPlayerId()).withAddedCard(privateCardState.faceUpCard(slot)));

        return new GameState(
                privateCardState.withDrawnFaceUpCard(slot),
                currentPlayerId(),
                playerIdPlayerStateMap,
                lastPlayer(),
                ticketsDeck);

    }

    /**
     * Method which returns a new game state where a player has blindly drawn a card
     * @return a state of the game where the player has draw the card at the top of the deck
     */
    public GameState withBlindlyDrawnCard(){

        // Associate the current player a new state where he took the card at the top of the deck
        Map<PlayerId,PlayerState> playerIdPlayerStateMap = new TreeMap<>(playerState);
        playerIdPlayerStateMap.replace(currentPlayerId(),playerState.get(currentPlayerId()).withAddedCard(privateCardState.topDeckCard()));

        return new GameState(
                privateCardState.withoutTopDeckCard(),
                currentPlayerId(),
                playerIdPlayerStateMap,
                lastPlayer(),
                ticketsDeck);

    }

    /**
     * Method which returns a new game state where a player has claimed a route with the given cards
     * @param route is the route the play (1 or 2) took
     * @param cards used by the player to take the root
     * @return a state of the game where the player has claimed the route (first argument of the method) with given cards (second argument of the method)
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){

        // Associate the current player a new state where he has claimed the route with the cards
        Map<PlayerId,PlayerState> playerIdPlayerStateMap= new TreeMap<>(playerState);
        playerIdPlayerStateMap.replace(currentPlayerId(),playerState.get(currentPlayerId()).withClaimedRoute(route,cards));


        return new GameState(
                privateCardState.withMoreDiscardedCards(cards),
                currentPlayerId(),
                playerIdPlayerStateMap,
                lastPlayer(),
                ticketsDeck);

    }

    /**
     * Method which returns true if the last turn begins
     * @return true iff the identity of the last player is null and if the current player has at most 2 cars
     */
    public boolean lastTurnBegins(){
        return (lastPlayer() == null && playerState.get(currentPlayerId()).carCount() <= CARS_COUNT);
    }

    /**
     * Method which returns a new game state with the next players' player id as current player
     * @return a state of the game where the current player become the last player and the lastPlayer become the current player
     */
    public GameState forNextTurn(){
        PlayerId lastPlayer = lastPlayer();
        if (lastTurnBegins()){
            lastPlayer = currentPlayerId();
        }
        return new GameState(
                privateCardState,
                currentPlayerId().next(),
                playerState,
                lastPlayer,
                ticketsDeck);
    }
}
