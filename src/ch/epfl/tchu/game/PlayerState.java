package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.*;

/**
 * Class PlayerState represents the private state of a player (i.e. the tickets, the cards, and the route that he owns)
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets; //Tickets the player has
    private final SortedBag<Card> cards; ///Cards the player has

    /**
     * Player state constructor
     * @param tickets the players' tickets
     * @param cards the players' cards
     * @param routes the players' routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes)
    {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Player state constructor with the initial cards for the player
     * @param initialCards the players' initial cards
     * @throws IllegalArgumentException if the initialCards size is not 4
     * @return PlayerState
     */
    public static PlayerState initial(SortedBag<Card> initialCards)
    {
        checkArgument(initialCards.size() == INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>());
    }

    /**
     * Ticket getter
     * @return <pre>SortedBag<Ticket></pre>
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * Method which adds the given tickets to the ticket list of the player
     * @param newTickets the players' added tickets
     * @return PlayerState with added tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets.union(newTickets),cards,routes());
    }


    /**
     * Card getter
     * @return sorted bag of cards
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * Method which adds a single card to the players' cards
     * @param card the card to be added to the players' cards
     * @return PlayerState with added Card
     */
    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Method which returns true if the player can claim the specified route with his cards
     * @param route the route to be tested
     * @return boolean
     */
    public boolean canClaimRoute(Route route){
        return super.carCount() >= route.length() && !possibleClaimCards(route).isEmpty();
    }

    /**
     * Method which computes the possible claim cards that the player can play from his own cards
     * @param route the route which the player tries to claim
     * @throws IllegalArgumentException if the player has less cards than the route's length
     * @return <pre>List<SortedBag<Card>></pre>
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){

        //Throw exception if the player doesn't have enough cards
        checkArgument(super.carCount() >= route.length());

        return playableCards(route.possibleClaimCards(), this.cards);
    }

    /**
     * Method which computes the possible additional cards the player can play to claim a tunnel
     * @param additionalCardsCount the number of additional cards
     * @param initialCards the initial cards the player uses to claim the route
     * @throws IllegalArgumentException if:
     * <ul>
     *     <li>The amount of additional cards is not between 1 and 3 (included)</li>
     *     <li>The initial cards are empty</li>
     *     <li>The initial cards contain more than two different types of cards</li>
     *     <li>The amount of drawn cards is different than 3</li>
     * </ul>
     * @return <pre>List<SortedBag<Card>></pre>
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards){

        //Exception throw if one of the conditions in the javadoc isn't met
        checkArgument(additionalCardsCount >= 1);
        checkArgument(additionalCardsCount <= ADDITIONAL_TUNNEL_CARDS);
        checkArgument(!initialCards.isEmpty());
        checkArgument(differentCardTypes(initialCards) <= 2);

        Card claimCardType = null; //Determine the type of card that is used to claim the road (other than Card.LOCOMOTIVE)

        for(Card c : initialCards)
        {
            if(!c.equals(Card.LOCOMOTIVE))
            {
                claimCardType = c;
                break;
            }
        }

        //Generate a list of all possible hands that can be used to claim the tunnel
        List<SortedBag<Card>> allPossibleAdditionalCards = new ArrayList<>();

        //If the player does not only use locomotive cards
        if(claimCardType != null)
        {
            //Generate all possible combinations of cards
            for(int i = 0; i < additionalCardsCount; ++i) //Loop for combination with i locomotive cards
            {
                List<Card> hand = new ArrayList<>();

                for(int j = 0; j < additionalCardsCount; ++j) //Loop for card placement in combination
                {
                    if(i <= j)
                    {
                        hand.add(claimCardType);
                    }
                    else
                    {
                        hand.add(Card.LOCOMOTIVE);
                    }
                }

                allPossibleAdditionalCards.add(SortedBag.of(hand));
            }
        }

        //Create the locomotive only hand
        List<Card> onlyLocomotiveHand = new ArrayList<>();
        for(int i = 0; i < additionalCardsCount; ++i)
        {
            onlyLocomotiveHand.add(Card.LOCOMOTIVE);
        }
        allPossibleAdditionalCards.add(SortedBag.of(onlyLocomotiveHand));

        return playableCards(allPossibleAdditionalCards, cards.difference(initialCards));
    }

    /**
     * Method which adds the claimed route to the players' collection and removes the used claim cards
     * @param route the route the player claimed
     * @param claimCards the cards the player used to claim the route
     * @return PlayerState
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards),newRoutes);
    }

    /**
     * Total ticket point getter
     * @return int
     */
    public int ticketPoints(){

        int maxIdStation = 0;
        StationPartition.Builder buildPartition;
        for (Route route : routes()){
            if (route.station1().id() > maxIdStation){
                maxIdStation = route.station1().id();
            }
            if (route.station2().id() > maxIdStation){
                maxIdStation = route.station2().id();
            }
        }
        buildPartition = new StationPartition.Builder(maxIdStation+1);

        for(Route route : routes())
        {
            buildPartition.connect(route.station1(), route.station2());
        }
        StationPartition partition = buildPartition.build();

        int sum = 0;
        for(Ticket ticket : tickets)
        {
            sum += ticket.points(partition);
        }
        return sum;

    }

    /**
     * Getter for the total final points (claim points + ticket points)
     * @return int
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }

    /**
     * Internal method which computes the amount of distinct cards in a sorted bag
     * @param cards the cards to test
     * @return int
     */
    private int differentCardTypes(SortedBag<Card> cards)
    {
        return cards.toSet().size();
    }

    /**
     * Internal method which computes all the card combinations the player can play with his cards
     * @param possibleCards <pre>List<SortedBag<Card>></pre> with all possible combinations to choose from
     * @return <pre>List<SortedBag<Card></pre> Playable hands
     */
    private List<SortedBag<Card>> playableCards(List<SortedBag<Card>> possibleCards, SortedBag<Card> currentPlayerCards)
    {
        List<SortedBag<Card>> out = new ArrayList<>(); //Create the output list
        for(SortedBag<Card> option : possibleCards)
        {
            if(currentPlayerCards.contains(option))
            {
                out.add(option);
            }
        }
        return List.copyOf(out);
    }
}
