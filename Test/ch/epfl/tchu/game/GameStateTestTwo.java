package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTestTwo {

    static SortedBag<Ticket> tickets = new SortedBag.Builder<Ticket>().add(new Ticket(new Station(1, "Station1"), new Station(2, "Station2"), 15))
            .add(new Ticket(new Station(3, "Station3"), new Station(4, "Station4"), 15))
            .add(new Ticket(new Station(5, "Station5"), new Station(6, "Station6"), 15))
            .add(new Ticket(new Station(7, "Station7"), new Station(8, "Station8"), 15))
            .add(new Ticket(new Station(9, "Station9"), new Station(10, "Station10"), 15))
            .build();

    static SortedBag<Card> cards = SortedBag.of(8, Card.BLUE, 5, Card.RED);

    static Station st1 = new Station(0, "st1");
    static Station st2 = new Station(1,"st2");
    static Station st3 = new Station(0, "st3");
    static Station st4 = new Station(1,"st4");
    static Station st5 = new Station(0, "st5");
    static Station st6 = new Station(1,"st6");
    static Route tester1 = new Route("try", st1 , st2,2, Route.Level.UNDERGROUND, Color.BLACK);
    static Route tester2 = new Route("try", st3 , st4,2, Route.Level.UNDERGROUND, Color.BLACK);
    static Route tester3 = new Route("try", st5 , st6,2, Route.Level.UNDERGROUND, Color.BLACK);
    static List<Route> routeList = new ArrayList<>();

    static Random rng = new Random(0);
    static GameState initialGamestate = GameState.initial(tickets, rng);


    @Test
    void ticketsCount() {
    }

    @Test
    void canDrawTickets() {
    }

    @Test
    void cardState() {
    }

    @Test
    void canDrawCards() {
    }

    @Test
    void currentPlayerId() {
    }

    @Test
    void playerState() {
        for(PlayerId playerId : PlayerId.ALL){
            System.out.println(initialGamestate.playerState(playerId));
        }

    }

    @Test
    void currentPlayerState() {
    }

    @Test
    void claimedRoutes() {
    }

    @Test
    void lastPlayer() {
    }

    @Test
    void initial() {
        int supposedDeckDize = Constants.ALL_CARDS.size() - 8;
        SortedBag<Ticket> tickets = new SortedBag.Builder<Ticket>().add(new Ticket(new Station(1, "Station1"), new Station(2, "Station2"), 15))
                .add(new Ticket(new Station(3, "Station3"), new Station(4, "Station4"), 15))
                .add(new Ticket(new Station(5, "Station5"), new Station(6, "Station6"), 15))
                .add(new Ticket(new Station(7, "Station7"), new Station(8, "Station8"), 15))
                .add(new Ticket(new Station(9, "Station9"), new Station(10, "Station10"), 15))
                .build();
        Random rng = new Random(0);
        GameState initialGamestate = GameState.initial(tickets, rng);
        assertEquals(supposedDeckDize, initialGamestate.cardState().totalSize());
        for (PlayerId player : PlayerId.ALL){
            assertEquals(4, initialGamestate.playerState(player).cardCount());
        }
    }

    @Test
    void testPlayerState() {
    }

    @Test
    void testCurrentPlayerState() {
    }

    @Test
    void topTickets() {
    }

    @Test
    void withoutTopTickets() {
        int expectedSize = initialGamestate.ticketsCount() - 2;
        GameState withoutTopCardGameState = initialGamestate.withoutTopTickets(2);
        assertEquals(expectedSize, withoutTopCardGameState.ticketsCount());
    }

    @Test
    void topCard() {


    }

    @Test
    void withoutTopCard() {
        int expectedSize = initialGamestate.cardState().totalSize() - 1;
        GameState withoutTopCardGameState = initialGamestate.withoutTopCard();
        assertEquals(expectedSize, withoutTopCardGameState.cardState().totalSize());
    }

    @Test
    void withMoreDiscardedCards() {
        SortedBag<Card> addCards = SortedBag.of(6, Card.LOCOMOTIVE);
        int expectedSize = initialGamestate.cardState().discardsSize() + 6;
        GameState gameStateWithAddedCards = initialGamestate.withMoreDiscardedCards(addCards);
        assertEquals(expectedSize, gameStateWithAddedCards.cardState().discardsSize());
    }

    @Test
    void withCardsDeckRecreatedIfNeeded() {
        assertEquals(initialGamestate, initialGamestate.withCardsDeckRecreatedIfNeeded(rng));

    }

    @Test
    void withInitiallyChosenTickets() {
        for(PlayerId playerId : PlayerId.ALL){
            GameState gameStateTest = initialGamestate.withInitiallyChosenTickets(playerId, tickets);
            assertEquals(5, gameStateTest.playerState(playerId).ticketCount());
        }
        for(PlayerId playerId : PlayerId.ALL) {
            GameState gameStateTest2 = initialGamestate.withInitiallyChosenTickets(playerId, tickets);
            assertThrows(IllegalArgumentException.class, () -> {
                gameStateTest2.withInitiallyChosenTickets(playerId, tickets);
            });
        }

    }

    @Test
    void withChosenAdditionalTickets() {
        SortedBag<Ticket> ticketsChosen = new SortedBag.Builder<Ticket>()
                .add(new Ticket(new Station(1, "Station1"), new Station(2, "Station2"), 15))
                .add(new Ticket(new Station(3, "Station3"), new Station(4, "Station4"), 15))
                .add(new Ticket(new Station(5, "Station5"), new Station(6, "Station6"), 15))
                .build();
        /*SortedBag<Ticket> emptyTicket = SortedBag.of();
        assertThrows(IllegalArgumentException.class, () -> {
            initialGamestate.withChosenAdditionalTickets(emptyTicket, tickets);
        });*/
        GameState gameStateWithAddedTickets = initialGamestate.withChosenAdditionalTickets(tickets, ticketsChosen);
        assertEquals(3, gameStateWithAddedTickets.currentPlayerState().ticketCount());
    }

    @Test
    void withDrawnFaceUpCard() {
        GameState gameStateWithDrawnFaceUpCards = initialGamestate.withDrawnFaceUpCard(0);
        assertEquals(initialGamestate.currentPlayerState().cardCount()+1, gameStateWithDrawnFaceUpCards.currentPlayerState().cardCount());
        //assertEquals(initialGamestate.cardState().faceUpCards.size() -1 , gameStateWithDrawnFaceUpCards.cardState().faceUpCards.size());

    }

    @Test
    void withBlindlyDrawnCard(){
        GameState gameStateWithBlindlyDrawnCard = initialGamestate.withBlindlyDrawnCard();
        assertEquals(initialGamestate.currentPlayerState().cardCount()+1, gameStateWithBlindlyDrawnCard.currentPlayerState().cardCount());
        assertEquals(initialGamestate.cardState().totalSize() -1 , gameStateWithBlindlyDrawnCard.cardState().totalSize());
    }
    @Test
    void withClaimedRoute(){
        GameState gameStateWithARouteClaimed = initialGamestate.withClaimedRoute(tester1, cards);
        assertEquals(1, gameStateWithARouteClaimed.currentPlayerState().routes().size());
        assertEquals(initialGamestate.cardState().discardsSize() + cards.size(), gameStateWithARouteClaimed.cardState().discardsSize());
    }
}