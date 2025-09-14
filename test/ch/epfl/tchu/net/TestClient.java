package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names + "\n");
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println("Recieve info: " + info + "\n");
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println("New state:");
            System.out.println("\tPublicGameState:");
            System.out.println("\t\tTickets: "+newState.ticketsCount());
            System.out.println("\t\tCurrent player id: "+newState.currentPlayerId());
            System.out.println("\t\tLast player: "+newState.lastPlayer());
            System.out.println("\t\tClaimed routes: "+newState.claimedRoutes());
            System.out.println("\t\tAmount of cards: "+newState.cardState().deckSize() + "\n");
            System.out.println("\tPlayerState:");
            System.out.println("\t\tCards: " + ownState.cards());
            System.out.println("\t\tTickets: " + ownState.tickets());
            System.out.println("\t\tRoutes: " + ownState.routes() + "\n");
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println("Initial ticket choice: "+tickets + "\n");
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            List<Ticket> tickets = ChMap.tickets().subList(2,5);
            System.out.println("Chosen initial tickets: " + SortedBag.of(tickets) + "\n");
            return SortedBag.of(tickets);
        }

        @Override
        public TurnKind nextTurn() {
            Random rng = new Random();
            TurnKind tK = TurnKind.ALL.get(rng.nextInt(TurnKind.ALL.size()));
            System.out.println("Turn kind: " + tK + "\n");
            return tK;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            Random rng = new Random();
            SortedBag<Ticket> chosenTickets = SortedBag.of(options.toList().subList(0, rng.nextInt(options.size() - 1) + 1));
            System.out.println("Chosen tickets: " + chosenTickets + "\n");
            return chosenTickets;
        }

        @Override
        public int drawSlot() {
            Random rng = new Random();
            int slot = rng.nextInt(5);
            System.out.println("Chosen draw slot: " + slot + "\n");
            return slot;
        }

        @Override
        public Route claimedRoute() {
            Random rng = new Random();
            Route route = ChMap.routes().get(rng.nextInt(ChMap.routes().size()));
            System.out.println("Chosen route: " + route + "\n");
            return route;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            List<Card> cards = List.of(Card.GREEN, Card.GREEN, Card.VIOLET, Card.BLUE, Card.LOCOMOTIVE);
            System.out.println("Initial claim cards: " + SortedBag.of(cards) + "\n");
            return SortedBag.of(cards);
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            Random rng = new Random();
            SortedBag<Card> choice = options.get(rng.nextInt(options.size()));
            System.out.println("Chosen additional cards: " + choice + "\n");
            return choice;
        }
    }
}
