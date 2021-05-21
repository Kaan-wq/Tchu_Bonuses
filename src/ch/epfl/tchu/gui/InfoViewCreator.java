package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

final class InfoViewCreator {

    private InfoViewCreator(){}

    /**
     * @param ownId (PlayerId) : l'identité du joueur
     * @param playerNames (Map<PlayerId, String>) : la map des noms des joueurs
     * @param gameState (ObservableGameState) : l'état de jeu observable
     * @param texts (ObservableList<Text>) : liste observable des messages
     * @return le graphe de scène des informations (joueurs + messages)
     */
    public static Node createInfoView(PlayerId ownId, Map<PlayerId, String> playerNames,
                                      ObservableGameState gameState, ObservableList<Text> texts){

        Separator separator = new Separator(Orientation.HORIZONTAL);
        VBox vBox = new VBox(makePlayerStats(ownId, playerNames, gameState), separator, makeMessages(texts));
        vBox.getStylesheets().addAll("info.css", "colors.css");
        return vBox;
    }

    /**
     * Méthode privée pour réaliser le graphe de scène des stats des joueurs
     *
     * @param ownId (PlayerId) : l'identité du joueur
     * @param playerNames (Map<PlayerId, String>) : la map des noms des joueurs
     * @param gameState (ObservableGameState) : l'état de jeu observable
     * @return le graphe de scène des stats des joueurs
     */
    private static VBox makePlayerStats(PlayerId ownId, Map<PlayerId, String> playerNames, ObservableGameState gameState){
        VBox vBox = new VBox();
        vBox.setId("player-stats");
        List<PlayerId> idList = List.of(ownId, ownId.next());

        for(PlayerId playerId : idList){

            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");

            Text text = new Text();
            text.textProperty().bind(Bindings.format(
                    StringsFr.PLAYER_STATS, playerNames.get(playerId),
                    gameState.getTicketsNumber(playerId), gameState.getCardsNumber(playerId),
                    gameState.getCarsNumber(playerId), gameState.getPoints(playerId)));

            TextFlow textFlow = new TextFlow(circle, text);
            textFlow.getStyleClass().add(playerId.name());

            vBox.getChildren().add(textFlow);
        }
        return vBox;
    }

    /**
     * Méthode privée pour réaliser le graphe de scène des messages
     *
     * @param texts (ObservableList<Text>) : liste observable des messages
     * @return le graphe des scènes des messages
     */
    private static TextFlow makeMessages(ObservableList<Text> texts){
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");

        for(int i : Constants.FACE_UP_CARD_SLOTS){
            Text text = new Text();
            textFlow.getChildren().add(text);
        }
        Bindings.bindContent(textFlow.getChildren(), texts);
        return textFlow;
    }
}