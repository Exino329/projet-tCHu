package ch.epfl.tchu.game;

/**
 * Interface StationConnectivity is used to determine if two stations are connected or not
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public interface StationConnectivity {

     /**
      * Method which returns true if the two stations are connected
      * @param s1 station 1
      * @param s2 station 2
      * @return boolean
      */
     boolean connected(Station s1,Station s2);
}

