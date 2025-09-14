package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;


/**
 * Class PublicCardState represents the private state of the cars/locomotive cards that are not in the hand of the player
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck; //Represents the deck
    private final SortedBag<Card> discard; // Represents the discard

    /**
     * private constructor of CardState, this constructor calls the super-constructor
     * to initialize the attributes of SuperCardState and then initialize it's own attribute
     * @param faceUpCards //List that contains the 5 faceUpCards
     * @param deck //Represents the deck
     * @param discard // Represents the discard
     */
    private CardState(List<Card> faceUpCards,Deck<Card> deck,SortedBag<Card> discard){
        super(faceUpCards,deck.size(),discard.size());
        this.deck = deck ;
        this.discard = discard;
    }

    /**
     *
     * @param deck Represents the deck
     * @return a new CardState with the 5 faceUpCards, the deck were players draw and a discard (that is empty for the beginning of a party)
     * @throws IllegalArgumentException if the deck has less cards than the five faceUpCards
     */
    public static CardState of(Deck<Card> deck){
        checkArgument(deck.size() >= FACE_UP_CARDS_COUNT);

        return new CardState(deck.topCards(FACE_UP_CARDS_COUNT).toList(),
                deck.withoutTopCards(FACE_UP_CARDS_COUNT),
                SortedBag.of());
    }

    /**
     *
     * @param slot index from the faceUpCards to remove
     * @return the actual CardState with a new list of faceUpCards
     * @throws IllegalArgumentException if the argument slot is less or equal than 0
     * (the actual faceUpCards list contain the same list as before except at the index slot
     * which contains the card at the top of the deck)
     * @throws IndexOutOfBoundsException if the slot is not between 0 and 4 (both included)
     */
    public CardState withDrawnFaceUpCard(int slot){
        checkArgument(!isDeckEmpty());
        
        // the slot need to be bounded between position 0 and position 5 of the faceUpCards
        Objects.checkIndex(slot, FACE_UP_CARDS_COUNT);
        List<Card> newFaceUpCards= new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot,topDeckCard());

        return new CardState(newFaceUpCards,
                deck.withoutTopCard(),
                discard);

    }

    /**
     *
     * @return the card at the top of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topDeckCard(){
        checkArgument(!isDeckEmpty());
        return deck.topCard();
    }


    /**
     *
     * @return the actual CardState without its first top deck card
     * @throws IllegalArgumentException if the deck is empty
     */
    public CardState withoutTopDeckCard(){
        checkArgument(!isDeckEmpty());
        return new CardState(faceUpCards(),
                deck.withoutTopCard(),
                discard);
    }

    /**
     *
     * @param rng random generator to shuffle the cards
     * @return a new CardState by recreating a new deck with the discard
     * @throws IllegalArgumentException if the deck is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        checkArgument(isDeckEmpty());
        Deck<Card> deckRecreated = Deck.of(discard,rng);
        return new CardState(faceUpCards(),
                deckRecreated,SortedBag.of());
    }

    /**
     *
     * @param additionalDiscards cards to add to the discard
     * @return a new CardState, adding the list of cards given in argument to the discard
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
    return new CardState(faceUpCards(), deck, discard.union(additionalDiscards));
    }

}

