
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.tchu.Preconditions;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public class PublicGameState {

    private final int tickets;
    private final PublicCardState statePublic;
    private final PlayerId playerNowId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId playerLastId;

    /**
     * @param ticketsCount (Int): le nombre de tickets dans la pioche
     * @param cardState (PublicCardState): l'état des cartes (tailles de la défausse, pioche et les cartes retournées)
     * @param currentPlayerId (PlayerId): l'identité du joueur courant
     * @param playerState (Map<PlayerId, PublicPlayerState>): associe l'identité des joueurs à leur état publique
     * @param lastPlayer (PlayerId): l'identité du dernier joueur de la partie
     * @throws NullPointerException élément null dans le constructeur
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
            Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) throws NullPointerException {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);
        if(cardState == null || currentPlayerId == null) {
            throw new NullPointerException();
        }
        tickets = ticketsCount;
        statePublic = cardState;
        playerNowId = currentPlayerId ;
        playerLastId = lastPlayer;
        this.playerState = new TreeMap<>(playerState);
    }

    /**
     * @return : taille de la pioche de billets
     */
    public int ticketsCount() {
        return tickets;
    }

    /**
     * @return : true si la pioche de billet est non-vide, on peut en prendre des tickets.
     */
    public boolean canDrawTickets() {
        return tickets > 0;
    }

    /**
     * @return : la partie publique de l'état des cartes wagon/locomotive,
     */
    public PublicCardState cardState() {
        return statePublic;
    }

    /**
     * @return : retourne vrai ssi il est possible de tirer des cartes,
     * c-à-d si la pioche et la défausse contiennent entre elles au moins 5 cartes
     */
    public boolean canDrawCards() {
        return statePublic.deckSize() + statePublic.discardsSize() >= 5;
    }

    /**
     * @return : retourne l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return playerNowId;
    }

    /**
     * @param playerid (PlayerId) : identité du joueur
     * @return : retourne la partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerid) {
        return playerState.get(playerid);
    }

    /**
     * @return : retourne la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(playerNowId);
    }

    /**
     * @return : retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes(){
        List<Route> a = new ArrayList<>(playerState.get(playerNowId).routes());
        List<Route> b = new ArrayList<>(playerState.get(playerNowId.next()).routes());
        a.addAll(b);                                                      
        return a;
    }

    /**
     * @return : retourne l'identité du dernier joueur,
     * ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé
     */
    public PlayerId lastPlayer() {
        return playerLastId;
    }
}