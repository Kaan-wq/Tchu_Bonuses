package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
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

import static ch.epfl.tchu.game.Constants.DECK_SLOT;
import static ch.epfl.tchu.gui.StringsFr.CARDS;
import static ch.epfl.tchu.gui.StringsFr.TICKETS;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

class DecksViewCreator {
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 5;

    private static final int LOC_AND_IN_WIDTH = 40;
    private static final int LOC_AND_IN_HEIGHT = 70;

    private static final int OUT_WIDTH = 60;
    private static final int OUT_HEIGHT = 90;

    /**
     * Constructeur privé pour rendre la classe non instanciable
     */
    private DecksViewCreator(){}

    /**
     * Méthode pour faire l'interface visible des cartes de la main du joueur
     *
     * @param observableState (ObservableGameState) état observable du jeu
     * @return (Node) : le graphe de scène des cartes de la main du joueur
     */
    public static Node createHandView(ObservableGameState observableState){

        ObservableList<Ticket> tickets = observableState.getListBillets();
        ListView<Ticket> listView = new ListView<>(tickets);
        listView.setId("tickets");

        listView.getSelectionModel().selectedItemProperty().addListener((p, o, n) ->{
            if(o != null){o.getStations().forEach(s -> observableState.setClickedStations(s, false)); }
            n.getStations().forEach(s -> observableState.setClickedStations(s, true));
        });

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

        Button buttonTickets = makeButton(observableState.getRemainingTickets(), TICKETS);
        buttonTickets.disableProperty().bind(ticketsHandler.isNull());
        buttonTickets.setOnMouseClicked(e -> ticketsHandler.get().onDrawTickets());

        Button buttonCards = makeButton(observableState.getRemainingCards(), CARDS);
        buttonCards.disableProperty().bind(cardHandler.isNull());
        buttonCards.setOnMouseClicked(e -> cardHandler.get().onDrawCard(DECK_SLOT));

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
        stackPane.setOnMouseClicked(e -> cardHandler.get().onDrawCard(slot));

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

        Rectangle foreground = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(property.multiply(50).divide(100));

        Rectangle background = new Rectangle(BUTTON_WIDTH,BUTTON_HEIGHT);
        background.getStyleClass().add("background");

        Group group = new Group(background, foreground);
        button.setGraphic(group);
        return button;
    }

    /**
     * @return (StackPane) : une visualisation d'une carte
     */
    private static StackPane makeRectangles(){
        Rectangle outside = new Rectangle(OUT_WIDTH, OUT_HEIGHT);
        outside.getStyleClass().add("outside");

        Rectangle inside = new Rectangle(LOC_AND_IN_WIDTH, LOC_AND_IN_HEIGHT);
        inside.getStyleClass().addAll("inside", "filled");

        Rectangle wagLoc = new Rectangle(LOC_AND_IN_WIDTH, LOC_AND_IN_HEIGHT);
        wagLoc.getStyleClass().add("train-image");

        return new StackPane(outside, inside, wagLoc);
    }
}