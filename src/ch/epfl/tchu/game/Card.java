package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 *
 *Classe du type de cartes de jeu, chacune avec en attribut sa couleur respective ou null dans le cas contraire.
 *Le jeu possède 9 types de cartes différentes, 8 wagons différents et 1 locomotive.
 */

public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);
    
    private final Color coul;
    
    private Card(Color co) {
        this.coul = co;
    }
    
    /**
     * Liste contenant tous les éléments de la classe dans l'ordre donné.
     */
    public final static List<Card> ALL = List.of(Card.values());
    
    /**
     * taille de la liste de tous les éléments de la classe.
     */
    public final static int COUNT= ALL.size();

    /**
     * liste avec uniquement les wagons du jeu.
     */
    public final static List<Card> CARS = ALL.subList(0, Color.COUNT);
    
    /**
     * @param color : couleur rechercher parmis les cartes
     * @return : type de carte correspondant à la couleur entrée.
     */
    public static Card of(Color color) {
        Card a = null;
        for(Card card: CARS) {
            if(card.color().equals(color)) {
                a = card;
            }
        }
        return a;
    }

    /**
     * @return : couleur de la carte correspondante. ( this )
     */
    public Color color() {
        return this.coul;
    }
}