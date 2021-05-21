package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;
/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public class PublicPlayerState {

    private final int billets;
    private final int cards;
    private final List<Route> routes;
    private final int points;
    private final int wagons;
 
   /**
    * @param ticketCount : nombre de tickets du joueur.
    * @param cardCount : nombre de cartes du joueur.
    * @param routes : liste de routes dans la main du joueur.
    */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        billets = ticketCount;
        cards = cardCount;
        this.routes = routes;
        
        //points du joueur
        int a = 0;
        for(Route x : this.routes) {
            a += x.claimPoints();
        }
        points = a;
        
        //nombre de wagons du joueur
        int b = Constants.INITIAL_CAR_COUNT;
        for(Route x : this.routes) {
            b -= x.length();
        }
        wagons = b;
    }

    /**
     * @return : quantité de billets du joueur.
     */
    public int ticketCount() {
        return billets;
    }
    
    /**
     * @return : quantité de cartes du joueur.
     */
    public int cardCount() {
        return cards;
    }
    
    /**
     * @return : liste de route du joueur.
     */
    public List<Route> routes(){
        return routes;
    }
    
    /**
     * @return : quantité de wagons du joueur.
     */
    public int carCount() {
        return wagons;
    }
    
    /**
     * @return : points du joueur par constructions des routes.
     */
    public int claimPoints() {
        return points;
    }
}