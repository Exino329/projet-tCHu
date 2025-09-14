package ch.epfl.tchu.game;

import java.util.List;

/**
 * enumeration Color represents the 8 colors used in the game (for cars and routes cards)
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public enum Color {

    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    public static final List<Color> ALL = List.of(Color.values()); //List of all colors

    public static final int COUNT = ALL.size(); //Total number of colors

}
