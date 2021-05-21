package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public class PublicCardState {

    private final int sizeDeck;
    private final int sizeDiscard;
    private final List<Card> faceUpCards;

   /**
    * @param faceUpCards : Cartes avec la face visible, sur le bord du tableau.
    * @param deckSize : Taille de la pioche.
    * @param discardsSize : taille du tas de cartes non disponibles pour les joueurs.
    */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize ) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);
        
        sizeDeck = deckSize;
        sizeDiscard = discardsSize;
        this.faceUpCards = new ArrayList<>(faceUpCards);
    }
    
    /**
     * @return : nombre total de cartes dans le jeu.
     */
    public int totalSize() {
        return Constants.FACE_UP_CARDS_COUNT + sizeDeck + sizeDiscard;
    }
    
    /**
     * @return : une copie de la liste de cartes retournées du jeu.
     */
    public List<Card> faceUpCards(){
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        return new ArrayList<>(faceUpCards);
    }
    
    /**
     * @param slot : numéro de placement recherché dans les cartes retournées.
     * @return : carte retourné placé dans la place numero slot.
     * @throws IndexOutOfBoundsException : si le numéro de référence n'appartient pas à ceux de référence des places fictives des cartes soulevés.
     */
    public Card faceUpCard(int slot) throws IndexOutOfBoundsException{
        if(!Constants.FACE_UP_CARD_SLOTS.contains(slot)) {
            throw new IndexOutOfBoundsException();
        }
        return faceUpCards.get(slot);
    }

    /**
     * @return : taille de la pioche.
     */
    public int deckSize() {
        return sizeDeck;
    }

    /**
     * @return : taille du tas de cartes non disponibles pour les joueurs.
     */
    public int discardsSize() {
        return sizeDiscard;
    }
    
    /**
     * @return : true si la pioche est vide, sinon faux.
     */
    public boolean isDeckEmpty() {
        return deckSize() <= 0;
    }
}