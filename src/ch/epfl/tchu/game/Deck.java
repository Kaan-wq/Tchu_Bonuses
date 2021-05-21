
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 * @param <C> : classe des éléments contenus dans le tas.
 */
public final class Deck <C extends Comparable<C>>{
    private final int size;
    private final List<C> basic;
    
    /**
     * Méthode pour faire des tas mélangé de façon aléatoire.
     * 
     * @param <C> : type de cartes composant le tas.
     * @param cards : Multigroupe de cartes organisé.
     * @param rng : valeur aléatoire.
     * @return : Multigroupe de cartes mélangé de façon aléatoire sous forme de tas.
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> toShuffle = cards.toList();
        Collections.shuffle(toShuffle, rng);
      
        return new Deck<>(toShuffle);
    }
    
    /**
     * Constructeur privé de la classe, pour faire des tas.
     * 
     * @param cards : liste de cartes sans ordre particulier.
     */
    private Deck(List<C> cards) {
        basic = new ArrayList<>(cards);
        size = basic.size();
    }
    
    /**
     * @return : nombres de cartes dans le tas.
     */
    public int size() {
        return this.size;
    }

    /**
     * @return : vrai que si le tas ne possède aucun élément, sinon faux.
     */
    public boolean isEmpty() {
        return !(this.size() > 0);
    }

    /**
     * @return : carte supérieur du tas.
     */
    public C topCard() {
        Preconditions.checkArgument(this.size()>0);
        return this.basic.get(0);
    }

    /**
     * @return : nouveau tas sans la carte supérieure du tas existant précédemment.
     */
    public Deck<C> withoutTopCard(){   
        Preconditions.checkArgument(this.size()>0);
        
        return subDeck(1, basic.size());
    }

    /**
     * @param count : nombres de cartes d'en haut du tas.
     * @return : multiensemble des count cartes supérieures du tas.
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(count >= 0 && count <= this.size());
        
        SortedBag.Builder<C> topOnes = new SortedBag.Builder<>();
        List<C> sub = basic.subList(0, count);

        sub.forEach(topOnes::add);

        return topOnes.build();
    }
    
    /**
     * @param count : nombre de cartes a enlever d'en haut du tas pour créer un nouveau
     * @return : nouveau tas sans les count cartes supérieures.
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(count >= 0 && count <= this.size());
        
        return subDeck(count, basic.size());
    }

    /**
     * Retourne un nouveau tas avec les bornes définies, le dernier élément n'est pas pris en compte
     * 
     * @param debut : position de la première carte du nouveau tas dans le tas existant.
     * @param fin : position suivante à la dernière carte nouveau tas dans le tas existant.
     * @return : nouveau tas avec moin de cartes que le premier. 
     */
    private Deck<C> subDeck(int debut, int fin){

        List<C> sub = basic.subList(debut, fin);
        return new Deck<>(sub);
    }
}