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
    private List<String> listParametres;

    public static void main(String[] args) {
        launch(args);
    }
    public static void startGameClient(String name, String number){
        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                name , Integer.parseInt(number));

        new Thread(remotePlayerClient::run).start();
    }

    /**
     * @param primaryStage (Stage) : pas utilisé dans cette méthode
     * @throws Exception dans le cas où la connexion avec le serveur est interrompue, ou numéro de port invalide.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        listParametres = getParameters().getRaw();
        if(listParametres.isEmpty()){ listParametres = List.of("localhost","5108"); }

        InitialMenuCreator.menuCreator(primaryStage, "client");

        /*RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),
                listParametres.get(0) , Integer.parseInt(listParametres.get(1)));

        new Thread(remotePlayerClient::run).start();*/
    }



}