package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumeration that represents the different types of cards of the game
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
  */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null); //Colors associated to each card, Card.LOCOMOTIVE is defined as null

    private final Color color; //Color of the card

    /**
     * Card constructor
     * @param color Color assigned to the card
     */
    Card(Color color)
    {
        this.color = color; //No RequireNonNull as LOCOMOTIVE has no color assigned
    }

    public static final List<Card> ALL = List.of(Card.values()); //List object containing all cards

    public static final int COUNT = ALL.size(); //Total number of cards

    public static final List<Card> CARS = ALL.subList(0, COUNT - 1); //List containing all cars


    /**
     * Method returning the card corresponding to a particular color
     * @param color color
     * @return Card of color 'color'
     */
    public static Card of(Color color)
    {
        for(Card c : Card.ALL)
        {
            if(c.color() == color)
            {
                return c;
            }
        }
        return LOCOMOTIVE;
    }

    /**
     * Color getter
     * @return Color of the card
     */
    public Color color()
    {
        return color;
    }

}
