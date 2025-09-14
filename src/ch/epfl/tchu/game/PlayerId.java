package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration PlayerId is used to distinguish and recognize the players
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(PlayerId.values()); //List of all players

    public static final int COUNT = ALL.size(); //Number of players

    /**
     * Method which gives the next player after the one to which the method is applied
     * @return PlayerId
     */
    public PlayerId next()
    {
        return (this.equals(PLAYER_1)) ? PLAYER_2 : PLAYER_1;
    }
}
