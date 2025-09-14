package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARDS_COUNT;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest2 {

    private static GameState gameState;
    private static Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i){
            return i-1;
        }
    };
    private static SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
    private static CardState cardDeck;


    @BeforeAll
    static void  init(){
        gameState = GameState.initial(tickets,NON_RANDOM);
        cardDeck = CardState.of(Deck.of(SortedBag.of(Constants.ALL_CARDS),NON_RANDOM).withoutTopCards(Constants.INITIAL_CARDS_COUNT*PlayerId.COUNT ));
    }


    @Test
    void topTicketsWorksWell(){
        SortedBag<Ticket> ticketsList = Deck.of(tickets,NON_RANDOM).topCards(5);
        assertEquals(ticketsList,gameState.topTickets(5));
    }



    @Test
    void withoutTopTicketsWorks(){
        GameState g1 = gameState.withoutTopTickets(5);

        assertEquals(gameState.ticketsCount()-5, g1.ticketsCount());
    }

    @Test
    void TopCardWorks(){
        assertEquals(cardDeck.topDeckCard(),gameState.topCard());
    }

    @Test
    void withoutTopCardWorks(){
        assertEquals(cardDeck.withoutTopDeckCard().topDeckCard(),gameState.withoutTopCard().topCard());

        assertEquals(cardDeck.withoutTopDeckCard().deckSize(),gameState.withoutTopCard().cardState().deckSize());
    }

    @Test
    void withMoreDiscardedCardsWorks(){
        SortedBag<Card> discard = SortedBag.of(List.of(Card.GREEN,Card.BLUE,Card.WHITE,Card.LOCOMOTIVE));
        assertEquals(cardDeck.withMoreDiscardedCards(discard).discardsSize(),gameState.cardState().discardsSize() + discard.size());
    }

    @Test
    void topTicketsFails(){
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.topTickets(-5);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            gameState.topTickets(ChMap.tickets().size() + 5);
        });

    }

    @Test
    void  withCardsDeckRecreatedIfNeededWithEmptyDeck(){
        SortedBag<Card> discard = SortedBag.of(List.of(Card.GREEN,Card.BLUE,Card.WHITE,Card.LOCOMOTIVE));
        CardState cardState = CardState.of(Deck.of(SortedBag.of(FACE_UP_CARDS_COUNT,Card.BLUE),NON_RANDOM));
        cardState =cardState.withMoreDiscardedCards(discard);
        GameState gameState1 = GameState.initial(tickets,NON_RANDOM);
        GameState gameState2 = gameState1;
        for(int i = 0; i<gameState1.cardState().deckSize();++i) {
            gameState2 = gameState2.withoutTopCard();
        }
        gameState2 = gameState2.withMoreDiscardedCards(discard);
        assertEquals(discard.size(),gameState2.withCardsDeckRecreatedIfNeeded(NON_RANDOM).cardState().deckSize());
    }

    @Test
    void withChosenAdditionalTicketsWorks(){
        SortedBag<Ticket> tickets1 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2),ChMap.tickets().get(3),ChMap.tickets().get(4)));
        SortedBag<Ticket> tickets2 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2),ChMap.tickets().get(3)));
        SortedBag<Ticket> tickets3 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2)));
        SortedBag<Ticket> tickets4 = SortedBag.of(List.of(ChMap.tickets().get(1)));
        GameState gameState1 = gameState.withChosenAdditionalTickets(tickets1,tickets1);
        assertEquals(gameState.currentPlayerState().tickets().size() + tickets1.size(),gameState1.currentPlayerState().tickets().size());
        GameState gameState2 = gameState.withChosenAdditionalTickets(tickets1,tickets2);
        assertEquals(gameState.currentPlayerState().tickets().size() + tickets2.size(),gameState2.currentPlayerState().tickets().size());
        GameState gameState3 = gameState.withChosenAdditionalTickets(tickets1,tickets3);
        assertEquals(gameState.currentPlayerState().tickets().size() + tickets3.size(),gameState3.currentPlayerState().tickets().size());
        GameState gameState4 = gameState.withChosenAdditionalTickets(tickets1,tickets4);
        assertEquals(gameState.currentPlayerState().tickets().size() + tickets4.size(),gameState4.currentPlayerState().tickets().size());
    }


    @Test
    void withChosenAdditionalTicketsFailsWithNonContainedCard(){
        SortedBag<Ticket> tickets1 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2),ChMap.tickets().get(3),ChMap.tickets().get(4)));
        SortedBag<Ticket> tickets2 = SortedBag.of(List.of(ChMap.tickets().get(5),ChMap.tickets().get(6),ChMap.tickets().get(7),ChMap.tickets().get(8)));
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withChosenAdditionalTickets(tickets1,tickets2);
        });
        SortedBag<Ticket> tickets3 = SortedBag.of(List.of(ChMap.tickets().get(5),ChMap.tickets().get(6),ChMap.tickets().get(7),ChMap.tickets().get(4)));
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withChosenAdditionalTickets(tickets1,tickets3);
        });
        SortedBag<Ticket> tickets4 = SortedBag.of(List.of(ChMap.tickets().get(5),ChMap.tickets().get(6),ChMap.tickets().get(3),ChMap.tickets().get(4)));
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withChosenAdditionalTickets(tickets1,tickets4);
        });
        SortedBag<Ticket> tickets5 = SortedBag.of(List.of(ChMap.tickets().get(5),ChMap.tickets().get(2),ChMap.tickets().get(3),ChMap.tickets().get(4)));
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withChosenAdditionalTickets(tickets1,tickets5);
        });
    }

    @Test
    void withInitiallyChosenTicketsFailsWithNonEmptyIntersection(){
        SortedBag<Ticket> tickets1 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2),ChMap.tickets().get(3),ChMap.tickets().get(4)));
        SortedBag<Ticket> tickets2 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2),ChMap.tickets().get(3)));
        SortedBag<Ticket> tickets3 = SortedBag.of(List.of(ChMap.tickets().get(1),ChMap.tickets().get(2)));
        SortedBag<Ticket> tickets4 = SortedBag.of(List.of(ChMap.tickets().get(1)));
        gameState =gameState.withChosenAdditionalTickets(tickets1,tickets1);
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withInitiallyChosenTickets( gameState.currentPlayerId(),tickets1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withInitiallyChosenTickets( gameState.currentPlayerId(),tickets2);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withInitiallyChosenTickets( gameState.currentPlayerId(),tickets3);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            gameState.withInitiallyChosenTickets( gameState.currentPlayerId(),tickets4);
        });




    }



    @Test
    void withBlindlyDrawnCardWorks(){
        assertEquals(gameState.withBlindlyDrawnCard().cardState().deckSize(),cardDeck.withoutTopDeckCard().deckSize() );
    }

    @Test
    void withDrawnFaceUpCardWorks(){
        assertEquals(gameState.withDrawnFaceUpCard(3).cardState().faceUpCards(),cardDeck.withDrawnFaceUpCard(3).faceUpCards());
    }

    @Test
    void withClaimedRouteWorks(){


    }




}
