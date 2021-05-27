package ch.epfl.tchu.gui;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InitialMenuCreator {

    public static void menuCreator(Stage parent, String id){
        Stage menu = new Stage(StageStyle.UTILITY);

        menu.initModality(Modality.WINDOW_MODAL);
        menu.initOwner(parent);
        menu.setTitle("Bienvenus à TcHu");
        menu.setOnCloseRequest(Event::consume);


        ImageView logo = new ImageView(new Image("logoTcHu.jpg"));

        Button button = new Button("Commencer le jeu");
        TextFlow textFlow;
        if(id.equals("serveur")){
             textFlow = new TextFlow(new Text("Veuillez introduire vos noms"));}
        else{
            textFlow = new TextFlow(new Text("Veuillez introduire les liens de connexion"));
        }
        TextField playerName;
        TextField playerName2;
        if(id.equals("serveur")){
             playerName = new TextField("Nom du premier joueur");
             playerName2 = new TextField("Nom du deuxième joueur");
        }else{
            playerName = new TextField("Nom du port / localhost");
            playerName2 = new TextField("Nunméro du port / 5108");
        }


        Button regles = new Button("Afficher les règles du jeu");
        regles.setAlignment(Pos.BOTTOM_CENTER);

        // mettre en place la visualisation
        VBox r = new VBox();

        // rajouter les éléments
        r.getChildren().add(logo);
        r.getChildren().add(textFlow);
        r.getChildren().add(playerName);
        r.getChildren().add(playerName2);
        r.getChildren().add(button);
        r.getChildren().add(regles);

        // faire la scene qui les affichera
        Scene sc = new Scene(r, 400, 400);
        sc.getStylesheets().add("menu.css");

        button.setOnAction(event -> {
            menu.hide();
            menu.close();
            if(id.equals("serveur")){
                ServerMain.startedGame(playerName.getText(), playerName2.getText());
            }else{
                ClientMain.startGameClient(playerName.getText(), playerName2.getText());
            }

        });

        regles.setOnAction(event -> {
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
