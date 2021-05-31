package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.DISCARDABLE_TICKETS_COUNT;
import static ch.epfl.tchu.game.Constants.MAX_MESSAGES;
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class GraphicalPlayer {

    private final ObservableGameState jeu;
    private final Stage screen;
    private final ObservableList<Text> listText;
    private final Clip foreSound = SoundMaker.makeSound("sounds/Musique_fond.wav");

    //propriètés des handler qui regardent si le joueur peut faire ces actions ou pas (alors null à l'intérieur)
    private final ObjectProperty<DrawCardHandler> cardsHandlerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<DrawTicketsHandler> ticketsHandlerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ClaimRouteHandler> routesHandlerProperty = new SimpleObjectProperty<>();

    /**
     * Le constructeur crée l'interface graphique, avec les graphes de scènes correspondant
     *
     * @param ownId (PlayerId) : identité du joueur
     * @param playerNames (Map<PlayerId, String>) : map des noms des joueurs
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames){
        assert (isFxApplicationThread());

        jeu = new ObservableGameState(ownId);
        listText = FXCollections.observableList(new ArrayList<>());

        screen = new Stage();
        screen.setTitle(String.join(" ", List.of("tCHu", "\u2014", playerNames.get(ownId))));

        Node mapView = MapViewCreator.createMapView(jeu, routesHandlerProperty, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(jeu, ticketsHandlerProperty, cardsHandlerProperty);
        Node handView = DecksViewCreator.createHandView(jeu);
        Node infoView = InfoViewCreator.createInfoView(ownId, playerNames, jeu, listText);

        BorderPane scenePane = new BorderPane( mapView ,null, cardsView, handView, infoView);
        Scene scene = new Scene(scenePane);

        screen.setScene(scene);
        screen.show();
    }

    /**
     * Appel à la méthode setState de GameState.
     * @param newGameState (PublicGameState) : état de jeu public
     * @param ownState (PlayerState) : état du joueur
     */
    public void setState(PublicGameState newGameState, PlayerState ownState){
        assert (isFxApplicationThread());

        jeu.setState(newGameState,ownState);
    }

    /**
     * Gère l'affichage des messages d'info de la partie
     * @param message message à rajouter à la liste d'informations de la partie
     */
    public void receiveInfo(String message){
        assert (isFxApplicationThread());

        Text textToAdd = new Text(message);
        listText.add(textToAdd);

        if(listText.size() > MAX_MESSAGES) { listText.remove(0); }
    }

    /**
     * Un gestionnaire d'action pour chaque type d'action que peut réaliser le joueur, qui sont mis à jour en fonction de ses possibilités
     *
     * @param routeHandler gestionnaire de la construction d'une route
     * @param cardHandler gestionnaire du tirage de cartes
     * @param ticketsHandler gestionnaires du tirage de billets
     */
    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler routeHandler){
        assert (isFxApplicationThread());

        if(jeu.canDrawCards()){
            cardsHandlerProperty.set(e ->{
                cardHandler.onDrawCard(e);
                setAllNull();
            });
        }else{ cardsHandlerProperty.set(null); }

        if(jeu.canDrawTickets()){
            ticketsHandlerProperty.set(() -> {
                ticketsHandler.onDrawTickets();
                setAllNull();

            });
        }else{ ticketsHandlerProperty.set(null); }

        routesHandlerProperty.set((r, s) ->{
            routeHandler.onClaimRoute(r, s);
            setAllNull();
        });
    }

    /**
     * Ouvre fenêtre de visualisation pour choix d'une quantité variable de billets.
     *
     * @param ticketChoices (SortedBag<Ticket>) : la pannel des billets
     * @param ticketsChooser (ChooseTicketsHandler)
     */
    public void chooseTickets(SortedBag<Ticket> ticketChoices, ChooseTicketsHandler ticketsChooser){
        assert (isFxApplicationThread());

        makePopUpTickets(ticketChoices, screen, ticketsChooser);
    }

    /**
     * Méthode lorsque le joueur à déjà choisi la première carte, autorise à prendre une carte de la pioche ou parmis les visibles.
     *
     * @param cardHandler (DrawCardHandler)
     */
    public void drawCard(DrawCardHandler cardHandler){
        assert (isFxApplicationThread());

        cardsHandlerProperty.set((e) -> {
            cardHandler.onDrawCard(e);
            setAllNull();
        });
    }

    /**
     * Ouvre fenêtre de visualisation pour choix d'une des options pour construire une route
     *
     * @param possibilities (List<SortedBag<Card>>) : pannel de choix des cartes pour construire une route
     * @param chooseCardsHandler (ChooseCardsHandler)
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibilities , ChooseCardsHandler chooseCardsHandler){
        assert (isFxApplicationThread());

        makePopUpCards(possibilities, CHOOSE_CARDS, screen, chooseCardsHandler);
    }

    /**
     * Ouvre fenêtre de visualisation pour choix d'une des options de cartes additionelles pour construire une route
     *
     * @param possibilities (List<SortedBag<Card>>) : liste de possibilités de choix de cartes supplémentaire pour construire une route.
     * @param chooseCardsHandler (ChooseCardsHandler)
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibilities , ChooseCardsHandler chooseCardsHandler){
        assert (isFxApplicationThread());

        makePopUpCards(possibilities, CHOOSE_ADDITIONAL_CARDS, screen, chooseCardsHandler);
    }

    /**
     * Joue un son
     * @param song (String) : le son à jouer
     * @param loop (int) : le durée
     */
    public void playSong(String song, int loop){
        if (! isFxApplicationThread()) throw new AssertionError();

        if(loop == Clip.LOOP_CONTINUOUSLY){

            FloatControl controlSound = (FloatControl) this.foreSound.getControl(FloatControl.Type.MASTER_GAIN);
            controlSound.setValue(-20.f);
            this.foreSound.start();
            this.foreSound.loop(Clip.LOOP_CONTINUOUSLY);

        }else if(loop == 1){
            this.foreSound.stop();
        }else{ SoundMaker.playSound(song, loop); }
    }

    /**
     * Set le longest
     * @param routesP1 (List<Route>)
     * @param routesP2 (List<Route>)
     */
    public void longest(List<Route> routesP1, List<Route> routesP2){
        assert(isFxApplicationThread());

        ObservableGameState.setLonguestTrail(routesP1, routesP2);
    }

    /**
     * Méthode privée pour faire la fenetre de choix des tickets
     *
     * @param tickets (SortedBag<Ticket>) : liste des tickets proposés
     * @param screen (Stage) : la fenetre de jeu principale
     * @param ticketsHandler (ChooseTicketsHandler)
     */
    private void makePopUpTickets(SortedBag<Ticket> tickets, Stage screen, ChooseTicketsHandler ticketsHandler){

        Stage popUp = makePopBasic(screen, TICKETS_CHOICE);

        Button button = new Button(CHOOSE);

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(tickets.toList()));
        BooleanBinding checkSelection = Bindings.greaterThan(tickets.size() - DISCARDABLE_TICKETS_COUNT, Bindings.size(listView.getSelectionModel().getSelectedItems()));
        button.disableProperty().bind(checkSelection);

        //choix multiple pour les billets
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Text text = new Text(String.format(CHOOSE_TICKETS, tickets.size() - DISCARDABLE_TICKETS_COUNT, plural(tickets.size())));
        TextFlow textFlow = new TextFlow(text);

        button.setOnAction(event ->{
            popUp.hide();
            ticketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });

        makeFinalShow(popUp, textFlow, listView, button);
    }

    /**
     * Méthode privée pour faire la fenetre de choix de cards pour construire les routes
     * ou choix de cards additionnelles.
     *
     * @param cards (List<SortedBag<Card>>) : différents choix possibles de cards
     * @param introText (String) : texte affiché dans la fenetre
     * @param screen (Stage) : la fenetre de jeu principale
     * @param chooseCardsHandler (ChooseCardsHandler)
     */
    private void makePopUpCards(List<SortedBag<Card>> cards , String introText ,Stage screen,
                                ChooseCardsHandler chooseCardsHandler){

        Stage popUp = makePopBasic(screen, CARDS_CHOICE);

        Button button = new Button(CHOOSE);

        Text text = new Text(introText);
        TextFlow textFlow = new TextFlow(text);

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(cards));

        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        if(introText.equals(CHOOSE_CARDS)){
            BooleanBinding checkSelection = listView.getSelectionModel().selectedItemProperty().isNull();
            button.disableProperty().bind(checkSelection);
        }

        button.setOnAction(event ->{
            popUp.hide();
            SortedBag<Card> choice = listView.getSelectionModel().getSelectedItem() == null ? SortedBag.of() : listView.getSelectionModel().getSelectedItem();
            chooseCardsHandler.onChooseCards(choice);
        });

        makeFinalShow(popUp,textFlow,listView,button);
    }

    /**
     * Convertisseur de la représentation textuelle des cartes au moyen de la redéfinition de toString()
     */
    public static final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        /**
         * @param object (SortedBag<Card>) : l'object dont on souhaite redéfinir la représentation textuelle
         * @return (String) : la représentation textuelle de l'object désiré
         */
        @Override
        public String toString(SortedBag<Card> object) {
            return Info.cardText(object);
        }

        /**
         * @param string (String) : pas utilisée dans ce cas
         * @return rien
         * @throws UnsupportedOperationException : throw cette erreure car cette méthode n'est pas censée être utilisée
         */
        @Override
        public SortedBag<Card> fromString(String string) throws UnsupportedOperationException{
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Méthode qui met les propriétés suivantes à null
     */
    private void setAllNull(){
        cardsHandlerProperty.set(null);
        ticketsHandlerProperty.set(null);
        routesHandlerProperty.set(null);
    }

    /**
     *Initialisation de la fenetre du popUp
     *
     * @param parent owner de la nouvelle fenetre
     * @return stage initialisé et configuré
     */
    private Stage makePopBasic(Stage parent, String title){
        Stage popUp = new Stage(StageStyle.UTILITY);
        popUp.initOwner(parent);
        popUp.initModality(Modality.WINDOW_MODAL);
        popUp.setTitle(title);
        popUp.setOnCloseRequest(Event::consume);
        return popUp;
    }

    /**
     * Affichage final du popUp avec tous les éléments nécéssaire
     *
     * @param popUp fenetre de l'affichage
     * @param textFlow texte de la fenetre
     * @param listView listes avec les éléments à afficher
     * @param button bouton pour confirmer le choix parmi la liste
     */
    private <T> void makeFinalShow (Stage popUp, TextFlow textFlow, ListView<T> listView, Button button){
        VBox box = new VBox(textFlow, listView, button);
        Scene scenePopUp = new Scene(box);
        scenePopUp.getStylesheets().add("chooser.css");

        popUp.setScene(scenePopUp);
        popUp.show();
    }


}