package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 *
 * Classe modélisant les routes du jeu.
 */

public final class Route {
    
    private final String id;
    private final int length;
    private final Level level;
    private final Color color;
    private final Station st1;
    private final Station st2;

    /**
     * Types de route dans le jeu, spécifique s'il s'agit d'une route en surface ou d'un tunnel.
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }
    
    /**
     * @param id : nom de la route, unique, peut pas être nulle.
     * @param station1 : station en bout de route.
     * @param station2 : station en bout de route
     * @param length : longueur de la route, peut pas être nulle, rélié au nombre de points acordé par sa construction.
     * @param level : définie la situation de la route.
     * @param color : couleur de la route, peut être neutre ( null).
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color){
        
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH);

        st1 = Objects.requireNonNull(station1);
        st2 =   Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.id = Objects.requireNonNull(id);
        this.length = length;

        this.color = color;
    }
    
    /**
     * @return : identifiant de la route.
     */
    public String id() {
        return id;
    }
    
    /**
     * @return : station rentrée en 1er dans le constructeur
     */
    public Station station1() {
        return st1;
    }

    /**
     * @return : station rentrée en 2nde dans le constructeur.
     */
    public Station station2() {
        return st2;
    }

    /**
     * @return : longueur de la route, entre 1 et 6.
     */
    public int length() { return length; }

    /**
     * @return : niveau de la route, sous-terre ou en surface.
     */
    public Level level() {
        return level;
    }

    /**
     * @return : couleur de la route, peut être null.
     */
    public Color color() {
        return color;
    }

    /**
     * @return : List avec les stations de la route, dans l'ordre donné dans le constructeur.
     */
    public List<Station> stations(){
        ArrayList<Station> a = new ArrayList<>();
        a.add(st1);
        a.add(st2);
        return a;
    }

    /**
     * @param station : station dont on recherche l'opposé, peut pas être null.
     * @return : l'autre station de la route.
     * @throws IllegalArgumentException : dans le cas où la station entrée ne fait pas partie de la route.
     */
    public Station stationOpposite(Station station) throws IllegalArgumentException {
        Preconditions.checkArgument(station.equals(st1) || station.equals(st2));

        if(station.equals(st1)) {
            return st2;
        }else{
            return st1;
        }
    }
    
    /**
     * @return : toutes les combinaisons possibles de cartes pour construire une route.
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        
        ArrayList<SortedBag<Card>> ordre = new ArrayList<>();

        if(color == null && level.equals(Level.UNDERGROUND)) {                    //cas neutre en tunnel
            for(int i = 0; i < length; i++) {

                int a = i;

                Card.CARS.forEach(
                        (e)->{
                            SortedBag.Builder<Card> combos = new SortedBag.Builder<>();
                            combos.add(length-a, e);
                            if(length-a != 0) {
                                combos.add(a, Card.LOCOMOTIVE);
                            }
                            ordre.add(combos.build());
                        });
            }

            SortedBag.Builder<Card> combos = new SortedBag.Builder<>();
            combos.add(length, Card.LOCOMOTIVE);
            ordre.add(combos.build());

        }else if(color == null && level.equals(Level.OVERGROUND)){                    //cas neutre en surface

            Card.CARS.forEach(
                    (e)->{
                        SortedBag.Builder<Card> combos = new SortedBag.Builder<>();
                        combos.add(length, e);
                        ordre.add(combos.build());
                    });

        }else if(color != null && level.equals(Level.OVERGROUND)){                    //cas couleur précise en surface
            SortedBag.Builder<Card> combos = new SortedBag.Builder<>();

            combos.add(length, Card.of(this.color));

            ordre.add(combos.build());

        }else if(color != null && level.equals(Level.UNDERGROUND)) {                  //cas couleur précise en tunnel
            for(int i = 0; i < length; i++) {

                SortedBag.Builder<Card> combos = new SortedBag.Builder<>();

                combos.add(length-i, Card.of(this.color));
                if(length-i != 0) {
                    combos.add(i, Card.LOCOMOTIVE);
                }
                ordre.add(combos.build());
            }
            SortedBag.Builder<Card> combos = new SortedBag.Builder<>();
            combos.add(length, Card.LOCOMOTIVE);
            ordre.add(combos.build());
        }
        return ordre;
    }

    /**
     * La méthode vérifie si les cartes piochées sont du même type que celles déposées par le joueur.
     * 
     * @param claimCards : cartes deposées par le jouer pour construire le tunnel.
     * @param drawnCards : cartes piochées du tas, déterminent les cartes additionelles à déposer par le joueur.
     * @return : nombre de cartes additionelles.
     * @throws IllegalArgumentException : s'il ne s'agit d'un tunnel ou que les cartes piochées ne sont pas suffisantes.
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) throws IllegalArgumentException{
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS );
        Preconditions.checkArgument(level.equals(Level.UNDERGROUND));
        
        int nbCartesAdditionnelles = 0;

        for(Card b : drawnCards) {
                if(claimCards.contains(b) || b.equals(Card.LOCOMOTIVE)) {
                    nbCartesAdditionnelles += 1;
            }
        }
        return nbCartesAdditionnelles;
    }

    /**
     * @return : nombre de points par la constructions de la route.
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length());
    }
}