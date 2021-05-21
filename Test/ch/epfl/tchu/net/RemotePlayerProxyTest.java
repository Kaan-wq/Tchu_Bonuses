package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Ticket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import static ch.epfl.tchu.game.PlayerId.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

class RemotePlayerProxyTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            SortedBag<Ticket> tickets = SortedBag.of(4, ChMap.tickets().get(1));
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");

            playerProxy.initPlayers(PLAYER_1, playerNames);
            playerProxy.setInitialTicketChoice(tickets);
            playerProxy.receiveInfo("F");

            playerProxy.chooseTickets(tickets);
            playerProxy.drawSlot();
        }
        System.out.println("Server done!");
    }
}