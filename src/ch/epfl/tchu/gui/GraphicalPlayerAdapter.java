package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public class GraphicalPlayerAdapter implements Player{

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<TurnKind> turnQueue;
    private final BlockingQueue<Integer> drawSlotQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<SortedBag<Card>> bagCardQueue;

    /**
     * Constructeur qui se charge d'instancier les BlockingQueue
     */
    public GraphicalPlayerAdapter(){
        ticketsQueue =  new ArrayBlockingQueue<>(1);
        turnQueue = new ArrayBlockingQueue<>(1);
        drawSlotQueue = new ArrayBlockingQueue<>(1);
        routeQueue = new ArrayBlockingQueue<>(1);
        bagCardQueue = new ArrayBlockingQueue<>(1);
    }

    /**
     * Instanciation du GraphicalPlayer
     * @param   ownId (PlayerId) : Le joueur à initialiser
     * @param   playerNames (Map) : la map qui associe les id des joueurs à leur nom
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Appel à la méthode receiveInfo de GraphicalPlayer
     * @param info (String) : information donnée au joueur
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Appel à la méthode setState de GraphicalPlayer
     * @param newState (PublicGameState) : nouvel état publique de la partie
     * @param ownState (PlayerState) : nouvel état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) { runLater(() -> graphicalPlayer.setState(newState, ownState)); }

    /**
     * Appel à la méthode chooseTickets de GraphicalPlayer
     * @param tickets (SortedBag<Ticket>) : les cinq billets distribués
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsQueue::add));
    }

    /**
     * @return (SortedBag<Ticket>) : le choix des tickets initiaux pris de la BlockingQueue
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() { return queueTake(ticketsQueue); }

    /**
     * Ajoute les éléments nécessaires à la réalisation du tour dans les BlockingQueue corespondantes
     * @return (TurnKind) : le type de tour
     */
    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(
                () -> turnQueue.add(TurnKind.DRAW_TICKETS),
                (drawSlot) ->{
                    turnQueue.add(TurnKind.DRAW_CARDS);
                    drawSlotQueue.add(drawSlot);
                },
                (route, claimCards) ->{
                    turnQueue.add(TurnKind.CLAIM_ROUTE);
                    routeQueue.add(route);
                    bagCardQueue.add(claimCards);
                }
        ));

        return queueTake(turnQueue);
    }

    /**
     * Simple appel setInitialTicketChoice et chooseInitialTickets
     * @param options (SortedBag<Ticket>) : les billets supplémentaires tirés
     * @return (SortedBag<Ticket>) : le choix des tickets supplémentaires à tirer
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    /**
     * @return (int) : le slot d'où l'on tire la carte
     */
    @Override
    public int drawSlot() {
        if(drawSlotQueue.size() > 0){
            return drawSlotQueue.poll();
        }else{
            runLater(() -> graphicalPlayer.drawCard(drawSlotQueue::add));
            return queueTake(drawSlotQueue);
        }
    }

    /**
     * @return (Route) : la route dont on vient de s'emparer
     */
    @Override
    public Route claimedRoute() { return queueTake(routeQueue); }

    /**
     * @return (SortedBag<Card>) : les cartes initiales pour s'emparer d'une route
     */
    @Override
    public SortedBag<Card> initialClaimCards() { return queueTake(bagCardQueue); }

    /**
     * @param options (List<SortedBag<Card>>) : les possiblités de choix pour les cartes additionnelles
     * @return (SortedBag<Card>) : les cartes additionnelles choisies
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, bagCardQueue::add));
        try{
            return bagCardQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    /**
     * Joue un son
     * @param song (String) : le son à passer
     * @param loop (int) : la durée du son
     */
    @Override
    public void playSong(String song, int loop) { runLater(() -> graphicalPlayer.playSong(song, loop)); }

    /**
     * Set le longest
     * @param routesP1 (List<Route>) : longest du premier joueur
     * @param routesP2 (List<Route>) : longest du deuxième joueur
     */
    @Override
    public void longest(List<Route> routesP1, List<Route> routesP2) { runLater(() -> graphicalPlayer.longest(routesP1, routesP2)); }

    /**
     * Méthode générique permettant de prendre les éléments d'une BlockingQueue
     *
     * @param queue (BlockingQueue<T>) : la BlockingQueue en question
     * @param <T> : le type d'objet que contenu dans la BlockingQueue
     * @return (T) : l'objet contenu dans la BlockingQueue
     */
    private <T> T queueTake(BlockingQueue<T> queue){
        try{
            return queue.take();
        }catch (InterruptedException e){ throw new Error(); }
    }
}