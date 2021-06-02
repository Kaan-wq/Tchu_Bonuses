package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Méthode pour lancer la partie
     * @param name nom du port pour lancer le jeu
     * @param number numéro du port
     */
    public static void startGameClient(String name, String number){
        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                name , Integer.parseInt(number));

        new Thread(remotePlayerClient::run).start();
    }

    /**
     * @param primaryStage (Stage) : pas utilisé dans cette méthode
     * @throws Exception dans le cas où la connexion avec le serveur est interrompue, numéro de port invalide ou le serveur n'est pas encore lancé.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        InitialMenuCreator.menuCreator(primaryStage, "client");
    }
}