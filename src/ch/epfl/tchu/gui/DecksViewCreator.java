package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ch.epfl.tchu.gui.ActionHandlers.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

class DecksViewCreator {
    private DecksViewCreator(){}

    /**
     * Méthode pour faire l'nterface visible des cartes de la mmain du joueur
     *
     * @param observableState (ObservableGameState)
     * @return (Node) : le graphe de scène des cartes de la main du joueur
     */
    public static Node createHandView(ObservableGameState observableState){

        ObservableList<Ticket> tickets = observableState.getListBillets();
        ListView listView = new ListView(tickets);
        listView.setId("tickets");

        HBox littleBox = new HBox();
        littleBox.setId("hand-pane");

        Card.ALL.forEach(i -> makeHandCardsPane(i, littleBox, observableState));

        HBox bigBox = new HBox(listView, littleBox);
        bigBox.getStylesheets().addAll("decks.css", "colors.css");

        return bigBox;
    }

    /**
     * Méthode pour faire l'interface visible des cartes visibles
     *
     * @param observableState (ObservableGameState)
     * @param ticketsHandler (ObjectProperty<DrawTicketsHandler>) :
     *                       le gestionnaire d'action pour le tirage des billets
     * @param cardHandler (ObjectProperty<DrawCardHandler>) :
     *                    le gestionnaire d'action pour le tirage des cartes
     * @return (Node) : le graphe de scène des cartes visibles
     */
    public static Node createCardsView(ObservableGameState observableState,
                                       ObjectProperty<DrawTicketsHandler> ticketsHandler,
                                       ObjectProperty<DrawCardHandler> cardHandler){

        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("decks.css", "colors.css");
        vBox.setId("card-pane");

        Button buttonTickets = makeButton(observableState.getRemainingTickets(), "Billets");
        buttonTickets.disableProperty().bind(ticketsHandler.isNull());
        buttonTickets.setOnMouseClicked(e -> ticketsHandler.get().onDrawTickets());

        Button buttonCards = makeButton(observableState.getRemainingCards(), "Cards");
        buttonCards.disableProperty().bind(cardHandler.isNull());
        buttonCards.setOnMouseClicked(e -> {
            cardHandler.get().onDrawCard(-1);
            soundMaker("sounds/CardFaceUpDraw.wav");
        });

        vBox.getChildren().add(buttonTickets);

        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            makeFaceUpCardsPane(observableState.getFaceUpCard(slot), vBox, slot, cardHandler);
        }

        vBox.getChildren().add(buttonCards);

        return vBox;
    }

    /**
     * Méthodes pour faire les cartes de la main du joueur (graphe + liens)
     *
     * @param card (Card) : la carte asociée
     * @param littleBox (HBox) : la boîte associée
     * @param observableState (ObservableGameState)
     */
    private static void makeHandCardsPane(Card card, HBox littleBox, ObservableGameState observableState){

        Text text = new Text();
        text.getStyleClass().add("count");

        StackPane stackPane = makeRectangles();
        stackPane.getChildren().add(text);
        String cardColor = card.color() == null ? "NEUTRAL" : card.color().name();
        stackPane.getStyleClass().addAll(cardColor, "card");

        //Liens pour les cartes de la main
        text.textProperty().bind(Bindings.convert(observableState.getCardHandNumber(card)));
        text.visibleProperty().bind(Bindings.greaterThan(observableState.getCardHandNumber(card), 1));
        stackPane.visibleProperty().bind(Bindings.greaterThan(observableState.getCardHandNumber(card), 0));

        littleBox.getChildren().add(stackPane);
    }

    /**
     * Méthode pour faire une carte parmis les cartes visibles (graphe + auditeur + liens)
     *
     * @param cardProp (ReadOnlyObjectProperty<Card>) : propriété contenant la carte
     * @param vBox (VBox) : la boite verticale associée
     * @param cardHandler (ObjectProperty<DrawCardHandler>)
     */
    private static void makeFaceUpCardsPane(ReadOnlyObjectProperty<Card> cardProp, VBox vBox, int slot,
                                            ObjectProperty<DrawCardHandler> cardHandler){

        StackPane stackPane = makeRectangles();
        stackPane.getStyleClass().addAll("card", "");

        //Auditeur pour les cartes faces visibles
        cardProp.addListener((p, o, n) ->{
            String color = (n == Card.LOCOMOTIVE ? "NEUTRAL" : n.color().name());
            stackPane.getStyleClass().set(1, color);
        });

        //Mouse click
        stackPane.disableProperty().bind(cardHandler.isNull());
        stackPane.setOnMouseClicked(e ->{
            cardHandler.get().onDrawCard(slot);
            soundMaker("sounds/CardFaceUpDraw.wav");
        });

        vBox.getChildren().add(stackPane);
    }

    /**
     * Méthode pour faire le graphe de scène d'un boutton
     *
     * @param property (ReadOnlyIntegerProperty) : la propriété qu'on souhaite lier
     * @param string (String) : le texte du boutton
     */
    private static Button makeButton(ReadOnlyIntegerProperty property, String string){
        Button button = new Button(string);
        button.getStyleClass().add("gauged");

        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(property.multiply(50).divide(100));

        Rectangle background = new Rectangle(50,5);
        background.getStyleClass().add("background");

        Group group = new Group(background, foreground);
        button.setGraphic(group);
        return button;
    }

    private static StackPane makeRectangles(){
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle inside = new Rectangle(40, 70);
        inside.getStyleClass().addAll("inside", "filled");

        Rectangle wagLoc = new Rectangle(40, 70);
        wagLoc.getStyleClass().add("train-image");

        return new StackPane(outside, inside, wagLoc);
    }

    /**
     * Méthode qui passe le son désiré
     *
     * @param string (String) : le son désiré
     */
    private static void soundMaker(String string){
        try{
            File file = new File(string);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException ua) {
            ua.printStackTrace();
        } catch (LineUnavailableException lu) {
            lu.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}