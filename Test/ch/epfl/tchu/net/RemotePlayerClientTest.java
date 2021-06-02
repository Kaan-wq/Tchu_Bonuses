package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

class RemotePlayerClientTest {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println("Info received");
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println("State Updated !");
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println("Initial ticket settled !");
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return null;
        }

        @Override
        public TurnKind nextTurn() {
            return null;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            SortedBag<Ticket> choice = SortedBag.of(options.toList().subList(0, 3));
            System.out.println("Tickets choosen !");
            return choice;
        }

        @Override
        public int drawSlot() {
            System.out.println("Drawn from slot !");
            return 2;
        }

        @Override
        public Route claimedRoute() {
            return null;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return null;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return null;
        }

        @Override
        public void playSong(String song, int loop) {

        }

        @Override
        public void longest(List<Route> routesP1, List<Route> routesP2) { }
    }
}