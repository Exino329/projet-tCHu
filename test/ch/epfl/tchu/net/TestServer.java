package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");

            // InitPlayers
            playerProxy.initPlayers(PLAYER_1, playerNames);

            Thread.sleep(1000);

            // RecieveInfo
            playerProxy.receiveInfo("Hello there!");

            Thread.sleep(1000);

            // UpdateState
            PublicCardState cardState = new PublicCardState(List.of(Card.WHITE, Card.RED, Card.VIOLET, Card.YELLOW, Card.LOCOMOTIVE), 15, 4);
            PublicPlayerState playerState1 = new PublicPlayerState(2, 5, ChMap.routes().subList(43, 45));
            PublicPlayerState playerState2 = new PublicPlayerState(3, 7, ChMap.routes().subList(21, 24));
            Map<PlayerId, PublicPlayerState> players = new TreeMap<>();
            players.put(PLAYER_1, playerState1);
            players.put(PLAYER_2, playerState2);
            PublicGameState gameState = new PublicGameState(3, cardState, PLAYER_1, players, null);
            List<Ticket> tickets = ChMap.tickets().subList(3,5);
            List<Card> cards = List.of(Card.YELLOW, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE);
            PlayerState playerState = new PlayerState(SortedBag.of(tickets), SortedBag.of(cards), ChMap.routes().subList(43, 45));

            playerProxy.updateState(gameState, playerState);

            Thread.sleep(1000);

            // SetInitialTicketChoice
            tickets = ChMap.tickets().subList(2, 5);
            playerProxy.setInitialTicketChoice(SortedBag.of(tickets));

            Thread.sleep(1000);

            // ChooseInitialTickets
            SortedBag<Ticket> ticketBag = playerProxy.chooseInitialTickets();
            System.out.println("Recieved ticket bag: " + ticketBag + "\n");

            Thread.sleep(1000);

            // NextTurn
            Player.TurnKind tK = playerProxy.nextTurn();
            System.out.println("Recieved turn kind: " + tK + "\n");

            Thread.sleep(1000);

            // ChooseTickets
            List<Ticket> ticketsToChoose = ChMap.tickets().subList(5, 8);
            System.out.println("Sent ticket options: "+SortedBag.of(ticketsToChoose));
            SortedBag<Ticket> chosenTickets = playerProxy.chooseTickets(SortedBag.of(ticketsToChoose));
            System.out.println("Recieved chosen tickets: "+ chosenTickets + "\n");

            Thread.sleep(1000);

            // DrawSlot
            int slot = playerProxy.drawSlot();
            System.out.println("Recieved draw slot: " + slot + "\n");

            Thread.sleep(1000);

            // ClaimedRoute
            Route route = playerProxy.claimedRoute();
            System.out.println("Recieved route: " + route + "\n");

            Thread.sleep(1000);

            // InitialClaimCards
            SortedBag<Card> claimCards = playerProxy.initialClaimCards();
            System.out.println("Recieved claim cards: " + claimCards + "\n");

            Thread.sleep(1000);

            // ChooseAdditionalCards
            List<Card> additionalCards1 = List.of(Card.YELLOW, Card.YELLOW);
            List<Card> additionalCards2 = List.of(Card.YELLOW, Card.LOCOMOTIVE);
            List<Card> additionalCards3 = List.of(Card.LOCOMOTIVE, Card.LOCOMOTIVE);
            List<SortedBag<Card>> options = List.of(SortedBag.of(additionalCards1), SortedBag.of(additionalCards2), SortedBag.of(additionalCards3));
            System.out.println("Sent options: " + options);
            SortedBag<Card> choice = playerProxy.chooseAdditionalCards(options);
            System.out.println("Recieved choice: " + choice + "\n");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server done!\n");
    }
}
