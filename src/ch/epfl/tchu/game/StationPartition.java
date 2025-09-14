package ch.epfl.tchu.game;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * Class StationPartition is used to represents the connectivity of a route network
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class StationPartition implements StationConnectivity {

    private final int[] partition; // The partition that represents the connectivity between the routes

    /**
     * Private constructor of the partition
     * @param partition represents the connectivity between the routes
     */
    private StationPartition(int[] partition){
        this.partition = partition.clone();
    }

    /**
     *
     * @param s1 is a station
     * @param s2 is a station
     * @return true if both stations have the same representative station.
     * If one of the station (or both) is out of the partition, return also true if they have the same id
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= partition.length || s2.id() >= partition.length){
            return s1.id() == s2.id();
        }
        else {
           /* 2 stations are connected if they are represented by the same representative station
              (which means that they belong to the same set of connectivity */
           return partition[s1.id()] == partition[s2.id()];
        }
    }

    /**
     * Station partition builder class
     */
    public final static class Builder {

        private final int[]buildPartition;//The partition to build

        /**
         * Builder constructor
         * @param stationCount represents the station (from id 0 to stationCount-1) that belong to the partition
         * @throws IllegalArgumentException if stationCount is negative
         */
        public Builder(int stationCount) {
            checkArgument(stationCount >=0 );

            this.buildPartition = new int[stationCount];
            for (int i = 0;i<stationCount;++i){
                buildPartition[i] = i;
            }
        }

        /**
         * Representative id getter
         * @param idStation is the id of the station
         * @return the representative station of the idStation given in argument
         */
        private int representative(int idStation){
            //Definition : a representative station of a station has the same index and id in the partition. A station can represents itself too
            while(buildPartition[idStation] != idStation){

                /*If the current id of the station is not the same as it index,
                we look on the station that is at this index until tne station id equals it index in the partition*/
                idStation = buildPartition[idStation];
            }
            return idStation;
        }

        /**
         * Method to connect two stations
         * @param s1 is a station
         * @param s2 is a station
         * @return the partition after connecting the 2 stations given in argument between them
         */
        public Builder connect(Station s1, Station s2) {
            int r1 = representative(s1.id());

            /*to connect two stations between them,
             we take the index of the representative station s1 and modify its value
             which takes the value of the id of the representative station s2  */
            buildPartition[r1] = representative(s2.id());
            return this;
        }

        /**
         * Build method, returning the completed partition
         * @return the completely built partition
         */
        public StationPartition build() {
            for(int i = 0;i<buildPartition.length;++i){
                //each index (or each station's id from 0 to stationCount -1) of the partition takes the value of its representative station
                buildPartition[i] = representative(i);
            }
            return new StationPartition(buildPartition);
        }
    }
}

