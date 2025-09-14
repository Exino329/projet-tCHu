package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ch.epfl.tchu.Preconditions.checkArgument;

//
/**
 * Class PublicGameState represents the pubic state of the game
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public class PublicGameState {

    private final int ticketsCount; ///Ticket count
    private final PublicCardState cardState; //Current public card state
    private final PlayerId currentPlayerId; //Id of the current player
    private final Map<PlayerId, PublicPlayerState> playerState; //Map which maps each player id to its corresponding public player state
    private final PlayerId lastPlayer; //Id of the last player

    /**
     * Public game state constructor
     * @param ticketsCount amount of tickets
     * @param cardState current public card state
     * @param currentPlayerId current player id
     * @param playerState map which maps each player id to the corresponding public player state
     * @param lastPlayer last players' player id
     * @throws IllegalArgumentException if the card states' deck size is strictly negative, and if the number of key/entry relationships in the playerState map is different than two
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        checkArgument(  playerState.size() == PlayerId.COUNT);
        checkArgument(ticketsCount >= 0);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Objects.requireNonNull(Map.copyOf(playerState));
        this.lastPlayer = lastPlayer;
    }


    /**
     * Ticket count getter
     * @return int
     */
    public int ticketsCount(){
        return ticketsCount;
    }

    /**
     * Boolean method which returns if tickets can be drawn (<pre><=></pre> if the deck is not empty)
     * @return boolean
     */
    public boolean canDrawTickets(){
        return ticketsCount != 0;
    }

    /**
     * Public card state getter
     * @return PublicCardState
     */
    public PublicCardState cardState(){
        return cardState;
    }

    /**
     * Boolean method which returns if cards can be drawn (<pre><=></pre> if there are at least 5 cards in the deck and discard together)
     * @return boolean
     */
    public boolean canDrawCards(){
        return cardState.deckSize() + cardState.discardsSize() >= Constants.FACE_UP_CARDS_COUNT;
    }

    /**
     * Current player id getter
     * @return PlayerId
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * Public player state getter for the specified player id
     * @param playerId player id
     * @return PublicPlayerState
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * Public player state getter for the current player
     * @return PublicPlayerState
     */
    public PublicPlayerState currentPlayerState(){
        return playerState(currentPlayerId());
    }

    /**
     * Getter which returns all of the roads claimed by any player
     * @return <pre>List<Route></pre>
     */
    public List<Route> claimedRoutes(){
        List<Route> out = new ArrayList<>();
        for(PlayerId player : playerState.keySet())
        {
            out.addAll(playerState.get(player).routes());
        }
        return out;
    }

    /**
     * Last player id getter
     * @return PlayerId
     */
    public PlayerId lastPlayer(){
        return lastPlayer;
    }

}
