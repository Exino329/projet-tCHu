package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

import static ch.epfl.tchu.Preconditions.checkArgument;
import static ch.epfl.tchu.game.Constants.*;

/**
 * Class Game represents the progress of an entire game session
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Game {

    /**
     * private constructor without parameters to make this class non instantiable
     */
    private Game(){}

    /**
     * Internal method which gives a given info to both players
     * @param info is the information to communicate to both players
     * @param players are the two players that receive the information
     */
    private static void giveInfoToPlayers(String info, Map<PlayerId, Player> players){
        PlayerId.ALL.forEach((playerID) -> players.get(playerID).receiveInfo(info));
    }

    /**
     * This method updates the state of both players
     * @param actualGameState actual state of the game
     * @param players represents each player in function of his id
     */
    private static void updateStateForPlayers(GameState actualGameState, Map<PlayerId, Player> players){
        PlayerId.ALL.forEach((playerId) -> players.get(playerId).updateState(actualGameState, actualGameState.playerState(playerId)));
    }

    /**
     * Internal method to update the state when a player claims a route
     * @param state current game state
     * @param info info message
     * @param players player map
     * @param route route that the player claimed
     * @param cards cards used
     * @param discard cards to discard (if there are any)
     * @return new game state
     */
    private static GameState didClaimRoute(GameState state, Info info, Map<PlayerId, Player> players, Route route, SortedBag<Card> cards, SortedBag<Card> discard)
    {
        if(!discard.isEmpty())
        {
            state = state.withMoreDiscardedCards(discard);
        }
        state = state.withClaimedRoute(route, cards);
        giveInfoToPlayers(info.claimedRoute(route, cards), players);
        return state;
    }

    /**
     * Internal method to update the state when a player fails to claim a route
     * @param state current game state
     * @param info info message
     * @param players player map
     * @param route route that the player did not claim
     * @param discard cards to discard (if there are any)
     * @return new game state
     */
    private static GameState didNotClaimRoute(GameState state, Info info, Map<PlayerId, Player> players, Route route, SortedBag<Card> discard)
    {
        state = state.withMoreDiscardedCards(discard);
        giveInfoToPlayers(info.didNotClaimRoute(route), players);
        return state;
    }

    /**
     * This method represents all the progress of a game, in the first part of the method,
     * players receive their tickets because and the game starts. Then the turns occurs until
     * the game end. After the loop , the method compute the final points of the players
     * @param players represents each player in function of his id
     * @param playerNames represents each player's name in function of his id
     * @param tickets represents the set of tickets we play with
     * @param rng is a random generator
     * @throws IllegalArgumentException if one of the two map or both doesn't/don't contain the same number of elements as there are ids
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

        /*=====================================================================
                              Initialisation of the game :
         =====================================================================*/

        checkArgument(players.size() == PlayerId.COUNT);
        checkArgument(playerNames.size() == PlayerId.COUNT);

        // creation of the initial state of the game
        GameState gameState = GameState.initial(tickets, rng);

        //communicates to each player their own identity and the name of the players of the game
        for (PlayerId playerId : PlayerId.ALL) {
            players.get(playerId).initPlayers(playerId, playerNames);
        }

        // informs to the players which player starts to play
        Info info = new Info(playerNames.get(gameState.currentPlayerId()));
        giveInfoToPlayers(info.willPlayFirst(), players);


        // communicates to the players the tickets that they chose and modify the state of the game accordingly
        for (PlayerId playerId : PlayerId.ALL) {
            players.get(playerId).setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
        }

        // update the state for both players
        updateStateForPlayers(gameState, players);

        // list that contains the number of tickets chosen by the players
        List<Integer> listNumberOfTickets = new ArrayList<>();

        // update the state of the game where players have chosen their tickets
        for (PlayerId playerId : PlayerId.ALL) {
            SortedBag<Ticket> chosenTickets = players.get(playerId).chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(playerId, chosenTickets);
            listNumberOfTickets.add(chosenTickets.size());
        }

        for (PlayerId playerId : PlayerId.ALL) {
            Info playerIdChosenTickets = new Info(playerNames.get(playerId));
            giveInfoToPlayers(playerIdChosenTickets.keptTickets(listNumberOfTickets.get(playerId.ordinal())), players);
        }


        /*=====================================================================
                                 progress of the game :
         =====================================================================*/

        // the game continues as long as the last player is not the current player
        do {

            //inform both players of who is playing and update the state of the players for the current turn
            info = new Info(playerNames.get(gameState.currentPlayerId()));
            giveInfoToPlayers(info.canPlay(), players);
            updateStateForPlayers(gameState, players);

            //definition of the current player of the turn
            Player currentPlayer = players.get(gameState.currentPlayerId());

            // Handle the current turn
            Player.TurnKind currentPlayerTurn = currentPlayer.nextTurn();

            switch (currentPlayerTurn) {
                /* ===== The player chooses to draw tickets ===== */
                case DRAW_TICKETS:

                    // the player chose the tickets and the state of the game is updated
                    giveInfoToPlayers(info.drewTickets(IN_GAME_TICKETS_COUNT), players);

                    SortedBag<Ticket> ticketsChosenByCurrentPlayer = currentPlayer.chooseTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT));

                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT), ticketsChosenByCurrentPlayer);

                    // Give the information to both player that the current player has drew
                    giveInfoToPlayers(info.keptTickets(ticketsChosenByCurrentPlayer.size()), players);
                    break;

                /* ===== The player chooses to draw cards ===== */
                case DRAW_CARDS:

                    for (int i = 0; i < 2; ++i) {
                        // if the decks is empty, we have to recreate the deck with the discard
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        // update the state of the players before the current player draw the second card at the top of the deck
                        if (i == 1) {
                            updateStateForPlayers(gameState, players);
                        }

                        int drawSlot = currentPlayer.drawSlot();

                        // the player draw the card at the top of the deck
                        if (drawSlot == DECK_SLOT) {
                            // update the state of the game (the player has draw the card at the top of the deck and we give this information to both players
                            gameState = gameState.withBlindlyDrawnCard();
                            giveInfoToPlayers(info.drewBlindCard(), players);
                        }

                        // if the player hasn't draw the card at the top of the deck, it means that he has draw one of the 5 faceUpCards
                        else {
                            // give the information to both players that the current player has draw one of the 5 faceUpCards
                            giveInfoToPlayers(info.drewVisibleCard(gameState.cardState().faceUpCard(drawSlot)), players);
                            gameState = gameState.withDrawnFaceUpCard(drawSlot);

                        }
                    }
                    break;

                /*=== The player chooses to try to claim a route =====*/
                case CLAIM_ROUTE:

                    Route routeThatPlayerTryToClaim = currentPlayer.claimedRoute(); // The chosen route
                    SortedBag<Card> cardsOfThePlayerForTryingToClaimRoute = currentPlayer.initialClaimCards(); // The chosen cards

                    if (routeThatPlayerTryToClaim.level() == Route.Level.UNDERGROUND) {

                        // Informs the players that the current player tries to attempt a tunnel
                        giveInfoToPlayers(info.attemptsTunnelClaim(routeThatPlayerTryToClaim, cardsOfThePlayerForTryingToClaimRoute), players);

                        // Additional cards pulled from deck
                        SortedBag.Builder<Card> additionalCards = new SortedBag.Builder<>();

                        for (int i = 0; i < ADDITIONAL_TUNNEL_CARDS; ++i) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            additionalCards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        // Number of additional cards needed to claim tunnel, accord to top 3 cards of the deck
                        int additionalClaimCards = routeThatPlayerTryToClaim.additionalClaimCardsCount(cardsOfThePlayerForTryingToClaimRoute,
                                additionalCards.build());

                        giveInfoToPlayers(info.drewAdditionalCards(additionalCards.build(), additionalClaimCards), players);

                        if (additionalClaimCards > 0) {
                            // Combinations of cards the player can use to claim the tunnel
                            List<SortedBag<Card>> playableAdditionalCards = gameState.currentPlayerState()
                                    .possibleAdditionalCards(additionalClaimCards, cardsOfThePlayerForTryingToClaimRoute);

                            if (!playableAdditionalCards.isEmpty()) {
                                // The cards the player has chosen
                                SortedBag<Card> chosenAdditionalCards = currentPlayer.chooseAdditionalCards(playableAdditionalCards);

                                if(chosenAdditionalCards.isEmpty())
                                {
                                    // If no cards are chosen (the player does not want to claim the route), the
                                    // cards are returned to the discard
                                    gameState = didNotClaimRoute(
                                            gameState,
                                            info,
                                            players,
                                            routeThatPlayerTryToClaim,
                                            additionalCards.build()
                                    );
                                }
                                else
                                {
                                    // Otherwise, the player directly claims the route
                                    gameState = didClaimRoute(gameState,
                                            info,
                                            players,
                                            routeThatPlayerTryToClaim,
                                            cardsOfThePlayerForTryingToClaimRoute.union(chosenAdditionalCards),
                                            additionalCards.build());
                                }
                            } else {
                                // If the player does not have the required additional cards to claim the tunnel
                                gameState = didNotClaimRoute(
                                        gameState,
                                        info,
                                        players,
                                        routeThatPlayerTryToClaim,
                                        additionalCards.build()
                                );
                            }
                        } else {
                            // With no additional required cards, the player directly claims the route
                            gameState = didClaimRoute(
                                    gameState,
                                    info,
                                    players,
                                    routeThatPlayerTryToClaim,
                                    cardsOfThePlayerForTryingToClaimRoute,
                                    additionalCards.build());
                        }
                    }
                    else {
                        // He directly claims the route
                        gameState = didClaimRoute(
                                gameState,
                                info,
                                players,
                                routeThatPlayerTryToClaim,
                                cardsOfThePlayerForTryingToClaimRoute,
                                SortedBag.of()
                        );
                    }
                    break;
            }

            // break while loop when the last player is the current player
            if (gameState.currentPlayerId() == gameState.lastPlayer()) {
                break;
            }

            // if the last turn begins, then we give the information to both players
            if (gameState.lastTurnBegins()) {
                giveInfoToPlayers(info.lastTurnBegins(gameState.currentPlayerState().carCount()), players);
            }

            // the next turn starts
            gameState = gameState.forNextTurn();
        }
        while (true);

        /*=====================================================================
                                   End of the game :
         =====================================================================*/

        // Update the state for both players to end the game
        updateStateForPlayers(gameState, players);

        // list that contains the total points for each player;
        List<Integer> playerTotalPoints = new ArrayList<>();

        // this variable will stock the length of the longest trail
        int playerLongestTrailLength = 0;

        // this loop compute the points for each players and give the value of the longest trail
        for (PlayerId playerId : PlayerId.ALL) {
            playerTotalPoints.add(gameState.playerState(playerId).finalPoints());
            if (Trail.longest(gameState.playerState(playerId).routes()).length() > playerLongestTrailLength) {
                playerLongestTrailLength = Trail.longest(gameState.playerState(playerId).routes()).length();
            }
        }

        for (PlayerId playerId : PlayerId.values()) {
            if (playerLongestTrailLength == Trail.longest(gameState.playerState(playerId).routes()).length()){
                // give the longest trail bonus to the player
                playerTotalPoints.set(playerId.ordinal(), playerTotalPoints.get(playerId.ordinal()) + LONGEST_TRAIL_BONUS_POINTS);

                info = new Info(playerNames.get(playerId));
                giveInfoToPlayers(info.getsLongestTrailBonus(Trail.longest(gameState.playerState(playerId).routes())), players);
            }
        }

        // list of the player names
        List<String> listOfPlayers = new ArrayList<>();

        for (PlayerId playerId : PlayerId.values()) {
            if (playerTotalPoints.get(playerId.ordinal()).equals(Collections.max(playerTotalPoints))) {
                listOfPlayers.add(playerNames.get(playerId));
            }
        }

        // if the list of players is equal to 1 , it means that there is only one player that has the maximum of points so there's no draw
        if (listOfPlayers.size() == 1) {
            // sort the list by descending order
            playerTotalPoints.sort(Collections.reverseOrder());
            giveInfoToPlayers(info.won(playerTotalPoints.get(0), playerTotalPoints.get(1)), players);
        } else {
            giveInfoToPlayers(Info.draw(listOfPlayers, Collections.max(playerTotalPoints)), players);
        }

        // Update the state for both players to end the game
        updateStateForPlayers(gameState, players);
    }
}

