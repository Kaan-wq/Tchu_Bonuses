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
    private final BlockingQueue<SortedBag<Ticket>> TICKETS_QUEUE;
    private final BlockingQueue<TurnKind> TURN_QUEUE;
    private final BlockingQueue<Integer> DRAW_SLOT_QUEUE;
    private final BlockingQueue<Route> ROUTE_QUEUE;
    private final BlockingQueue<SortedBag<Card>> BAG_CARD_QUEUE;

    public GraphicalPlayerAdapter(){
        TICKETS_QUEUE =  new ArrayBlockingQueue<>(1);
        TURN_QUEUE = new ArrayBlockingQueue<>(1);
        DRAW_SLOT_QUEUE = new ArrayBlockingQueue<>(1);
        ROUTE_QUEUE = new ArrayBlockingQueue<>(1);
        BAG_CARD_QUEUE = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) { runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames)); }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) { runLater(() -> graphicalPlayer.setState(newState, ownState)); }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            runLater(() -> graphicalPlayer.chooseTickets(tickets, TICKETS_QUEUE::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try{
            return TICKETS_QUEUE.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(
                () -> TURN_QUEUE.add(TurnKind.DRAW_TICKETS),
                (drawSlot) ->{
                    TURN_QUEUE.add(TurnKind.DRAW_CARDS);
                    DRAW_SLOT_QUEUE.add(drawSlot);
                },
                (route, claimCards) ->{
                    TURN_QUEUE.add(TurnKind.CLAIM_ROUTE);
                    ROUTE_QUEUE.add(route);
                    BAG_CARD_QUEUE.add(claimCards);
                }
        ));

        try{
            return TURN_QUEUE.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        if(DRAW_SLOT_QUEUE.size() > 0){
            return DRAW_SLOT_QUEUE.poll();
        }else{
            runLater(() -> graphicalPlayer.drawCard(DRAW_SLOT_QUEUE::add));
            try{
                return DRAW_SLOT_QUEUE.take();
            }catch(InterruptedException exception){throw new Error();}
        }
    }

    @Override
    public Route claimedRoute() {
        try{
            return ROUTE_QUEUE.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try{
            return BAG_CARD_QUEUE.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, BAG_CARD_QUEUE::add));
        try{
            return BAG_CARD_QUEUE.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public void playSong(String song, int loop) { runLater(() -> graphicalPlayer.playSong(song, loop)); }
}