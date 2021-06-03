package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class ServerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage (Stage) : pas utilisé
     * @throws Exception  dans le cas où la connexion avec le serveur est interrompue, ou numéro de port invalide.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        InitialMenuCreator.menuCreator(primaryStage, "serveur");
    }

    /**
     *
     * @param argFirst nom du premier joueur de la partie
     * @param argSecnd nom du deuxième joueur de la partie
     */
    public static void startedGame(String argFirst, String argSecnd){
        try{
            ServerSocket serverSocket = new ServerSocket(5108);
            Socket socket = serverSocket.accept();

            Player clientPlayer = new GraphicalPlayerAdapter();
            Player remotePlayerProxy = new RemotePlayerProxy(socket);

            var playerNames = new EnumMap(PlayerId.class);
            playerNames.put(PLAYER_1, argSecnd);
            playerNames.put(PLAYER_2, argFirst);

            var playersMap = new EnumMap(PlayerId.class);
            playersMap.put(PLAYER_1, remotePlayerProxy);
            playersMap.put(PLAYER_2, clientPlayer);

            new Thread(() -> Game.play(playersMap, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();

        }catch(Exception e){ throw new Error(); }
    }
}