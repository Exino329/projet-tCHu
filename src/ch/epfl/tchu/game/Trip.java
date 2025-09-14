package ch.epfl.tchu.game;

import java.util.*;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * Class that represents a trip
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Trip {

    private final Station from; //Starting station
    private final Station to; //Target station
    private final int points; //Awarded points

    private static final List<String> countries = List.of("France","Allemagne","Italie","Autriche");
    //List of countries to detect to correct the order if there is a city-country relationship

    /**
     * Trip constructor. Automatically corrects the order for a city-country relationship
     * @param from Starting station
     * @param to Target station
     * @param points Points awarded for the completion of the trip
     * @throws IllegalArgumentException if the number of points is less or equal than 0
     */
    public Trip(Station from, Station to, int points)
    {
        checkArgument(points > 0);
        Objects.requireNonNull(to);
        Objects.requireNonNull(from);
        this.points = points;

        this.from = (countries.contains(from.name()) && !countries.contains(to.name())) ? to : from;
        this.to = (countries.contains(from.name()) && !countries.contains(to.name())) ? from : to;
    }

    /**
     * Method which returns a list of all possible trips between two lists of stations
     * @param from List of starting stations
     * @param to List of target stations
     * @param points Points awarded for the completion of the trip
     * @throws IllegalArgumentException if the station from or the station to is null or if the points of the trip is less than 1
     * @return List of trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points)
    {
        List<Trip> all = new ArrayList<>();

        checkArgument(!from.isEmpty() && !to.isEmpty() && points >= 1);

        for (Station stationFrom : from) {
            for (Station stationTo : to) {
                all.add(new Trip(stationFrom, stationTo, points));
            }
        }

        return List.copyOf(all);
    }

    /**
     * Departure station getter
     * @return Station
     */
    public Station from()
    {
        return from;
    }

    /**
     * Arrival station getter
     * @return Station
     */
    public Station to()
    {
        return to;
    }

    /**
     * Point getter
     * @return int
     */
    public int points()
    {
        return points;
    }

    /**
     * Tests if two stations are connected and returns positive points if they are, negative otherwise
     * @param connectivity the station connectivity
     * @return int points
     */
    public int points(StationConnectivity connectivity)
    {
        return (connectivity.connected(this.from(),this.to())) ? points : -points;
    }
}