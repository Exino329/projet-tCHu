package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ch.epfl.tchu.net.Serdes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerdesTest {

    Random rng = new Random();

    @Test
    public void integerSerdeTest()
    {
        int int1 = 284756;
        int int2 = 19048504;
        int int3 = 6324;

        String int1Ser = INTEGER_SERDE.serialize(int1);
        String int2Ser = INTEGER_SERDE.serialize(int2);
        String int3Ser = INTEGER_SERDE.serialize(int3);

        assertEquals(int1, (int) INTEGER_SERDE.deserialize(int1Ser));
        assertEquals(int2, (int) INTEGER_SERDE.deserialize(int2Ser));
        assertEquals(int3, (int) INTEGER_SERDE.deserialize(int3Ser));

        int randomInt1 = rng.nextInt(1000000000);
        int randomInt2 = rng.nextInt(1000000000);
        int randomInt3 = rng.nextInt(1000000000);

        String randomInt1Ser = INTEGER_SERDE.serialize(randomInt1);
        String randomInt2Ser = INTEGER_SERDE.serialize(randomInt2);
        String randomInt3Ser = INTEGER_SERDE.serialize(randomInt3);

        assertEquals(randomInt1, (int) INTEGER_SERDE.deserialize(randomInt1Ser));
        assertEquals(randomInt2, (int) INTEGER_SERDE.deserialize(randomInt2Ser));
        assertEquals(randomInt3, (int) INTEGER_SERDE.deserialize(randomInt3Ser));
    }

    @Test
    public void stringSerdeTest()
    {
        String string1 = "Jean Jacques Goldman";
        String string2 = "Rien n'est si pénible qu'un esprit posé ne puisse y trouver un quelconque réconfort.";
        String string3 = "Quel est ce pays merveilleux, Que je chéris, où je suis né ? Où l'Alpe blanche jusqu'aux cieux Élève son front couronné ! Vallée où le Rhône a son cours, Noble pays de mes amours, C'est toi, c'est toi, mon beau Valais ! Reste à jamais, Reste à jamais, Reste mes amours !";

        String string1Ser = STRING_SERDE.serialize(string1);
        String string2Ser = STRING_SERDE.serialize(string2);
        String string3Ser = STRING_SERDE.serialize(string3);

        assertEquals(string1, STRING_SERDE.deserialize(string1Ser));
        assertEquals(string2, STRING_SERDE.deserialize(string2Ser));
        assertEquals(string3, STRING_SERDE.deserialize(string3Ser));

        String stringAlphabet = "abcdefghijklmnopqrstuvwxyz123456789";

        String randomString1 = generateRandomString(stringAlphabet);
        String randomString2 = generateRandomString(stringAlphabet);
        String randomString3 = generateRandomString(stringAlphabet);

        String randomString1Ser = STRING_SERDE.serialize(randomString1);
        String randomString2Ser = STRING_SERDE.serialize(randomString2);
        String randomString3Ser = STRING_SERDE.serialize(randomString3);

        assertEquals(randomString1, STRING_SERDE.deserialize(randomString1Ser));
        assertEquals(randomString2, STRING_SERDE.deserialize(randomString2Ser));
        assertEquals(randomString3, STRING_SERDE.deserialize(randomString3Ser));
    }

    @Test
    public void playerIdSerdeTest()
    {
        PlayerId player1 = PlayerId.PLAYER_1;
        PlayerId player2 = PlayerId.PLAYER_2;

        String player1Ser = PLAYER_ID_SERDE.serialize(player1);
        String player2Ser = PLAYER_ID_SERDE.serialize(player2);

        assertEquals(player1, PLAYER_ID_SERDE.deserialize(player1Ser));
        assertEquals(player2, PLAYER_ID_SERDE.deserialize(player2Ser));
    }

    @Test
    public void turnKindSerdeTest()
    {
        Player.TurnKind claimRoute = Player.TurnKind.CLAIM_ROUTE;
        Player.TurnKind drawCards = Player.TurnKind.DRAW_CARDS;
        Player.TurnKind drawTickets = Player.TurnKind.DRAW_TICKETS;

        String claimRouteSer = TURN_KIND_SERDE.serialize(claimRoute);
        String drawCardsSer = TURN_KIND_SERDE.serialize(drawCards);
        String drawTicketsSer = TURN_KIND_SERDE.serialize(drawTickets);

        assertEquals(claimRoute, TURN_KIND_SERDE.deserialize(claimRouteSer));
        assertEquals(drawCards, TURN_KIND_SERDE.deserialize(drawCardsSer));
        assertEquals(drawTickets, TURN_KIND_SERDE.deserialize(drawTicketsSer));
    }

    @Test
    public void cardSerdeTest()
    {
        for(Card c : Card.values())
        {
            String ser = CARD_SERDE.serialize(c);
            assertEquals(c, CARD_SERDE.deserialize(ser));
        }
    }

    @Test
    public void routeSerdeTest()
    {
        for(Route r : ChMap.routes())
        {
            String ser = ROUTE_SERDE.serialize(r);
            assertEquals(r, ROUTE_SERDE.deserialize(ser));
        }
    }

    @Test
    public void ticketSerdeTest()
    {
        for(Ticket t : ChMap.tickets())
        {
            String ser = TICKET_SERDE.serialize(t);
            assertEquals(t, TICKET_SERDE.deserialize(ser));
        }
    }

    @Test
    public void stringListSerdeTest()
    {
        int size = rng.nextInt(50) + 1;
        List<String> stringList = new ArrayList<>();
        String stringAlphabet = "abcdefghijklmnopqrstuvwxyz123456789";

        for(int i = 0; i < size; ++i)
        {
            stringList.add(generateRandomString(stringAlphabet));
        }

        String stringListSer = STRING_LIST_SERDE.serialize(stringList);
        assertEquals(stringList, STRING_LIST_SERDE.deserialize(stringListSer));
    }

    @Test
    public void cardListSerdeTest()
    {
        List<Card> cardList = generateRandomListOfCards(50);
        String cardListSer = CARD_LIST_SERDE.serialize(cardList);
        assertEquals(cardList, CARD_LIST_SERDE.deserialize(cardListSer));
    }

    @Test
    public void routeListSerdeTest()
    {
        int size = rng.nextInt(50) + 1;
        List<Route> routeList = new ArrayList<>();
        for(int i = 0; i < size; ++i)
        {
            routeList.add(ChMap.routes().get(rng.nextInt(ChMap.routes().size())));
        }

        String routeListSer = ROUTE_LIST_SERDE.serialize(routeList);
        assertEquals(routeList, ROUTE_LIST_SERDE.deserialize(routeListSer));
    }

    @Test
    public void cardBagSerdeTest()
    {
        SortedBag<Card> cardBag = SortedBag.of(generateRandomListOfCards(50));

        String cardBagSer = CARD_BAG_SERDE.serialize(cardBag);
        assertEquals(cardBag, CARD_BAG_SERDE.deserialize(cardBagSer));
    }

    @Test
    public void ticketBagSerdeTest()
    {
        int size = rng.nextInt(50);
        List<Ticket> ticketList = new ArrayList<>();
        for(int i = 0; i < size; ++i)
        {
            ticketList.add(ChMap.tickets().get(rng.nextInt(ChMap.tickets().size())));
        }
        SortedBag<Ticket> ticketBag = SortedBag.of(ticketList);

        String ticketBagSer = TICKET_BAG_SERDE.serialize(ticketBag);
        assertEquals(ticketBag, TICKET_BAG_SERDE.deserialize(ticketBagSer));
    }

    @Test
    public void cardBagListSerdeTest()
    {
        int listSize = rng.nextInt(50) + 1;
        List<SortedBag<Card>> cardBagList = new ArrayList<>();
        for(int j = 0; j < listSize; ++j)
        {
            SortedBag<Card> cardBag = SortedBag.of(generateRandomListOfCards(50));
            cardBagList.add(cardBag);
        }

        String cardBagListSer = CARD_BAG_LIST_SERDE.serialize(cardBagList);
        assertEquals(cardBagList, CARD_BAG_LIST_SERDE.deserialize(cardBagListSer));
    }

    @Test
    public void publicCardStateSerdeTest()
    {
        List<Card> faceUpCards1 = List.of(Card.BLACK, Card.BLUE, Card.YELLOW, Card.LOCOMOTIVE, Card.RED);
        List<Card> faceUpCards2 = List.of(Card.YELLOW, Card.GREEN, Card.YELLOW, Card.BLUE, Card.RED);
        List<Card> faceUpCards3 = List.of(Card.BLACK, Card.VIOLET, Card.YELLOW, Card.LOCOMOTIVE, Card.RED);
        List<Card> faceUpCards4 = List.of(Card.GREEN, Card.BLUE, Card.YELLOW, Card.RED, Card.RED);

        PublicCardState state1 = new PublicCardState(faceUpCards1, 12, 15);
        PublicCardState state2 = new PublicCardState(faceUpCards2, 14, 2);
        PublicCardState state3 = new PublicCardState(faceUpCards3, 7, 5);
        PublicCardState state4 = new PublicCardState(faceUpCards4, 3, 11);

        String state1Ser = PUBLIC_CARD_STATE_SERDE.serialize(state1);
        String state2Ser = PUBLIC_CARD_STATE_SERDE.serialize(state2);
        String state3Ser = PUBLIC_CARD_STATE_SERDE.serialize(state3);
        String state4Ser = PUBLIC_CARD_STATE_SERDE.serialize(state4);

        assertTrue(publicCardStateEqual(state1, PUBLIC_CARD_STATE_SERDE.deserialize(state1Ser)));
        assertTrue(publicCardStateEqual(state2, PUBLIC_CARD_STATE_SERDE.deserialize(state2Ser)));
        assertTrue(publicCardStateEqual(state3, PUBLIC_CARD_STATE_SERDE.deserialize(state3Ser)));
        assertTrue(publicCardStateEqual(state4, PUBLIC_CARD_STATE_SERDE.deserialize(state4Ser)));
    }

    @Test
    public void publicPlayerStateSerdeTest()
    {
        PublicPlayerState state1 = new PublicPlayerState(2, 6, ChMap.routes().subList(2,5));
        PublicPlayerState state2 = new PublicPlayerState(1, 12, ChMap.routes().subList(12,19));
        PublicPlayerState state3 = new PublicPlayerState(3, 5, ChMap.routes().subList(3,7));
        PublicPlayerState state4 = new PublicPlayerState(7, 3, ChMap.routes().subList(32,51));

        String state1Ser = PUBLIC_PLAYER_STATE_SERDE.serialize(state1);
        String state2Ser = PUBLIC_PLAYER_STATE_SERDE.serialize(state2);
        String state3Ser = PUBLIC_PLAYER_STATE_SERDE.serialize(state3);
        String state4Ser = PUBLIC_PLAYER_STATE_SERDE.serialize(state4);

        assertTrue(publicPlayerStateEqual(state1, PUBLIC_PLAYER_STATE_SERDE.deserialize(state1Ser)));
        assertTrue(publicPlayerStateEqual(state2, PUBLIC_PLAYER_STATE_SERDE.deserialize(state2Ser)));
        assertTrue(publicPlayerStateEqual(state3, PUBLIC_PLAYER_STATE_SERDE.deserialize(state3Ser)));
        assertTrue(publicPlayerStateEqual(state4, PUBLIC_PLAYER_STATE_SERDE.deserialize(state4Ser)));
    }

    @Test
    public void playerStateSerdeTest()
    {
        PlayerState state1 = PlayerState.initial(SortedBag.of(generateListOfRandomCards(4)));
        PlayerState state2 = PlayerState.initial(SortedBag.of(generateListOfRandomCards(4)));
        PlayerState state3 = PlayerState.initial(SortedBag.of(generateListOfRandomCards(4)));
        PlayerState state4 = PlayerState.initial(SortedBag.of(generateListOfRandomCards(4)));


        state2 = state2.withClaimedRoute(ChMap.routes().get(12), SortedBag.of());
        state2 = state2.withClaimedRoute(ChMap.routes().get(15), SortedBag.of());
        state2 = state2.withClaimedRoute(ChMap.routes().get(39), SortedBag.of());

        state3 = state3.withClaimedRoute(ChMap.routes().get(9), SortedBag.of());
        state3 = state3.withClaimedRoute(ChMap.routes().get(21), SortedBag.of());
        state3 = state3.withClaimedRoute(ChMap.routes().get(27), SortedBag.of());

        state4 = state4.withClaimedRoute(ChMap.routes().get(3), SortedBag.of());
        state4 = state4.withClaimedRoute(ChMap.routes().get(14), SortedBag.of());

        String state1Ser = PLAYER_STATE_SERDE.serialize(state1);
        String state2Ser = PLAYER_STATE_SERDE.serialize(state2);
        String state3Ser = PLAYER_STATE_SERDE.serialize(state3);
        String state4Ser = PLAYER_STATE_SERDE.serialize(state4);

        assertTrue(playerStateEqual(state1, PLAYER_STATE_SERDE.deserialize(state1Ser)));
        assertTrue(playerStateEqual(state2, PLAYER_STATE_SERDE.deserialize(state2Ser)));
        assertTrue(playerStateEqual(state3, PLAYER_STATE_SERDE.deserialize(state3Ser)));
        assertTrue(playerStateEqual(state4, PLAYER_STATE_SERDE.deserialize(state4Ser)));

    }

    @Test
    public void publicGameStateSerdeTest()
    {
        List<Card> faceUpCards1 = List.of(Card.BLACK, Card.BLUE, Card.YELLOW, Card.LOCOMOTIVE, Card.RED);
        List<Card> faceUpCards2 = List.of(Card.YELLOW, Card.GREEN, Card.YELLOW, Card.BLUE, Card.RED);
        List<Card> faceUpCards3 = List.of(Card.BLACK, Card.VIOLET, Card.YELLOW, Card.LOCOMOTIVE, Card.RED);
        List<Card> faceUpCards4 = List.of(Card.GREEN, Card.BLUE, Card.YELLOW, Card.RED, Card.RED);

        PublicCardState publicCardState1 = new PublicCardState(faceUpCards1, 12, 15);
        PublicCardState publicCardState2 = new PublicCardState(faceUpCards2, 14, 2);
        PublicCardState publicCardState3 = new PublicCardState(faceUpCards3, 7, 5);
        PublicCardState publicCardState4 = new PublicCardState(faceUpCards4, 3, 11);

        PublicPlayerState publicPlayer1State1 = new PublicPlayerState(2, 6, ChMap.routes().subList(2,5));
        PublicPlayerState publicPlayer1State2 = new PublicPlayerState(1, 12, ChMap.routes().subList(12,19));
        PublicPlayerState publicPlayer1State3 = new PublicPlayerState(3, 5, ChMap.routes().subList(3,7));
        PublicPlayerState publicPlayer1State4 = new PublicPlayerState(7, 3, ChMap.routes().subList(32,51));

        PublicPlayerState publicPlayer2State1 = new PublicPlayerState(7, 3, ChMap.routes().subList(32,51));
        PublicPlayerState publicPlayer2State2 = new PublicPlayerState(2, 6, ChMap.routes().subList(2,5));
        PublicPlayerState publicPlayer2State3 = new PublicPlayerState(1, 12, ChMap.routes().subList(12,19));
        PublicPlayerState publicPlayer2State4 = new PublicPlayerState(3, 5, ChMap.routes().subList(3,7));

        Map<PlayerId, PublicPlayerState> map1 = new HashMap<>();
        Map<PlayerId, PublicPlayerState> map2 = new HashMap<>();
        Map<PlayerId, PublicPlayerState> map3 = new HashMap<>();
        Map<PlayerId, PublicPlayerState> map4 = new HashMap<>();

        map1.put(PlayerId.PLAYER_1, publicPlayer1State1);
        map1.put(PlayerId.PLAYER_2, publicPlayer2State1);

        map2.put(PlayerId.PLAYER_1, publicPlayer1State2);
        map2.put(PlayerId.PLAYER_2, publicPlayer2State2);

        map3.put(PlayerId.PLAYER_1, publicPlayer1State3);
        map3.put(PlayerId.PLAYER_2, publicPlayer2State3);

        map4.put(PlayerId.PLAYER_1, publicPlayer1State4);
        map4.put(PlayerId.PLAYER_2, publicPlayer2State4);

        PublicGameState state1 = new PublicGameState(4, publicCardState1, PlayerId.PLAYER_1, map1, null);
        PublicGameState state2 = new PublicGameState(4, publicCardState2, PlayerId.PLAYER_2, map2, null);
        PublicGameState state3 = new PublicGameState(4, publicCardState3, PlayerId.PLAYER_2, map3, null);
        PublicGameState state4 = new PublicGameState(4, publicCardState4, PlayerId.PLAYER_1, map4, null);

        String state1Ser = PUBLIC_GAME_STATE_SERDE.serialize(state1);
        String state2Ser = PUBLIC_GAME_STATE_SERDE.serialize(state2);
        String state3Ser = PUBLIC_GAME_STATE_SERDE.serialize(state3);
        String state4Ser = PUBLIC_GAME_STATE_SERDE.serialize(state4);

        assertTrue(publicGameStateEqual(state1, PUBLIC_GAME_STATE_SERDE.deserialize(state1Ser)));
        assertTrue(publicGameStateEqual(state2, PUBLIC_GAME_STATE_SERDE.deserialize(state2Ser)));
        assertTrue(publicGameStateEqual(state3, PUBLIC_GAME_STATE_SERDE.deserialize(state3Ser)));
        assertTrue(publicGameStateEqual(state4, PUBLIC_GAME_STATE_SERDE.deserialize(state4Ser)));
    }

    // Method which generates a random string from the string alphabet given
    private String generateRandomString(String alphabet)
    {
        int length = rng.nextInt(50) + 1;

        StringBuilder out = new StringBuilder();
        for(int i = 0; i < length; ++i)
        {
            out.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        return out.toString();
    }

    private List<Card> generateRandomListOfCards(int bound)
    {
        int size = rng.nextInt(bound) + 1;
        List<Card> cardList = new ArrayList<>();
        for(int i = 0; i < size; ++i)
        {
            cardList.add(Card.values()[rng.nextInt(Card.values().length)]);
        }
        return cardList;
    }

    private List<Card> generateListOfRandomCards(int size)
    {
        List<Card> cardList = new ArrayList<>();
        for(int i = 0; i < size; ++i)
        {
            cardList.add(Card.values()[rng.nextInt(Card.values().length)]);
        }
        return cardList;
    }

    private boolean publicPlayerStateEqual(PublicPlayerState p1, PublicPlayerState p2)
    {
        boolean a = p1.carCount() == p2.carCount();
        boolean b = p1.routes().equals(p2.routes());
        boolean c = p1.cardCount() == p2.cardCount();
        boolean d = p1.ticketCount() == p2.ticketCount();
        return (a && b && c && d);
    }

    private boolean publicCardStateEqual(PublicCardState p1, PublicCardState p2)
    {
        boolean a = p1.isDeckEmpty() == p2.isDeckEmpty();
        boolean b = p1.discardsSize() == p2.discardsSize();
        boolean c = p1.deckSize() == p2.deckSize();
        boolean d = p1.faceUpCards().equals(p2.faceUpCards());
        return (a && b && c && d);
    }

    private boolean publicGameStateEqual(PublicGameState p1, PublicGameState p2)
    {
        boolean a = p1.canDrawTickets() == p2.canDrawTickets();
        boolean b = p1.canDrawCards() == p2.canDrawCards();
        boolean c = p1.currentPlayerId() == p2.currentPlayerId();
        boolean d = publicCardStateEqual(p1.cardState(), p2.cardState());
        boolean e = publicPlayerStateEqual(p1.currentPlayerState(), p2.currentPlayerState());
        boolean f = p1.claimedRoutes().equals(p2.claimedRoutes());
        boolean g = p1.lastPlayer() == p2.lastPlayer();
        boolean h = p1.ticketsCount() == p2.ticketsCount();
        return (a && b && c && d && e && f && g && h);
    }

    private boolean playerStateEqual(PlayerState p1, PlayerState p2)
    {
        boolean a = p1.cards().equals(p2.cards());
        boolean b = p1.ticketPoints() == p2.ticketPoints();
        boolean c = p1.tickets().equals(p2.tickets());
        boolean d = p1.finalPoints() == p2.finalPoints();
        boolean e = publicPlayerStateEqual(p1, p2);
        return (a && b && c && d && e);
    }

}
