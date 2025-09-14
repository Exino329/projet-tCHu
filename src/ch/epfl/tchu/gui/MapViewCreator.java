package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Map view creator class, which generates the image for the map, with corresponding routes.
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
final class MapViewCreator {

    private static final int RADIUS = 3; // Radius of the circle
    private static final int CIRCLE_CENTER_X = 12; // Pos x of the circle
    private static final int CIRCLE_CENTER_Y = 6; // Pos y of the circle
    private static final int WIDTH_RECTANGLE = 36; // Width of the rectangle
    private static final int HEIGHT_RECTANGLE = 12; // Height of the rectangle

    /**
     * Private constructor of MapViewCreator to make this class not instantiable
     *
     */
    private MapViewCreator(){}

    /**
     * Method which creates the map view
     *
     * @param state is the observable game state with all the needed properties to refresh visual elements
     * @param handler Claim route handler
     * @param chooser Card chooser
     * @return Node element with all the visuals baked in
     */
    public static Pane createMapView(ObservableGameState state, ObjectProperty<ActionHandlers.ClaimRouteHandler> handler, CardChooser chooser)
    {
        // Map pane
        Pane map = new Pane();
        map.getStylesheets().addAll("map.css", "colors.css");

        // Background
        ImageView background = new ImageView();
        map.getChildren().add(background);

        // Roads
        for(Route route : ChMap.routes())
        {
            Group routeGroup = new Group(); // Create the group
            routeGroup.setId(route.id()); // Set the road ID

            // Set the overground / underground class and color class
            routeGroup.getStyleClass().addAll(route.level() == Route.Level.OVERGROUND ? route.level().name() : route.level().name(),
                    route.color() == null ? "NEUTRAL" : route.color().toString());

            // Add a listener for the route owner, which adds the player ID to the group classes if the route is claimed
            state.getRouteOwner(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.toString()));

            // Add a listener if a route is claimable to add the "route" class
            state.claimable(route).addListener((o, oV, nV) -> {
                // If the route is claimable
                if(nV) {
                    // Add the class tag
                    routeGroup.getStyleClass().add("route");
                }
                // If not
                else
                {
                    // Remove the class tag
                    routeGroup.getStyleClass().remove("route");
                }
            });

            // Disable clicking if the route is not claimable
            routeGroup.disableProperty().bind(handler.isNull().or(state.claimable(route).not()));

            // Configure the mouse event if the road is clicked
            routeGroup.setOnMouseClicked(mouseEvent -> {
                List<SortedBag<Card>> possibleClaimCards = state.possibleClaimCards(route);
                ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> handler.get().onClaimRoute(route, chosenCards);
                chooser.chooseCards(possibleClaimCards, chooseCardsH);
            });

            // Go through each square of the road
            for(int i = 1; i < route.length() + 1; ++i)
            {
                Group square = new Group();
                square.setId(String.format("%s_%s", route.id(), i));

                // Track
                Rectangle track = new Rectangle(WIDTH_RECTANGLE, HEIGHT_RECTANGLE);
                track.getStyleClass().addAll("filled", "track");

                // Car
                Group car = new Group();
                car.getStyleClass().add("car");

                Rectangle carRectangle = new Rectangle(WIDTH_RECTANGLE, HEIGHT_RECTANGLE);
                carRectangle.getStyleClass().add("filled");

                Circle circle1 = new Circle(CIRCLE_CENTER_X,CIRCLE_CENTER_Y,RADIUS);
                Circle circle2 = new Circle(CIRCLE_CENTER_X * 2,CIRCLE_CENTER_Y,RADIUS);

                car.getChildren().addAll(carRectangle, circle1, circle2);

                square.getChildren().addAll(track, car);
                routeGroup.getChildren().add(square);
            }

            map.getChildren().add(routeGroup);
        }

        return map;
    }

    /**
     * Card chooser interface which opens the dialog for player interaction, to choose card combinations
     *
     */
    @FunctionalInterface
    interface CardChooser
    {
        void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler);
    }

}
