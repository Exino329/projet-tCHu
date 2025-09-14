package ch.epfl.tchu.game;


import java.util.List;
import java.util.Objects;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;

/**
 * Class PublicCardState represents the public state of the cars/locomotive cards that are not in the hand of the player
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public class PublicCardState {

    private final List<Card> faceUpCards; //List that contains the 5 faceUpCards
    private final int deckSize;// Number of cards in the deck
    private final int discardsSize;// Number of cards in the discard

    /**
     * Public card state constructor
     * @param faceUpCards list that contains the 5 faceUpCards
     * @param deckSize number of cards in the deck
     * @param discardsSize number of cards in the discard
     * @throws IllegalArgumentException if the list of faceUpCards is not the same as the slot's length of faceUpCards
     * or if the deckSize/discardSize is negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        checkArgument(faceUpCards.size() == FACE_UP_CARDS_COUNT);
        checkArgument(deckSize >= 0);
        checkArgument(discardsSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Face up cards getter
     * @return the list of faceUpCards
     */
    public List<Card> faceUpCards(){
        return faceUpCards;
    }

    /**
     * Face up card getter for a given slot
     * @param slot index
     * @return the card that is at the slot position in the faceUpCards
     * @throws IndexOutOfBoundsException if the argument is not between 0 and the length of faceUpCards-1
     */
    public Card faceUpCard(int slot){
        Objects.checkIndex(slot,faceUpCards.size());
        return faceUpCards.get(slot);
    }

    /**
     * Deck size getter
     * @return the number of cards in the deck
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     * Method which returns true if the deck is empty
     * @return true iff the deck is empty, return false in other case
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Discard size getter
     * @return the number of cards in the discard
     */
    public int discardsSize(){
        return discardsSize;
    }

}
