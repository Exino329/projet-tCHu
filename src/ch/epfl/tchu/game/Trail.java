package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Trail is a chain of routes that is unidirectional
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class Trail {

    private final int length; // Sum of trail's routes lengths
    private final Station station1;// Trail's departure station
    private final Station station2;// Trail's arrival station
    private final List<Route> routes;// Contains all of the routes  of the trail
    private static final Trail emptyTrail = new Trail(null, null, new ArrayList<>()); // Empty trail

    /**
     * Trail constructor
     * @param station1 Trail's departure station
     * @param station2 Trail's arrival station
     * @param routes contains all of the routes of the trail
     *
     */
    private Trail(Station station1,Station station2,List<Route> routes){
        this.station1 = station1;
        this.station2 = station2;
        this.routes = List.copyOf(routes);
        this.length = computeLength(routes); // Sum of trail's routes  lengths
    }

    /**
     * Method which computes the length of the trail
     * @param routes is list of the routes of the trail
     * @return the sum of trail's routes  lengths
     */
    private static int computeLength(List<Route> routes){
        int trailLength = 0;

        for (Route route : routes) {
            trailLength += route.length();
        }
        return trailLength;
    }

    /**
     * Method which computes the longest trail in a given list of routes
     * @param routes All of the routes of the trail
     * @return the longest Trail
     */
    public static Trail longest(List<Route> routes) {

        Trail longestTrail = null;//initialisation of the Longest trail
        List<Trail> trailsWithNRoute = new ArrayList<>();//List of trails that has n trails (depend of iteration of the method)

        //check if the list of routes that are given in argument is empty
        if (routes.size() == 0) {
            return emptyTrail;
        }

        //This for loop compute a list of trails that only have one route (the trails have to start in both directions)
        for (Route route : routes) {
            List<Route> listRoutes = new ArrayList<>();
            listRoutes.add(route);
            trailsWithNRoute.add(new Trail(route.station1(), route.station2(), listRoutes));
            trailsWithNRoute.add(new Trail(route.station2(), route.station1(), listRoutes));
        }


        while(trailsWithNRoute.size() != 0){
            List<Trail> extendedTrail = new ArrayList<>();//trails with n+1 routes (n starts at 1)
            for (Trail t : trailsWithNRoute){
                for(Route r : routes){
                    /*We can extend a trail if the trail doesn't already contains the route and
                      if trail's arrival station is the same as the route's departure station */
                    if (!t.routes.contains(r)){
                        if(t.station2() == r.station1()){
                            List<Route> listRoute = new ArrayList<>(t.routes);//create new list with actual routes of the trail t
                            listRoute.add(r);//Extends the trail adding the route r
                            extendedTrail.add(new Trail(t.station1(),r.station2(),listRoute));//add the extended trail to the list with n+1 routes
                        }
                        else if(t.station2() == r.station2()){
                            List<Route> listRoute = new ArrayList<>(t.routes);//create new list with actual routes of the trail t
                            listRoute.add(r);//Extends the trail adding the route r
                            extendedTrail.add(new Trail(t.station1(),r.station1(),listRoute));//add the extended trail to the list with n+1 routes
                        }
                    }
                }
                //this condition selects the longest trail
                if (longestTrail == null || longestTrail.length() < t.length()){
                    longestTrail = t;
                }
            }
            //Create a new list of trails with n+1 routes;
            trailsWithNRoute = new ArrayList<>(extendedTrail);
        }
        return longestTrail;

    }

    /**
     * Length getter
     * @return the sum of the trail's routes lengths
     */
    public int length() {
        return length;
    }

    /**
     * Departure station getter
     * @return the trail's departure station
     */
    public Station station1(){
        return station1;
    }

    /**
     * Arrival station getter
     * @return the trail's arrival station
     */
    public Station station2() {
        return station2;
    }

    /**
     * Method which returns a trails' textual representation
     * @return the trail's textual representation, if the list of roads is empty, return "-----"
     */
    @Override
    public String toString() {
        String out;
        if (routes.isEmpty()){
            return "-----"; //Returns this string if the list of roads is empty
        } else{
            out = station1.toString();
            Station nextStation = station1();
            for (Route r : routes){
                out = String.format("%s - %s",out,r.stationOpposite(Objects.requireNonNull(nextStation)).toString());
                nextStation = r.stationOpposite(nextStation);
            }
        }
        return String.format("%s (%s)",out,length());
    }
}
