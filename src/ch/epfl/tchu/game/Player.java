package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 *
 * L'interface qui modélise le joueur et les actions qu'il peut entreprendre
 */

public interface Player {
    enum TurnKind{

        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * Qui est appelée au début de la partie pour communiquer au joueur sa propre identité ownId,
     * ainsi que les noms des différents joueurs, le sien inclus, qui se trouvent dans playerNames.
     * @param   ownId (PlayerId) : Le joueur à initialiser
     * @param   playerNames (Map) : la map qui associe les id des joueurs à leur nom
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Qui est appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie;
     * cette information est donnée sous la forme d'une chaîne de caractères, généralement produite par la classe Info.
     * @param info (String) : information donnée au joueur
     */
    void receiveInfo(String info);

    /**
     * Qui est appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique
     * de ce nouvel état, newState, ainsi que de son propre état, ownState.
     * @param newState (PublicGameState) : nouvel état publique de la partie
     * @param ownState (PlayerState) : nouvel état du joueur
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Qui est appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués.
     * @param tickets (SortedBag<Ticket>) : les cinq billets distribués
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Qui est appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui
     * a distribué initialement (via la méthode précédente) il garde.
     * @return les billets gardés par le joueur
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Qui est appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour
     * @return le type d'action que veut effectuer le joueur
     */
    TurnKind nextTurn();

    /**
     * Qui est appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde
     * @param options (SortedBag<Ticket>) : les billets supplémentaires tirés
     * @return les billets qu'il garde
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * qui est appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer: d'un des emplacements contenant une
     * carte face visible—auquel cas la valeur retourne est comprise entre 0 et 4 inclus —,
     * ou de la pioche—auquel cas la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1)
     * @return la valeur correspondant à l'emplacement d'où il à choisi de tirer la carte
     */
    int drawSlot();

    /**
     * Qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir de quelle route il s'agit
     * @return la route dont il a tenté de s'emparer
     */
    Route claimedRoute();

    /**
     *  Qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     *  afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela
     * @return les cartes que le joueur décide d'utiliser afin de s'emparer d'une route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Qui est appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes
     * additionnelles sont nécessaires, afin de savoir quelle(s) carte(s) il désire utiliser pour cela,
     * les possibilités lui étant passées en argument; si le multiensemble retourné est vide, cela signifie
     * que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités.
     * @param options (List<SortedBag<Card>>) : les possiblités de choix pour les cartes additionnelles
     * @return le multiensemble de cartes choisi pour s'emparer du tunnel
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Méthode pour passer un son
     * @param song (String) : le son à passer
     * @param loop
     */
    void playSong(String song, int loop);

    /**
     * Longest
     * @param routesP1 (List<Route>)
     * @param routesP2 (List<Route>)
     */
    void longest(List<Route> routesP1, List<Route> routesP2);
}