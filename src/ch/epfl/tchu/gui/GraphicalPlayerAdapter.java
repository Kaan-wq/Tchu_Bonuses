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

    public GraphicalPlayerAdapter(){
        ticketsQueue =  new ArrayBlockingQueue<>(1);
        turnQueue = new ArrayBlockingQueue<>(1);
        drawSlotQueue = new ArrayBlockingQueue<>(1);
        routeQueue = new ArrayBlockingQueue<>(1);
        bagCardQueue = new ArrayBlockingQueue<>(1);
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
            runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsQueue::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try{
            return ticketsQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }

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

        try{
            return turnQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        if(drawSlotQueue.size() > 0){
            return drawSlotQueue.poll();
        }else{
            runLater(() -> graphicalPlayer.drawCard(drawSlotQueue::add));
            try{
                return drawSlotQueue.take();
            }catch(InterruptedException exception){throw new Error();}
        }
    }

    @Override
    public Route claimedRoute() {
        try{
            return routeQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try{
            return bagCardQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, bagCardQueue::add));
        try{
            return bagCardQueue.take();
        }catch(InterruptedException exception){throw new Error();}
    }
}