package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

import static ch.epfl.tchu.gui.StringsFr.PLAYER_STATS;
import static javafx.geometry.Orientation.HORIZONTAL;

/**
 * InfoViewCreator class generates the view of information (player's stats and the last 5 information about the progress of the game)
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
final class InfoViewCreator {

    private static final int RADIUS = 5; // Radius of an instance of Circle

    /**
     * Private constructor of InfoViewCreator without parameter to make this class not instantiable
     */
    private InfoViewCreator() {}

    /**
     * This method creates the view of information (player's stats and the last 5 information about the progress of the game)
     *
     * @param playerIdOfGUI       is the identity of the player to which the interface corresponds.
     *                            (This player has to be shown in first on the view)
     * @param playerNames         represents each player's name in function of his id
     * @param observableGameState is the observable game state with all the needed properties to refresh visual elements
     * @param textInformation     is an observable list that contains the information about the progress of the game
     * @return VBox element with all the visuals information of the game baked in
     */
    public static VBox createInfoView(PlayerId playerIdOfGUI, Map<PlayerId, String> playerNames, ObservableGameState observableGameState, ObservableList<Text> textInformation) {

        // root of the scene graph
        VBox root = new VBox();
        root.getStylesheets().addAll("info.css", "colors.css");

        // one of the children of the root
        VBox vBoxChildren = new VBox();
        vBoxChildren.setId("player-stats");

        // creates the view of the stats for each player
        for (PlayerId player : PlayerId.values()) {

            // TextFlow node will be a parent of Circle and Text nodes
            TextFlow textFlowPlayer = new TextFlow();
            textFlowPlayer.getStyleClass().add((player.name()));

            //children of TextFlow node
            Circle circle = new Circle(RADIUS);
            circle.getStyleClass().add("filled");


            //children of TextFlow node
            Text textPlayer = new Text(PLAYER_STATS);

            //property of the player's stats
            textPlayer.textProperty().bind(Bindings.format(
                    PLAYER_STATS,
                    playerNames.get(player),
                    observableGameState.getPlayerTicketsCount(player),
                    observableGameState.getPlayerCardsCount(player),
                    observableGameState.getPlayerCarsCount(player),
                    observableGameState.getPlayerPoints(player)
            ));

            textFlowPlayer.getChildren().addAll(circle, textPlayer);

            vBoxChildren.getChildren().add(textFlowPlayer);

        }

        //  we have to rotate the list if the player corresponding to the gui is not at the first position of the list
        FXCollections.rotate(vBoxChildren.getChildren(), -playerIdOfGUI.ordinal());


        /* separator is a children of the root, it is used to create a visual separation between
           player's stats and information of the progress of the game */
        Separator separator = new Separator();
        separator.setOrientation(HORIZONTAL);

        // this node is the parent of all the node of the text
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");

        Bindings.bindContent(textFlow.getChildren(), textInformation);

        root.getChildren().addAll(vBoxChildren, separator, textFlow);

        return root;
    }
}
