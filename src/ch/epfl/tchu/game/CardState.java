package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 * 
 * Classe représentant l'état des différents pioche du jeu, partie privée avec toute l'information.
 */

public final class CardState extends PublicCardState{

    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    /**
     * Constructeur qui mets ne place les tas du jeu.
     * 
     * @param faceUpCards : liste de cartes à la face visible pour les joueurs
     * @param deck : pioche du jeu.
     * @param discard : tas des cartes non disponibles pour les joueurs
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discard){
        super(faceUpCards, deck.size(), discard.size());
        this.deck = deck;
        this.discard = discard;
    }
    
    /**
     * Mise en jeu de la partie, pour initialiser les premières cartes retournées.
     * 
     * @param deck : tas initiale avec toutes les cartes
     * @return : état du jeu avec 5 cartes retournées, la pioche complète et la défausse vide.
     * @throws IllegalArgumentException : si la pioche n'est pas assez grande pour retourné 5 cartes.
     */
    public static CardState of(Deck<Card> deck) throws IllegalArgumentException {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        List<Card> faceUp = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
        Deck<Card> deckWithoutTopCards = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);
        SortedBag<Card> discard = SortedBag.of();
        return new CardState(faceUp, deckWithoutTopCards, discard);
    }
    /**
     * État des cartes lorsqu'une carte visible est piochée.
     * 
     * @param slot : numéro de carte recherchée parmis les cartes retournées.
     * @return : état du jeu avec les nouvelles cartes retournées, et la pioche modifié.
     * @throws IndexOutOfBoundsException : si le numéro recherché est dehors des limites.
     * @throws IllegalArgumentException : si la pioche est vide.
     */
    public CardState withDrawnFaceUpCard(int slot) throws IndexOutOfBoundsException, IllegalArgumentException{
        Preconditions.checkArgument(!this.deck.isEmpty());
        if (!Constants.FACE_UP_CARD_SLOTS.contains(slot)) {
            throw new IndexOutOfBoundsException();
        }
        List<Card> faceUpCards = new ArrayList<>(this.faceUpCards());
        faceUpCards.set(slot, deck.topCard());
        return new CardState(faceUpCards, deck.withoutTopCard(), this.discard);
    }

    /**
     * @return : carte en haut de la pioche
     * @throws IllegalArgumentException : si la pioche est vide.
     */
    public Card topDeckCard() throws IllegalArgumentException{
        Preconditions.checkArgument(!this.deck.isEmpty());
        return this.deck.topCard();
    }

    /**
     * État des cartes lorsqu'une carte de la pioche est piochée.
     * 
     * @return : état des cartes avec la pioche sans la carte d'en haut.
     * @throws IllegalArgumentException : si la pioche est vide.
     */
    public CardState withoutTopDeckCard() throws IllegalArgumentException{
        Preconditions.checkArgument(!this.deck.isEmpty());
        return new CardState(this.faceUpCards(), this.deck.withoutTopCard(), this.discard);
    }
    
    /**
     * État des cartes avec nouvelle pioche si la pioche est vide, sinon reste
     * 
     * @param rng : valeur aléatoire pour mixer la défausse.
     * @return : état des cartes avec défausse vide et nouvelle pioche.
     * @throws IllegalArgumentException : si la pioche n'est pas vide.
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) throws IllegalArgumentException{
        Preconditions.checkArgument(this.deck.isEmpty());
        Deck<Card> shuffled = Deck.of(this.discard, rng);
        return new CardState(this.faceUpCards(), shuffled, SortedBag.of());
    }
    
    /**
     * État des cartes avec cartes rajoutées à la défausse.
     * 
     * @param additionalDiscards : cartes à rajouter à la défausse.
     * @return : état des cartes avec défausse mise à jour.
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(this.faceUpCards(), this.deck, this.discard.union(additionalDiscards));
    }
}