package ch.epfl.tchu.game;

import java.util.List;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.INITIAL_CAR_COUNT;

/**
 * Class PublicPlayerState represents the public state of a player (i.e. his number of cards and tickets and the routes that he has claimed)
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public class PublicPlayerState {

    private final int ticketCount; //Number of tickets the player has
    private final int cardCount; //Number of cards the player has
    private final List<Route> routes; //Routes claimed by the player
    private final int carCount; //Number of car cards the player has
    private final int claimPoints; //Number of points the player has through claimed routes

    /**
     * PublicPlayerState constructor
     * @param ticketCount amount of tickets the player has
     * @param cardCount amount of cards the player has
     * @param routes routes the player has claimed
     * @throws IllegalArgumentException if cardCount or ticketCount are negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes)
    {
        checkArgument(cardCount >= 0);
        checkArgument(ticketCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        claimPoints = computeClaimPoints();
        carCount = computeCarCount();
    }

    /**
     * Ticket count getter
     * @return int
     */
    public int ticketCount()
    {
        return ticketCount;
    }

    /**
     * Card count getter
     * @return int
     */
    public int cardCount()
    {
        return cardCount;
    }

    /**
     * Claimed routes getter
     * @return <pre>List<Route></pre>
     */
    public List<Route> routes()
    {
        return routes;
    }

    /**
     * Car count getter
     * @return int
     */
    public int carCount()
    {
        return carCount;
    }

    /**
     * Total claim points getter
     * @return int
     */
    public int claimPoints()
    {
        return claimPoints;
    }

    /**
     * Internal method which computes the total claim points from the routes claimed by the player
     * @return int
     */
    private int computeClaimPoints()
    {
        int sum = 0;
        for(Route route : routes)
        {
            sum += route.claimPoints();
        }
        return sum;
    }

    /**
     * Internal method to compute the car count
     * @return int
     */
    private int computeCarCount()
    {
        int tempCarCount = 0;
        for(Route route : routes)
        {
            tempCarCount += route.length();
        }
        return INITIAL_CAR_COUNT - tempCarCount;
    }
}
