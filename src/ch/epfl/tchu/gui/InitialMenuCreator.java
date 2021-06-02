package ch.epfl.tchu.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.ConnectException;

public class InitialMenuCreator {

    public static void menuCreator(Stage parent, String id){
        Stage menu = new Stage(StageStyle.UTILITY);

        menu.initModality(Modality.WINDOW_MODAL);
        menu.initOwner(parent);
        menu.setTitle("Bienvenu à TcHu");


        ImageView logo = new ImageView(new Image("logoTcHu.jpg"));

        Button button = new Button("Commencer le jeu");
        TextFlow textFlow;
        if(id.equals("serveur")){
             textFlow = new TextFlow(new Text("Veuillez introduire vos noms"));}
        else{
            textFlow = new TextFlow(new Text("Veuillez introduire les liens de connexion"));
        }


        TextField playerName; //premier textfield
        TextField playerName2; //deuxième textfield
        if(id.equals("serveur")){                               //initialisation des textfield du serveur
             playerName = new TextField();
             playerName.setPromptText("Nom du premier joueur");
             playerName.setFocusTraversable(false);
             playerName2 = new TextField();
             playerName2.setPromptText("Nom du deuxième joueur");
             playerName2.setFocusTraversable(false);
        }else{                                                 //initialisation des textfield du client
            playerName = new TextField("localhost");
            playerName.setPromptText("Nom du port");
            playerName.setFocusTraversable(false);
            playerName2 = new TextField("5108");
            playerName2.setPromptText("Numéro du port");
            playerName2.setFocusTraversable(false);
        }


        Button regles = new Button("Afficher les règles du jeu");
        regles.setAlignment(Pos.BOTTOM_CENTER);

        // mettre en place la visualisation
        VBox r = new VBox();

        // rajouter les éléments
        r.getChildren().addAll(logo, textFlow, playerName, playerName2, button, regles);

        // faire la scene qui les affichera
        Scene sc = new Scene(r, 400, 400);
        sc.getStylesheets().add("menu.css");

        button.setOnAction(event -> {
            Platform.setImplicitExit(false);
            menu.hide();

            if(id.equals("serveur")){
                ServerMain.startedGame(playerName.getText(), playerName2.getText());
            }else{
                ClientMain.startGameClient(playerName.getText(), playerName2.getText());
            }


        });

        regles.setOnAction(event -> {                           //affichage des règles dans le menu
            Stage popUp = new Stage(StageStyle.UTILITY);
            popUp.initOwner(menu);
            popUp.initModality(Modality.NONE);
            popUp.setTitle("Règles du jeu");
            ImageView reg = new ImageView(new Image("regles.jpg"));
            StackPane view = new StackPane(reg);
            Scene sceneRegles = new Scene(view);
            popUp.setScene(sceneRegles);
            popUp.show();
        });

        menu.setScene(sc);
        menu.show();


    }
}
