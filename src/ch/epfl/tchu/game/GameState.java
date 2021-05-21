
package ch.epfl.tchu.game;


import java.util.EnumMap;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> billets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> mape;
    
    /**
     * @param cardState : état de cartes, contient la pioche, la défausse et les cartes retournées.
     * @param currentPlayerId : identifiant du joueur qui à le droit de faire une action à ce tour.
     * @param lastPlayer : identifiant du joueur qui jouera en dernier la partie.
     * @param playerRealState : map qui mets en lien les identifiants des joueurs avec leurs mains.
     * @param billets : pioche des billets.
     */
    private GameState(CardState cardState, PlayerId currentPlayerId,
            PlayerId lastPlayer, Map<PlayerId, PlayerState> playerRealState,
            Deck<Ticket> billets){
        super(billets.size(), cardState, currentPlayerId, fullMakePublic(playerRealState),
                lastPlayer);
        this.cardState = cardState;
        this.billets = billets;
        mape = playerRealState;
    }
    
    /**
     * @param tickets : pioche de billets initiales.
     * @param rd : valeur aléatoire pour mélanger les différents tas et choisir le premier joueur.
     * @return : état du jeu initial avec tous ces composants, identifiants des joueurs, leurs mains et l'état global des cartes.
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rd) {
        Deck<Ticket> billets = Deck.of(tickets, rd);
        Map<PlayerId, PlayerState> mape = new EnumMap<>(PlayerId.class);
        
        //choix premier joueur 
        PlayerId idFirst = PlayerId.ALL.get(rd.nextInt(2));
                       
        //mélange des cartes et suppresion des 8 premières
        Deck<Card> cards = Deck.of(Constants.ALL_CARDS, rd);
       
        for(PlayerId a : PlayerId.ALL){
            mape.put(a, PlayerState.initial(cards.topCards(4)));
            cards= cards.withoutTopCards(4);
        }
        
        CardState state = CardState.of(cards);
               
        return new GameState(state, idFirst, null, mape , billets);
    }
    
    /**
     * @param playerId : identifiant du joueur.
     * @return : état complet d'un joueur.
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return mape.get(playerId);
    }
    
    /**
     * @return : état complet du joueur désiré.
     */
    @Override
    public PlayerState currentPlayerState() {
        return mape.get(currentPlayerId());
    }
    
    /**
     * @param count : nombre de billets à retirer du haut de la pioche.
     * @return : multiensemble des count cartes du haut de la pioche de billets.
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= billets.size());
        
        return billets.topCards(count); 
    }
    
    /**
     * @param count : nombre de billets à retirer du haut de la pioche.
     * @return : état du jeu avec la pioche de billet réduit des count billets du haut de la pioche.
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count>0 && count<=super.ticketsCount());
        return new GameState(this.cardState, currentPlayerId(), lastPlayer(), mape,billets.withoutTopCards(count));
    }
    
    /**
     * @return : carte du haut de la pioche.
     */
    public Card topCard() {
        Preconditions.checkArgument(super.cardState().deckSize()>0);
        return cardState.topDeckCard();
    }
    
    /**
     * @return : état du jeu sans la carte du haut de la pioche.
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(super.cardState().deckSize()>0);
        return new GameState(cardState.withoutTopDeckCard(), currentPlayerId(), lastPlayer(), mape, billets);
    }
    
    /**
     * @param deckS : cartes à rajouter à la défausse.
     * @return : état du jeu avec la défausse élargie.
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> deckS) {
        return new GameState(cardState.withMoreDiscardedCards(deckS),currentPlayerId(), lastPlayer(), mape, billets);
    }
    
    /**
     * @param random : valeur aléatoire pour mélanger la défausse si nécessaire.
     * @return : état du jeu avcec nouvelle pioche si elle était vide au paravant, sinon retourne meme état de jeu.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random random) {
        if(cardState.isDeckEmpty()) {
            return new GameState(cardState.withDeckRecreatedFromDiscards(random),currentPlayerId(), lastPlayer(), mape, billets);
        }else {
            return this;
        }
    }
    
    /**
     * @param id : identifiant du joueur qui recevra les tickets choisis.
     * @param chosenOne : billets choisi par le joueur qui s'ajouterons à sa main.
     * @return : état du jeu avec nouveau état de la main du joueur choisi.
     */
    public GameState withInitiallyChosenTickets(PlayerId id, SortedBag<Ticket> chosenOne) {
        Preconditions.checkArgument(super.playerState(id).ticketCount()<1);
        Map<PlayerId, PlayerState> nuevoState = new EnumMap<>(PlayerId.class);
        
        for(PlayerId a: PlayerId.ALL) {
            if(a==id) {
                nuevoState.put(a, mape.get(a).withAddedTickets(chosenOne));   
            }else {
                nuevoState.put(a, mape.get(a));
            }
        }
        return new GameState(cardState, currentPlayerId(), lastPlayer(), nuevoState, billets);
    }
    
    /**
     * @param drawnTickets : multiensemble de tickets tirés de la pioche.
     * @param chosenTickets : multiensemble de tickets tirés de la pioche.
     * @return : état du jeu avec nouveau état de la main du joueur actuel.
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        
        Map<PlayerId, PlayerState> nuevoState = new EnumMap<>(PlayerId.class);
        for(PlayerId a: PlayerId.ALL) {
            if(a==currentPlayerId()) {
                nuevoState.put(a, mape.get(a).withAddedTickets(chosenTickets));   
            }else {
                nuevoState.put(a, mape.get(a));
            }
        }
        return new GameState(cardState,currentPlayerId(), lastPlayer(), nuevoState, billets.withoutTopCards(drawnTickets.size()));
    }
    
    /**
     * @param slot : emplacement de la carte retourné désiré
     * @return : état du jeu avec la pioche modifié, ainsi que les cartes retournées, et la main du joueur actuel.
     */
    public GameState withDrawnFaceUpCard(int slot) {
        
        Map<PlayerId, PlayerState> nuevoState = new EnumMap<>(PlayerId.class);
        for(PlayerId a: PlayerId.ALL) {
            if(a==currentPlayerId()) {
                nuevoState.put(a, mape.get(a).withAddedCard(cardState.faceUpCard(slot)));   
            }else {
                nuevoState.put(a, mape.get(a));
            }
        }
        return new GameState(cardState.withDrawnFaceUpCard(slot), currentPlayerId(), lastPlayer(), nuevoState, billets);
    }
    
    /**
     * @return : état du jeu avec la carte du haut de la pioche mise dans la main du joueur courrant, pioche modifiée.
     */
    public GameState withBlindlyDrawnCard() {
        
        Map<PlayerId, PlayerState> nuevoState = new EnumMap<>(PlayerId.class);
        for(PlayerId a: PlayerId.ALL) {
            if(a==currentPlayerId()) {
                nuevoState.put(a, mape.get(a).withAddedCard(cardState.topDeckCard()));   
            }else {
                nuevoState.put(a, mape.get(a));
            }
        }
        return new GameState(cardState.withoutTopDeckCard(), currentPlayerId(), lastPlayer(), nuevoState, billets);
    }
    
    /**
     * @param route : route construite par le joueur courant.
     * @param cards : cartes utilisées pour construire cette route
     * @return : état du jeu avec mise à jour de la main du joueur courrant qui à construit une nouvelle route, avec les cartes fournies qui vont à la défausse.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> nuevoState = new EnumMap<>(PlayerId.class);
        for(PlayerId a: PlayerId.ALL) {
            if(a==currentPlayerId()) {
                nuevoState.put(a, mape.get(a).withClaimedRoute(route, cards));   
            }else {
                nuevoState.put(a, mape.get(a));
            }
        }
        return new GameState(cardState.withMoreDiscardedCards(cards),currentPlayerId(), lastPlayer(), nuevoState, billets);
    }
    
    /**
     * @return : true si un des joueurs a moins de 2 wagons, déclanche le dernier tour de la partie.
     */
    public boolean lastTurnBegins() {
        return mape.get(currentPlayerId()).carCount() <= 2 && lastPlayer() == null;
    }
    
    /**
     * @return : état du jeu qui change le joueur courrant, marque qui sera le dernier joueur si lastTurnBegins() est true.
     */
    public GameState forNextTurn() {
        if(lastTurnBegins()) {
            return new GameState(cardState,currentPlayerId().next(), currentPlayerId(), mape, billets);
        }else {
            return new GameState(cardState,currentPlayerId().next(), lastPlayer(), mape, billets);
        }
    }

    /**
     * @param goodOne : map avec les états complets des joueurs et leur identifiants.
     * @return : map avec les états publiques des joueurs et leur identifiant. (pas de partie privée)
     */
    private static Map<PlayerId , PublicPlayerState> fullMakePublic(Map<PlayerId, PlayerState> goodOne){
        Map<PlayerId , PublicPlayerState> aRendre = new EnumMap<>(PlayerId.class);

        PlayerId.ALL.forEach(a-> aRendre.put(a, new PublicPlayerState (goodOne.get(a).ticketCount(), goodOne.get(a).cardCount(), goodOne.get(a).routes())));

        return aRendre;
    }
}