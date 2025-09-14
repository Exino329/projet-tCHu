package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.ADDITIONAL_TUNNEL_CARDS;
import static ch.epfl.tchu.game.Constants.ROUTE_CLAIM_POINTS;

/**
 * Class is the characterisation of the link between two stations
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class Route {

    //Level of the route: if it is a normal route or a tunnel
    public enum Level {
        UNDERGROUND,
        OVERGROUND
    }

    private final String id; //unique ID associated to the route
    private final Station station1; //first station
    private final Station station2; //second station
    private final int length; //length of the route
    private final Level level; //level of the route
    private final Color color; //color of the route

    /**
     * Route constructor
     * @param id unique ID associated to the route
     * @param station1 first station
     * @param station2 second station
     * @param length length of the route
     * @param level level of the route (overground & underground / tunnel)
     * @param color color of the route (<code>null</code> for neutral color)
     * @throws IllegalArgumentException if station1 has the same name as station2 or if the length is out of
     * MIN_ROUTE_LENGTH and MAX_ROUTE_LENGTH
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color)
    {
        checkArgument(!station1.equals(station2));
        checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * Route id getter
     * @return String
     */
    public String id()
    {
        return this.id;
    }

    /**
     * Getter for the routes' first station
     * @return Station
     */
    public Station station1()
    {
        return this.station1;
    }

    /**
     * Getter for the routes' second station
     * @return Station
     */
    public Station station2()
    {
        return this.station2;
    }

    /**
     * Route length getter
     * @return int
     */
    public int length()
    {
        return this.length;
    }

    /**
     * Route level getter
     * @return Level
     */
    public Level level()
    {
        return this.level;
    }

    /**
     * Route color getter
     * @return Color
     */
    public Color color()
    {
        return this.color;
    }

    /**
     * Returns a list containing the stations which this route connects.
     * @return <pre>List<Station></pre>
     */
    public List<Station> stations()
    {
        return List.of(this.station1, this.station2);
    }

    /**
     * Method which returns the station opposite of the given station. throws <code>IllegalArgumentException</code>
     * if the given station is neither of those corresponding to the route.
     * @param station station from which to find the opposite
     * @return Station
     */
    public Station stationOpposite(Station station)
    {
        if(!station.equals(station1) && !station.equals(station2))
        {
            throw new IllegalArgumentException();
        }

        return (station.equals(station1)) ? station2 : station1;
    }

    /**
     * Method which returns a list of sorted bags with all combinations of cards needed to take over a particular
     * route. These combinations depend on the color of the route and are become a lot if the color is neutral. The
     * combinations range from all colored car cards up to all locomotive cards and any combination in between. The
     * amount of different combinations can be computed by <code>C = LENGTH + 1</code> if the routes' color is
     * specified and <code>C = 8 * LENGTH + 1</code> if it is neutral.
     * @return <pre>List<SortedBag<Card>></pre>
     */
    public List<SortedBag<Card>> possibleClaimCards(){

        List<SortedBag<Card>> out = new ArrayList<>(); //Create the output list

        List<Color> colors = (color == null) ? Color.ALL : List.of(color);

        if(level == Level.UNDERGROUND)
        {
            // The order is important here, as the list of bags needs to be sorted by number of locomotive cards first,
            // then by color.
            for(int i = 0; i < length; ++i)
            {
                for(Color c : colors)
                {
                    out.add(generateBag(length, c, i));
                }
            }
            out.add(generateBag(length, null, 0));
        }
        else
        {
            for(Color c : colors)
            {
                out.add(generateBag(length, c, 0));
            }
        }

        return List.copyOf(out);
    }

    /**
     * Method which returns the amount of additional cards the player has to play to claim the tunnel.
     * @param claimCards <pre>SortedBag<Card></pre> which contains the hand of cards the player uses to claim the tunnel
     * @param drawnCards <pre>SortedBag<Card></pre> which contains the three cards drawn from the deck
     * @return int representing the additional cards the player has to play
     * @throws IllegalArgumentException if the level equals if the route is not a tunnel or if the number of drawnCards is not 3
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards)
    {

        checkArgument(level == Level.UNDERGROUND && drawnCards.size() == ADDITIONAL_TUNNEL_CARDS);
        {
            int out = 0; //Output

            for (Card drawnCard : drawnCards) { //For each of the claimed cards
                if(drawnCard == Card.LOCOMOTIVE || claimCards.contains(drawnCard))
                {
                    ++out;
                }
            }

            return out;
        }
    }

    /**
     * Method which returns the claimed points if the route is claimed, depending on its length
     * @return int
     */
    public int claimPoints()
    {
        return ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Internal method to generate a bag with a given length, color and number of locomotive cards.
     * @param cardCount number of total cards in the bag
     * @param color color of the cards
     * @param locomotiveCards number of locomotive cards
     * @return <pre>SortedBag<Card></pre>
     */
    private SortedBag<Card> generateBag(int cardCount, Color color, int locomotiveCards)
    {
        List<Card> out = new ArrayList<>();
        for(int i = 0; i < cardCount; ++i)
        {
            out.add((locomotiveCards > i) ? Card.LOCOMOTIVE : Card.of(color));
        }
        return SortedBag.of(out);
    }
}
