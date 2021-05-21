package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTestOne {
    Random rd = new Random(12);

    Station geneve = new Station(4, "Geneve");
    Station lausanne = new Station(3, "Lausanne");
    Station fribourg =new Station(1, "Fribourg");
    Station nope = new Station(7, "meh");
    List<Route> routes = List.of(new Route("bjr" , lausanne,geneve, 4, Route.Level.OVERGROUND, Color.BLACK), new Route("tst", lausanne,fribourg, 5, Route.Level.UNDERGROUND, Color.BLACK ));

    List<Card> test = List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.GREEN, Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.RED, Card.BLACK, Card.BLACK);
    List<Card> forDeck = List.of(Card.ORANGE, Card.WHITE, Card.LOCOMOTIVE, Card.GREEN, Card.BLUE, Card.VIOLET, Card.LOCOMOTIVE, Card.RED, Card.BLACK, Card.YELLOW);
    List<Ticket> test1 = List.of(new Ticket(lausanne, geneve, 4), new Ticket(fribourg, geneve, 7), new Ticket(nope, nope, 5));
    SortedBag<Ticket> sortedTicket = SortedBag.of(test1);
    Deck<Ticket> ticket = Deck.of(SortedBag.of(test1), rd);

    SortedBag<Card> cards =  SortedBag.of(test);
    Deck<Card> pioche =  Deck.of(SortedBag.of(forDeck), rd);
    SortedBag<Ticket> tickets = SortedBag.of(test1);

    PlayerState player = new PlayerState(tickets, cards, routes);

    Map<PlayerId, PlayerState> map = new EnumMap<>(PlayerId.class);

    private Deck<Card> deck = Deck.of(Constants.ALL_CARDS, rd);

    private CardState cardState = CardState.of(pioche);

    private GameState state = GameState.initial(sortedTicket, rd);

    @Test
    void initial() {
        System.out.println(pioche.size());
        assertEquals(PlayerId.PLAYER_2, state.currentPlayerId());
        assertEquals(null, state.lastPlayer());
        assertEquals(tickets, state.topTickets(tickets.size()));
    }

    @Test
    void playerState() {
    }

    @Test
    void currentPlayerState() {
    }

    @Test
    void topTickets() {
    }

    @Test
    void withoutTopTickets() {
    }

    @Test
    void topCard() {
    }

    @Test
    void withoutTopCard() {
    }

    @Test
    void withMoreDiscardedCards() {
    }

    @Test
    void withCardsDeckRecreatedIfNeeded() {
    }

    @Test
    void withInitiallyChosenTickets() {
    }

    @Test
    void withChosenAdditionalTickets() {
    }

    @Test
    void withDrawnFaceUpCard() {
    }

    @Test
    void withBlindlyDrawnCard() {
    }

    @Test
    void withClaimedRoute() {
    }

    @Test
    void lastTurnBegins() {
    }

    @Test
    void forNextTurn() {
    }
}