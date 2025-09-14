package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * Class Info generates texts that describe the progress of the game
 *
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
public final class Info {

    private final String playerName; //Player name

    /**
     * Info constructor
     *
     * @param playerName String
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Card name getter in french
     *
     * @param card  Card
     * @param count number of cards is missing
     * @return String
     */
    public static String cardName(Card card, int count) {
        String cardFr = "";

        switch (card) {

            case BLACK:
                cardFr = StringsFr.BLACK_CARD;
                break;

            case BLUE:
                cardFr = StringsFr.BLUE_CARD;
                break;

            case GREEN:
                cardFr = StringsFr.GREEN_CARD;
                break;

            case ORANGE:
                cardFr = StringsFr.ORANGE_CARD;
                break;

            case RED:
                cardFr = StringsFr.RED_CARD;
                break;

            case VIOLET:
                cardFr = StringsFr.VIOLET_CARD;
                break;

            case WHITE:
                cardFr = StringsFr.WHITE_CARD;
                break;

            case YELLOW:
                cardFr = StringsFr.YELLOW_CARD;
                break;

            case LOCOMOTIVE:
                cardFr = StringsFr.LOCOMOTIVE_CARD;
                break;
        }

        return String.format("%s%s", cardFr, StringsFr.plural(count));
    }

    /**
     * Method which generates the message when both players end the game with the same amount of points
     *
     * @param playerNames list of player names
     * @param points      players' points
     * @return String
     * @throws IllegalArgumentException if the number of players is different from 2
     */
    public static String draw(List<String> playerNames, int points) {
        checkArgument(playerNames.size() == 2); //Check for  size
        String players = String.format("%s%s%s", playerNames.get(0), StringsFr.AND_SEPARATOR, playerNames.get(1));
        return String.format(StringsFr.DRAW, players, points);
    }

    /**
     * Method which generates the message of which player is playing first
     *
     * @return String
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Method which generates the message if a player kept N tickets
     *
     * @param count the amount of tickets
     * @return String
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Method which generates the message if a player can play
     *
     * @return String
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Method which generates the message if the player drew N tickets
     *
     * @param count the amount of tickets
     * @return String
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Method which generates the message if a player drew a blind card
     *
     * @return String
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Method which generates the message if a player drew a visible card
     *
     * @param card the drawn card
     * @return String
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Method which generates the message if a player claims a route
     *
     * @param route claimed route
     * @param cards cards used to claim
     * @return String
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeText(route), bagOfCardsText(cards));
    }

    /**
     * Method which generates the message if a player tries to claim a tunnel
     *
     * @param route        claimed tunnel
     * @param initialCards initial cards used to claim
     * @return String
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeText(route), bagOfCardsText(initialCards));
    }

    /**
     * Method which generates the message if a player drew additional cards for a tunnel claim
     *
     * @param drawnCards     drawn cards
     * @param additionalCost additional cost to claim the tunnel
     * @return String
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder out = new StringBuilder();
        out.append(String.format(StringsFr.ADDITIONAL_CARDS_ARE, bagOfCardsText(drawnCards)));
        if (additionalCost == 0) {
            out.append(StringsFr.NO_ADDITIONAL_COST);
        } else {
            out.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)));
        }
        return out.toString();
    }

    /**
     * Method which generates the message if the player did not claim the tunnel
     *
     * @param route tunnel
     * @return String
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route));
    }

    /**
     * Method which generates the message if a player only has N or less wagons, and that the last turn begins
     *
     * @param carCount the amount of remaining wagons of the player
     * @return String
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Method which generates the message if the player gets the longest trail bonus
     *
     * @param longestTrail the longest trail
     * @return String
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }

    /**
     * Method which generates the message if the player wins, with their points as well as the opponents' points
     *
     * @param points      players' points
     * @param loserPoints opponents' points
     * @return String
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * Internal method which generates the message for a sorted bag of cards
     *
     * @param cards <pre>SortedBag<Card></pre>
     * @return String
     */
    private String bagOfCardsText(SortedBag<Card> cards) {
        int size = cards.toSet().size();

        if (size == 0) {
            return "";
        } else if (size == 1) {
            return cards.countOf(cards.get(0)) + " " + cardName(cards.get(0), cards.countOf(cards.get(0))); //If it is a single card, return it
        } else {
            List<String> cardStrings = new ArrayList<>();
            for (Card c : cards.toSet()) {
                cardStrings.add(cards.countOf(c) + " " + cardName(c, cards.countOf(c)));
            }

            String out;
            out = String.join(",", cardStrings.subList(0, cardStrings.size() - 1));
            out = String.join(StringsFr.AND_SEPARATOR, out, cardStrings.get(cardStrings.size() - 1));
            return out;
        }
    }

    /**
     * Internal method which generates the text for a given route
     *
     * @param route Route
     * @return String
     */
    private String routeText(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }
}



