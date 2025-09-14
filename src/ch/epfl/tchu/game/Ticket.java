package ch.epfl.tchu.game;

import java.util.List;
import java.util.TreeSet;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * represents a ticket going from one station to another
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Ticket implements Comparable<Ticket> {

    private final String text; // Textual representation of the ticket
    private final List<Trip> trips; // Trips possibilities

    /**
     * First constructor. Create ticket with all possible trips
     * @param trips Trips possibilities
     * @throws IllegalArgumentException if the list of trips is empty or if one trip does not have the same from station
     */
    public Ticket(List<Trip> trips){
        checkArgument(!trips.isEmpty() && hasSameFromStation(trips));
        //Throw and argumentException if the list of trips is empty or if all elements don't have same start station;
        this.trips = List.copyOf(trips);
        this.text = computeText(trips);
    }

    /**
     * Second constructor. Create a ticket with unique trip
     * @param from Starting station
     * @param to Target station
     * @param points Awarded points
     */
    public Ticket(Station from,Station to,int points){
        this(List.of((new Trip(from, to, points))));
    }

    /**
     * Method which returns true if all trips have the same departure station
     * @param trips Trips possibilities
     * @return False if one of the trips doesn't have same departure station as the other trips. return true otherwise
     */
    private boolean hasSameFromStation(List<Trip> trips){
        String s1 = trips.get(0).from().toString();
        for(Trip trip : trips) {
            if (!s1.equals(trip.from().toString()))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Method which returns the textual representation of the ticket
     * @return textual representation of the ticket
     */
    public String text(){
        return text;
    }

    /**
     * Method which returns the textual representation of the ticket
     * @return textual representation of the ticket
     */
    @Override
    public String toString(){
        return text;
    }

    /**
     * Method which computes the text from a given list of trips
     * @param trips Trips possibilities
     * @return Textual representation of a ticket. The representation depends on whether the ticket is a city-to-city ticket or a city/country-to-country ticket
     */
    private static String computeText(List<Trip> trips){
        String textTicket;
        TreeSet<String> toStation = new TreeSet<>();

        for (Trip trip : trips) {
            toStation.add(String.format("%s (%s)",
                    trip.to().toString(),
                    trip.points()));
        }

        if (toStation.size() > 1){
            textTicket = String.format("%s - {%s}",
                    trips.get(0).from().toString(),
                    String.join(", ", toStation));
        }
        else {
            //If the ticket has one trip (which means that there is only one toStation), it is a city-to-city ticket
            textTicket = String.format("%s - %s (%s)",
                    trips.get(0).from().toString(),
                    trips.get(0).to().toString(),
                    trips.get(0).points());
        }
        return textTicket;
    }

    /**
     * Method which returns the points from a given connectivity
     * @param connectivity represent the connectivity between two station
     * @return maxPoints return the maxPoints in absolute value
     */
    public int points(StationConnectivity connectivity){
        int maxPoints = trips.get(0).points(connectivity);

        for(Trip trip : trips){
            if(maxPoints < trip.points(connectivity)){
                maxPoints = trip.points(connectivity);
            }
        }

        return maxPoints;
    }

    /**
     * Method which compares the ticket to a given ticket
     * @param that Is a ticket
     * @return Negative integer if the ticket's instance (this) come before the ticket to compare (that) before in alphabet position,
     *         0 if both are equals,
     *         positive integer if the ticket to compare come before the ticket's instance in alphabet position
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }
}
