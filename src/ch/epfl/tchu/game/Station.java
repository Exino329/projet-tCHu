package ch.epfl.tchu.game;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * Class Station represents a station
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Station {

    private final int id;// Each station has unique id
    private final String name; // Name of the station

    /**
     * Constructor. Create a new station
     * @param id Id of the station
     * @param name // Name of the station
     * @throws IllegalArgumentException if the id is negative
     */
    public Station(int id,String name){
        checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     *id getter
     * @return int id
     */
    public int id(){
        return this.id;
    }

    /**
     * name getter
     * @return string with name
     */
    public String name(){
        return this.name;
    }

    /**
     * Method which returns the name of the route
     * @return the name of the instance
     */
    @Override
    public String toString(){
        return this.name;
    }
}
