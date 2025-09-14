package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * Graphical player class which manages interactions between the player and the visual part of the game (windows,
 * dialogs, etc).
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class GraphicalPlayer {

    private final PlayerId ownId; // Own id
    private final Map<PlayerId, String> nameMap; // Map with player id - player name string correspondences

    private final int MAX_INFO_LINES = 5; // Maximum amount of lines to be shown in the info feed

    private final ObservableGameState observableGameState; // Observable game state
    private final ObservableList<Text> infoStream; // Info "stream" containing the last 5 information messages received

    // Handler properties for the different actions to be taken during the players' turn
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandlerObjectProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ActionHandlers.DrawCardHandler> cardHandlerObjectProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerObjectProperty = new SimpleObjectProperty<>();

    private Stage stage; // Main window

    /**
     * Graphical player constructor
     *
     * @param ownId   own player id
     * @param nameMap map with player ids and corresponding player name strings
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> nameMap) {
        assert isFxApplicationThread();

        this.ownId = ownId;
        this.nameMap = nameMap;

        this.observableGameState = new ObservableGameState(ownId);
        this.infoStream = FXCollections.observableArrayList();
    }

    /**
     * Method to update the graphical view of the game, according to the games' public state and own private state
     *
     * @param gameState   public game state
     * @param playerState own player state
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        assert isFxApplicationThread();

        // If the stage has not yet been created
        if (stage == null) {
            // Create the stage and set its properties
            stage = new Stage();

            stage.titleProperty().set(String.join(" \u2014 ", "tCHu", nameMap.get(ownId)));

            BorderPane container = new BorderPane();
            Scene scene = new Scene(container);

            // Create the map view
            container.setCenter(MapViewCreator.createMapView(observableGameState,
                    claimRouteHandlerObjectProperty,
                    this::chooseClaimCards));

            // Create the cards view
            container.setRight(DecksViewCreator.createCardsView(observableGameState,
                    ticketsHandlerObjectProperty,
                    cardHandlerObjectProperty));

            // Create the hand view
            container.setBottom(DecksViewCreator.createHandView(observableGameState));

            // Update the info view
            container.setLeft(InfoViewCreator.createInfoView(ownId, nameMap, observableGameState, infoStream));

            // Set the scene
            stage.setScene(scene);
            stage.show();
        }

        // Update the current state of the game
        observableGameState.setState(gameState, playerState);
    }

    /**
     * Method to create the info list given to the InfoViewCreator
     *
     * @param message latest info message to show the player
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();

        List<Text> newInfoStream = new ArrayList<>(infoStream);

        // If the max lines have not yet been reached
        if (newInfoStream.size() < MAX_INFO_LINES) {
            newInfoStream.add(new Text(message));
        } else {
            // Otherwise, add in the new info and rotate the list
            newInfoStream.set(0, new Text(message));
            Collections.rotate(newInfoStream, -1);
        }

        infoStream.setAll(newInfoStream);
    }

    /**
     * Method which sets the handler properties on the beginning of the turn
     *
     * @param ticketsHandler ticket drawing handler
     * @param cardsHandler   card drawing handler
     * @param routeHandler   route claiming handler
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler ticketsHandler, ActionHandlers.DrawCardHandler cardsHandler, ActionHandlers.ClaimRouteHandler routeHandler) {
        assert isFxApplicationThread();

        // Set the handlers for the player to use for the different actions
        ticketsHandlerObjectProperty.set((observableGameState.canDrawTickets()) ? () -> {
            ticketsHandler.onDrawTickets();
            emptyHandlerProperties();
        } : null);
        cardHandlerObjectProperty.set((observableGameState.canDrawCards()) ? (slot) -> {
            cardsHandler.onDrawCard(slot);
            emptyHandlerProperties();
        } : null);
        claimRouteHandlerObjectProperty.set((route, cardBag) -> {
            routeHandler.onClaimRoute(route, cardBag);
            emptyHandlerProperties();
        });

        // Also bring the game window to the front
        stage.toFront();
    }

    /**
     * Ticket choosing method, which updates the handlers as well as opening the dialog window
     *
     * @param tickets the tickets from which the player may choose
     * @param handler the tickets drawing handler
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ActionHandlers.ChooseTicketsHandler handler) {
        assert isFxApplicationThread();

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        int ticketAmount = tickets.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        openDialog(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, Integer.toString(ticketAmount), StringsFr.plural(ticketAmount)),
                listView,
                mouseEvent -> handler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems())), ticketAmount);
    }

    /**
     * Method called when the player draws a card for the second time as to refill the handlers
     *
     * @param handler the card draw handler
     */
    public void drawCard(ActionHandlers.DrawCardHandler handler) {
        assert isFxApplicationThread();

        ticketsHandlerObjectProperty.set(null);
        cardHandlerObjectProperty.set((slot) -> {
            handler.onDrawCard(slot);
            emptyHandlerProperties();
        });
        claimRouteHandlerObjectProperty.set(null);
    }

    /**
     * Method called when the player must choose a set of cards to claim a route
     *
     * @param options the options the player may choose
     * @param handler the card choosing handler property
     */
    public void chooseClaimCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(options));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        if (options.size() == 1) {
            handler.onChooseCards(options.get(0));
        } else {
            openDialog(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, listView, mouseEvent -> {
                SortedBag<Card> choice = listView.getSelectionModel().getSelectedItem();
                handler.onChooseCards(choice);
            }, 1);
        }
    }

    /**
     * Method called to let the player choose the additional cards he wants to play to take possession of a tunnel
     *
     * @param options the options given to the player
     * @param handler the card chooser handler property
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(options));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        openDialog(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, listView, mouseEvent -> {
            SortedBag<Card> choice = listView.getSelectionModel().getSelectedItem();
            if (choice == null) {
                // If no cards were chosen, send back an empty bag
                choice = SortedBag.of();
            }
            handler.onChooseCards(choice);
        }, 0);
    }

    /**
     * Internal method which sets all handlers to null, preventing any further actions by the player
     */
    private void emptyHandlerProperties() {
        assert isFxApplicationThread();

        ticketsHandlerObjectProperty.set(null);
        cardHandlerObjectProperty.set(null);
        claimRouteHandlerObjectProperty.set(null);
    }

    /**
     * Internal class which is used to transform card bags into properly formatted strings
     */
    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        /**
         * This method is used to show the textual representation of the cards in the information view
         *
         * @param cards are the cards to convert in textual representation
         * @return the string that contains textual representation of the sorted bag of cards
         */
        @Override
        public String toString(SortedBag<Card> cards) {
            assert isFxApplicationThread();

            List<String> cardStrings = new ArrayList<>();
            for (Card c : cards.toSet()) {
                int count = cards.countOf(c);
                cardStrings.add(String.join(" ", Integer.toString(count), Info.cardName(c, count)));
            }
            return String.join(StringsFr.AND_SEPARATOR, cardStrings);
        }

        /**
         * Unused method
         *
         * @param s is string given in argument
         * @throws UnsupportedOperationException in any case
         */
        @Override
        public SortedBag<Card> fromString(String s) {
            assert isFxApplicationThread();
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Internal method which opens a dialog window with a given title, message, choice box, handler and minimum number of
     * elements to be selected to be able to close the window.
     *
     * @param title       window title
     * @param message     message shown at the top of the multiple choice box
     * @param options     the options given to the player
     * @param handler     the MouseEvent handler executed on the press of the button
     * @param minElements minimum number of elements which must be chosen
     */
    private static void openDialog(String title, String message, ListView options, EventHandler<MouseEvent> handler, int minElements) {
        assert isFxApplicationThread();

        // Create the stage and vbox
        Stage stage = new Stage();
        VBox carrier = new VBox();

        // Create the text element
        TextFlow textFlow = new TextFlow();
        Text text = new Text();
        text.textProperty().set(message);
        textFlow.getChildren().add(text);

        // Integer property which contains the number of elements selected in the dialog box
        IntegerProperty chosenOptionsProperty = new SimpleIntegerProperty(options.getSelectionModel().getSelectedItems().size());
        options.getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> chosenOptionsProperty.set(change.getList().size()));

        // Create the button
        Button button = new Button();
        button.setText(StringsFr.CHOOSE);
        button.setOnMouseClicked(MouseEvent -> {
            handler.handle(MouseEvent); // Handle the mouse event
            stage.hide(); // Hide the window
        });

        // Disables the choose button if not enough elements have been selected
        button.disableProperty().bind(chosenOptionsProperty.lessThan(minElements));

        // Wrap up and display the dialog box
        carrier.getChildren().addAll(textFlow, options, button);
        Scene scene = new Scene(carrier);
        scene.getStylesheets().add("chooser.css");
        stage.titleProperty().set(title);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true); // Push the dialog to the front
        stage.setOnCloseRequest(Event::consume); // Prevent closing the dialog box
        stage.show();
    }

}
