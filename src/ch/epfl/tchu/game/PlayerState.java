
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */
public final class PlayerState extends PublicPlayerState {
    
private final SortedBag<Ticket> tickets;
private final SortedBag<Card> cards;

    /**
     * Constructeur des états privés des joueurs avec toute l'information sur leurs cartes.
     * 
     * @param tickets : multiensemble des billets en possesion du joueur.
     * @param cards : multiensemble des cartes en possesion du joueur.
     * @param routes : liste de toutes les routes du joueur.
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }
    
    /**
     * Mise en place de l'état de début de partie du joueur.
     * 
     * @param initialCards : état initial du tas de cartes du joueur, multiensemble avec 4 cartes.
     * @return : état du joueur au début de la partie.
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size()== Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>());
    }
    
    /**
     * @return : les billets du joueur.
     */
    public SortedBag<Ticket> tickets(){
        return tickets;        
    }
    
    /**
     * État du joueur avec des billets piochés.
     * 
     * @param newTickets : billets à rajouter dans le tas du joueur.
     * @return : état du joueur avec les billets mis à jour.
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }
    
    /**
     * @return : tas de cartes du joueur.
     */
    public SortedBag<Card> cards(){
        return cards;
    }
    
    /**
     * État du joueur qui a pioché une carte.
     * 
     * @param card : carte à rajouter dans le tas du joueur.
     * @return : état du joueur avec les cartes mises à jour.
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }
    
    /**
     * État du joueur qui a pioché des cartes.
     * 
     * @param additionalCards : multiensemble de cartes à rajouter dans le tas du joueur.
     * @return : état du joueur avec les cartes mises à jour.
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }
    
    /**
     * État du joueur qui a pioché des cartes.
     * 
     * @param route : route que le joueur essaye de prendre.
     * @return : vrai si le joueur possède les éléments pour construire cette route, faux autrement.
     */
    public boolean canClaimRoute(Route route) { 
        boolean bol= false;
        if(this.carCount()>= route.length()) {
            for(SortedBag<Card> a : route.possibleClaimCards()) {
                if(cards.contains(a)) {
                    bol = true;
                }
            }
            return bol;
        }
        else{
            return false;
        }
    }
    
    /**
     * @param route : route que le joueur désire construire.
     * @return : liste avec toutes les possibiltés de multiensemble pour construire cette route en fonction des cartes du joueur.
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(this.carCount()>= route.length());
        
        List<SortedBag<Card>> options = new ArrayList<>();
        
        for(SortedBag<Card> a : route.possibleClaimCards()) {
            if(cards.contains(a)) {
                options.add(a);
            }
        }
        return options;
    }
    
    /**
     * @param additionalCardsCount : nombre de cartes que les cartes piochées obligent le joueur à déposer.
     * @param initialCards : cartes déposées initialement par le joueur.
     * @return : liste avec toute les combinaisons possibles de cartes additionnelles à deposer afin de pouvoir construire un tunnel.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards){
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty());
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);

        SortedBag<Card> cardsLeft = this.cards.difference(initialCards);
        List<SortedBag<Card>> bonPaquets = new ArrayList<>();

        if(cardsLeft.size() < additionalCardsCount){
            return bonPaquets;
        }

        Set<SortedBag<Card>> paquets = cardsLeft.subsetsOfSize(additionalCardsCount);

        Color couleur = null;

        for(Card b : initialCards) {
            if(b.color()!= null) {
                couleur = b.color();
            }
        }

        for(SortedBag<Card> a : paquets) {
            for(int i = 0; i<= additionalCardsCount; i++) {
                if(couleur != null) {
                    if(a.countOf(Card.LOCOMOTIVE)==i && a.countOf(Card.of(couleur))==additionalCardsCount-i && !bonPaquets.contains(a)) {
                        bonPaquets.add(a);
                    }
                }else {
                    if(a.countOf(Card.LOCOMOTIVE)==additionalCardsCount && !bonPaquets.contains(a)) {
                        bonPaquets.add(a);
                    }
                }
            }
        }
        bonPaquets.sort(Comparator.comparingInt(cs-> cs.countOf(Card.LOCOMOTIVE)));
        return bonPaquets;
    }
    
    /**
     * État du joueur avec nouvelle route construite.
     * 
     * @param route : route prise par le joueur dans ce tour.
     * @param claimCards : cartes utilisées pour construire cette route.
     * @return : état du joueur mis à jour, cartes enlevées et routes rajoutées.
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> goodOne = new ArrayList<>(routes());
        goodOne.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), goodOne);
    }
    
    /**
     * La méthode construit une partition des stations en possesion par le joueur, puis connecte toutes les stations,
     * afin de déterminer si elles sont bien reliées et si le billets donne ou enlève des points au joueur.
     * 
     * @return : nombre de points donnés ou enlevés par les billets du joueur.
     */
    public int ticketPoints() {
        
        int stationMax=0;
        
        for(int i =0; i< routes().size(); i++) {
            if(routes().get(i).station1().id()>stationMax) {
                stationMax = routes().get(i).station1().id();
            }
            
            if(routes().get(i).station2().id()>stationMax) {
                stationMax = routes().get(i).station2().id();
            }
            
        }
        
        StationPartition.Builder ab = new StationPartition.Builder(stationMax+1);
        for(Route a : routes()) {
            ab.connect(a.station1(), a.station2());
        }
        
        StationPartition abc = ab.build();
        int value = 0;
        
        for( Ticket a : this.tickets) {
          value += a.points(abc);
        }
        return value;
    }
    
    /**
     * @return : nombre de points totals du joueur.
     */
    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }
}