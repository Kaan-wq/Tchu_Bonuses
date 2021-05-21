package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import static ch.epfl.tchu.game.Constants.*;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public class ObservableGameState {

    private final PlayerId ownId;
    private PlayerState ownState;
    private PublicGameState publicGame;

    //pourcentages publics
    private final IntegerProperty remainingTicketsProperty;
    private final IntegerProperty remainingCardsProperty;
    private final List<ObjectProperty<Card>> faceUpCardsProperty = new ArrayList<>();
    private final List<ObjectProperty<PlayerId>> routesLords = new ArrayList<>();

    //état public de chacun des joueurs
    private final List<IntegerProperty> ticketsNumber = new ArrayList<>();
    private final List<IntegerProperty> cardsNumber = new ArrayList<>();
    private final List<IntegerProperty> carsNumber = new ArrayList<>();
    private final List<IntegerProperty> points = new ArrayList<>();

    private final ObservableList<Ticket> listBillets;

    //état privé du joueur de l'instance
    private final List<IntegerProperty> handCardsNumber = new ArrayList<>();
    private final List<BooleanProperty> routesPossibleClaim = new ArrayList<>();


    public ObservableGameState(PlayerId ownId){
        this.ownId = ownId;

        remainingTicketsProperty = new SimpleIntegerProperty(0);
        remainingCardsProperty = new SimpleIntegerProperty(0);

        for(int i : FACE_UP_CARD_SLOTS){
            faceUpCardsProperty.add(new SimpleObjectProperty<>());
        }

        for(PlayerId playerId : PlayerId.ALL){
            ticketsNumber.add(new SimpleIntegerProperty(0));
            cardsNumber.add(new SimpleIntegerProperty(0));
            carsNumber.add(new SimpleIntegerProperty(0));
            points.add(new SimpleIntegerProperty(0));
        }

        for(Route route : ChMap.routes()){
            ObjectProperty<PlayerId> routeLord = new SimpleObjectProperty<>();
            BooleanProperty routePossible = new SimpleBooleanProperty();

            routesLords.add(routeLord);
            routesPossibleClaim.add(routePossible);
        }

        for(Card card : Card.ALL){
            IntegerProperty quantity = new SimpleIntegerProperty();
            handCardsNumber.add(quantity);
        }

        listBillets = FXCollections.observableList(new ArrayList<>());
    }

    /**
     * Mise à jour de toutes les propriètés de l'état du jeu.
     * @param newGameState : partie publique de l'état du jeu
     * @param ownState : état complet du joueur auquel correspond l'instance.
     */
    public void setState (PublicGameState newGameState, PlayerState ownState) {
        publicGame = newGameState;
        this.ownState = ownState;

        remainingCardsProperty.set((newGameState.cardState().deckSize() * 100) / TOTAL_CARDS_COUNT);
        remainingTicketsProperty.set((newGameState.ticketsCount() * 100) / ChMap.tickets().size());

        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCardsProperty.get(slot).set(newCard);
        }

        for(PlayerId playerId : PlayerId.ALL){
            for (Route route : ChMap.routes()) {
                if (newGameState.playerState(playerId).routes().contains(route)) {
                    routesLords.get(ChMap.routes().indexOf(route)).set(playerId);
                }
            }
            ticketsNumber.get(playerId.ordinal()).set(newGameState.playerState(playerId).ticketCount());
            cardsNumber.get(playerId.ordinal()).set(newGameState.playerState(playerId).cardCount());
            carsNumber.get(playerId.ordinal()).set(newGameState.playerState(playerId).carCount());
            points.get(playerId.ordinal()).set(newGameState.playerState(playerId).claimPoints());
        }

        List<Ticket> ticketsList = ownState.tickets().toList();
        ticketsList.removeAll(listBillets);
        listBillets.addAll(ticketsList);

        //mise à jour de la main du joueur
        countCardsPlayer(ownState);

        //mise à jour de la possibilité de construire les routes.
        for(Route route : ChMap.routes()){
            int index = ChMap.routes().indexOf(route);

            routesPossibleClaim.get(index).set(newGameState.currentPlayerId().equals(ownId) && ownState.canClaimRoute(route) &&
                    routesLords.get(index).get() == null && routesLords.get(ChMap.routes().indexOf(doubleOuPas(route))).get() == null);
        }
    }

    public boolean canDrawTickets(){
        return publicGame.canDrawTickets();
    }

    public boolean canDrawCards(){
        return publicGame.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return ownState.possibleClaimCards(route);
    }

    //Tous les getters

    /**
     * @param slot (int) : l'emplacement de la carte
     * @return (ReadOnlyObjectProperty<Card>) : la propriété non-modifiable de la carte face visible à l'emplacement "slot"
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCard(int slot) {
        return faceUpCardsProperty.get(slot);
    }

    /**
     * @param card (Card) : la carte dont on cherche la multiplicité
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de cartes "card" détenus par le joueurs
     */
    public ReadOnlyIntegerProperty getCardHandNumber(Card card){
        return handCardsNumber.get(Card.ALL.indexOf(card));
    }

    /**
     * @param route (Route)
     * @return (ReadOnlyBooleanProperty) : la propriété non-modifiable qui indique si la route à été prise ou non
     */
    public ReadOnlyBooleanProperty getBooleanRoute(Route route){
        return routesPossibleClaim.get(ChMap.routes().indexOf(route));
    }

    /**
     * @param route (Route) : route dont on veut connaitre le propriétaire
     * @return (ReadOnlyObjectProperty<PlayerId>) : la propriété non-modifiable du propriétaire de la route
     */
    public ReadOnlyObjectProperty<PlayerId> getLordRoute(Route route) {
        return routesLords.get(ChMap.routes().indexOf(route));
    }

    /**
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de tickets restants
     */
    public ReadOnlyIntegerProperty getRemainingTickets(){
        return remainingTicketsProperty;
    }

    /**
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de cartes restantes
     */
    public ReadOnlyIntegerProperty getRemainingCards(){
        return remainingCardsProperty;
    }

    /**
     * @return (ObservableList<Ticket>) : la liste observable des tickets du joueurs
     */
    public ObservableList<Ticket> getListBillets(){
        return listBillets;
    }

    /**
     * @param playerId (PlayerId) : l'identité du joueur
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de tickets détenus par le joueur
     */
    public ReadOnlyIntegerProperty getTicketsNumber(PlayerId playerId){
        return ticketsNumber.get(playerId.ordinal());
    }

    /**
     * @param playerId (PlayerId) : l'identité du joueur
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de cartes détenues par le joueur
     */
    public ReadOnlyIntegerProperty getCardsNumber(PlayerId playerId){
        return cardsNumber.get(playerId.ordinal());
    }

    /**
     * @param playerId (PlayerId) : l'identité du joueur
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de wagons détenus par le joueur
     */
    public ReadOnlyIntegerProperty getCarsNumber(PlayerId playerId){
        return carsNumber.get(playerId.ordinal());
    }

    /**
     * @param playerId (PlayerId) : l'identité du joueur
     * @return (ReadOnlyIntegerProperty) : la propriété non-modifiable du nombre de points détenus par le joueur
     */
    public ReadOnlyIntegerProperty getPoints(PlayerId playerId){
        return points.get(playerId.ordinal());
    }

    /**
     * Méthode privée afin de modifier la propriété du nombre de cartes de type "card" en main
     *
     * @param card (Card) : carte dont on cherche l'index dans le liste
     * @return (IntegerProperty) : la propriété modifiable du nombres de cartes du type "card"
     */
    private IntegerProperty cardGetter(Card card){
        return handCardsNumber.get(Card.ALL.indexOf(card));
    }

    /**
     * Mets à jour les cartes dans la main du joueur.
     * @param ownState nouvel état du joueur à mettre à jour dans l'interface.
     */
    private void countCardsPlayer(PlayerState ownState){
        for (Card cards : Card.ALL) {
            int countCard = 0;
            for (Card card : ownState.cards()) {
                if (card.equals(cards)) { ++countCard; }
            }
            cardGetter(cards).set(countCard);
        }
    }

    /**
     * @param route : route à vérifier si double ou pas et rendre sa complémentaire si elle existe.
     * @return l'autre route double de la même section ou elle même si elle est pas double.
     */
    private Route doubleOuPas (Route route){
        for(Route routeCompared : ChMap.routes()){
            if(route.stations().equals(routeCompared.stations()) && !routeCompared.equals(route)){
                return routeCompared;
            }
        }
        return route;
    }
}