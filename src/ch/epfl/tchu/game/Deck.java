package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static ch.epfl.tchu.Preconditions.checkArgument;

/**
 * Class deck represents a heap of cards
 * @author Lorin Lieberherr (326858)
 * @author Elija Dirren (310502)
 */
//
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards; //Represents cards in the deck

    /**
     * Deck constructor (private), creates the list of cards
     * @param cards represents cards in the deck
     */
    private Deck(List<C> cards){
        this.cards = List.copyOf(cards);
    }

    /**
     * Method that is used to construct a deck
     * @param cards cards that are in the sortedBag
     * @param rng random generator to shuffle the cards
     * @param <C> type of the cards in the sorted bag
     * @return a deck with shuffled cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> deckCards = cards.toList(); //Creation of the list that contains the cards that are in the sortedBag
        Collections.shuffle(deckCards,rng); //Shuffle of cards
        return new Deck(deckCards);

    }

    /**
     *
     * @return the number of cards that are remaining in the deck
     */
    public int size(){
        return cards.size();
    }

    /**
     *
     * @return true iff the deck is empty
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     *
     * @return the card at the top of the deck
     * @throws IllegalArgumentException there remains no card in the deck
     */
    public C topCard(){
        checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     *
     * @return the deck without the card at the top of the deck
     * @throws IllegalArgumentException there remains no card in the deck
     */
    public Deck withoutTopCard(){
        checkArgument(!cards.isEmpty());
        return new Deck(cards.subList(1, cards.size()));
    }

    /**
     *
     * @param count number of cards that the method needs to return
     * @return a sortedBad that contains the count's cards at the top of the deck
     * @throws IllegalArgumentException if the argument count is negative of if it exceeds the number of cards in the deck
     */
    public SortedBag<C> topCards(int count) {
        checkArgument(count >= 0 && count <= cards.size());
        return SortedBag.of(cards.subList(0,count));
    }

    /**
     *
     * @param count number of cards at the top that need to be skipped while creating the new deck
     * @return a deck without the count's cards at the top of the deck
     * @throws IllegalArgumentException if the argument count is negative of if it exceeds the number of cards in the deck
     */
    public Deck<C> withoutTopCards(int count){
        checkArgument(count >= 0 && count <= cards.size());
        return new Deck(cards.subList(count, cards.size()));
    }
}
