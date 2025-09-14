package ch.epfl.tchu.gui;


import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

import static ch.epfl.tchu.gui.StringsFr.CARDS;
import static ch.epfl.tchu.gui.StringsFr.TICKETS;

/**
 * DecksViewCreator class, which generates :
 * - the view of the hand of the player
 * - the view of the ticket's deck and card's deck (that are represented by a button)
 * - the view of the 5 face up cards
 * - the view of the tickets that the player owns
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
final class DecksViewCreator {

    private static final int WIDTH_RECTANGLE_1 = 40;
    private static final int WIDTH_RECTANGLE_2 = 60;
    private static final int WIDTH_RECTANGLE_3 = 50;

    private static final int HEIGHT_RECTANGLE_1 = 70;
    private static final int HEIGHT_RECTANGLE_2 = 90;
    private static final int HEIGHT_RECTANGLE_3 = 5;


    private static final String NEUTRAL = "NEUTRAL";


    /**
     * Private constructor of DecksViewCreator without parameter to make this class not instantiable
     */
    private DecksViewCreator() {
    }

    /**
     * Private method which constructs the rectangles for the visual representation of the cards
     *
     * @return a list with the rectangles that represent a card
     */
    private static List<Rectangle> cardsRectangles() {
        Rectangle outsideRectangle = new Rectangle(WIDTH_RECTANGLE_2, HEIGHT_RECTANGLE_2);
        Rectangle insideRectangle = new Rectangle(WIDTH_RECTANGLE_1, HEIGHT_RECTANGLE_1);
        Rectangle trainRectangle = new Rectangle(WIDTH_RECTANGLE_1, HEIGHT_RECTANGLE_1);

        outsideRectangle.getStyleClass().add("outside");
        insideRectangle.getStyleClass().addAll("filled", "inside");
        trainRectangle.getStyleClass().add("train-image");

        return List.of(outsideRectangle, insideRectangle, trainRectangle);
    }

    /**
     * Method which creates the hand view
     *
     * @param observableGameState is the observable game state with all the needed properties to refresh visual elements
     * @return Node element with all the visuals of the hand view (tickets and cards) baked in
     */
    public static HBox createHandView(ObservableGameState observableGameState) {

        // root of the scene graph
        HBox root = new HBox();
        root.getStylesheets().addAll("decks.css", "colors.css");

        // creation of the view of the tickets
        ObservableList<Ticket> ticket = observableGameState.getOwnTickets();
        ListView<Ticket> listTickets = new ListView<>(ticket);
        listTickets.setId("tickets");

        // listTickets is a children of the root
        root.getChildren().add(listTickets);

        // this node will be the parent of the 9 different types of cards
        HBox hBoxChildren = new HBox();
        hBoxChildren.setId("hand-pane");

        // we iterate on all the different types of card to create a node (each card is a children node of hBoxChildren)
        for (Card card : Card.values()) {

            // property of the card number of the given type
            ReadOnlyIntegerProperty count = observableGameState.getOwnCardCount(card);

            // this node is the parent of the 3 rectangles used to represent the card
            StackPane cardNode = new StackPane();
            cardNode.getStyleClass().add((card == Card.LOCOMOTIVE) ? NEUTRAL : card.color().toString());
            cardNode.getStyleClass().add("card");

            // graphic representation of the card is displayed only if the player has at least one
            cardNode.visibleProperty().bind(Bindings.greaterThan(count, 0));

            // this node is the visual representation of the number of cards
            Text text = new Text();
            text.textProperty().bind(Bindings.convert(count));

            // graphical representation showing the number of identical type of card the player has in the form of a counter
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            text.getStyleClass().add("count");

            // we add the 3 children (instances of rectangles) to the parent node (card)
            cardNode.getChildren().addAll(cardsRectangles());
            cardNode.getChildren().add(text);

            //at the end of the current iteration, the actual type of card become a children of the instance hBoxChildren
            hBoxChildren.getChildren().add(cardNode);
        }
        root.getChildren().add(hBoxChildren);

        return root;
    }

    /**
     * This method creates the view of the 5 face up cards and the both buttons that represents the cards and tickets draw
     *
     * @param observableGameState Observable game state with all the needed properties to refresh visual elements
     * @param drawTickets         draw tickets handler
     * @param drawCards           draw cards handler
     * @return the graph scene  with the graphical representation of the 5 face up cards
     * and the the both buttons (ticket's deck and card's deck view)
     */
    public static VBox createCardsView(
            ObservableGameState observableGameState,
            ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets,
            ObjectProperty<ActionHandlers.DrawCardHandler> drawCards) {

        //root of the graph scene
        VBox root = new VBox();

        root.getStylesheets().addAll("decks.css", "colors.css");
        root.setId("card-pane");

        // button for the ticket's pile view
        Button ticketButton = new Button(TICKETS);
        ticketButton.disableProperty().bind(drawTickets.isNull());
        ticketButton.setOnAction((mouseEvent) -> drawTickets.get().onDrawTickets());
        ticketButton.getStyleClass().add("gauged");

        // this node will be a child of the button representing the ticket stack
        Group ticketGroup = new Group();
        ticketButton.setGraphic(ticketGroup);

        // background rectangle that contains the gauge
        Rectangle backgroundRectangleTickets = new Rectangle(WIDTH_RECTANGLE_3, HEIGHT_RECTANGLE_3);

        // this rectangle will represent the gauge of the ticket's draw during the game
        Rectangle foregroundRectangleTicket = new Rectangle(observableGameState.getTicketsPercentage().get(), HEIGHT_RECTANGLE_3);
        foregroundRectangleTicket.widthProperty().bind(
                observableGameState.getTicketsPercentage().multiply(WIDTH_RECTANGLE_3).divide(100));

        backgroundRectangleTickets.getStyleClass().add("background");
        foregroundRectangleTicket.getStyleClass().add("foreground");

        // the node ticketGroup become a parent of the both rectangles that represent the tickets gauge
        ticketGroup.getChildren().addAll(backgroundRectangleTickets, foregroundRectangleTicket);

        // the button for the tickets become a children of the root
        root.getChildren().add(ticketButton);

        // we iterate on the 5 face up cards to create a node for each face up cards (each face up card node will be a children of the root)
        for (int indexFaceUpCards : Constants.FACE_UP_CARD_SLOTS) {

            // parent node of the 3 rectangles that will represent the view of the face up card
            StackPane faceUpCard = new StackPane();

            // if the instance of face up card is not null, we attach to the node the color corresponding to the current face-up card
            if (observableGameState.getFaceUpCards(indexFaceUpCards).get() != null) {
                faceUpCard.getStyleClass().add(observableGameState.getFaceUpCards(indexFaceUpCards).get().color() == null ? NEUTRAL :
                        observableGameState.getFaceUpCards(indexFaceUpCards).get().color().toString());
            }

            faceUpCard.getStyleClass().add("card");

            // creation of a listener that will detect a face up card changes
            observableGameState.getFaceUpCards(indexFaceUpCards).addListener((o, oV, nV) -> {
                if (oV != null) {
                    faceUpCard.getStyleClass().remove(oV.color() == null ? NEUTRAL : oV.color().toString());
                }
                faceUpCard.getStyleClass().add(nV.color() == null ? NEUTRAL : nV.color().toString());
            });

            faceUpCard.setOnMouseClicked((mouseEvent) -> {
                try {
                    drawCards.get().onDrawCard(indexFaceUpCards);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            faceUpCard.disableProperty().bind(drawCards.isNull());

            // the node of the current face up card become a parent of the 3 rectangles that represent the face up card
            faceUpCard.getChildren().addAll(cardsRectangles());

            // the node of the current face up card become a children of the root
            root.getChildren().add(faceUpCard);

        }

        // button for the card's pile view
        Button drawButton = new Button(CARDS);
        drawButton.disableProperty().bind(drawCards.isNull());
        drawButton.setOnAction((mouseEvent) -> drawCards.get().onDrawCard(-1));
        drawButton.getStyleClass().add("gauged");

        // background rectangle that contains the gauge
        Rectangle backgroundRectangleDeck = new Rectangle(WIDTH_RECTANGLE_3, HEIGHT_RECTANGLE_3);

        // this rectangle will represent the gauge of the ticket's draw during the game
        Rectangle foregroundRectangleDeck = new Rectangle(observableGameState.getDeckPercentage().get(), HEIGHT_RECTANGLE_3);
        foregroundRectangleDeck.widthProperty().bind(
                observableGameState.getDeckPercentage().multiply(WIDTH_RECTANGLE_3).divide(100));

        // this node will be a child of the button representing the draw stack
        Group drawGroup = new Group();
        drawButton.setGraphic(drawGroup);

        backgroundRectangleDeck.getStyleClass().add("background");
        foregroundRectangleDeck.getStyleClass().add("foreground");

        // the node drawGroup become a parent of the both rectangles that represent the draw gauge
        drawGroup.getChildren().addAll(backgroundRectangleDeck, foregroundRectangleDeck);

        // the draw button become a children of the root
        root.getChildren().add(drawButton);

        return root;
    }
}
