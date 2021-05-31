package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static ch.epfl.tchu.net.Serdes.*;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class RemotePlayerClient {

    private final Player player;
    private final String name;
    private final int port;
    private final String SPACE = " ";
    private final String V_SEPARATOR = ",";

    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * Méthode pour recevoir les informations du proxy (boucle quasi infini)
     */
    public void run() {
        try (Socket socket = new Socket(name, port);
             BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));
             BufferedWriter writer =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {
            String string;

            while((string = reader.readLine()) != null){
                List<String> received = Arrays.asList(string.split(Pattern.quote(SPACE),-1));
                String idMessage = received.get(0);

                switch(MessageId.valueOf(idMessage)){
                    case INIT_PLAYERS:
                        PlayerId ownId = SERDE_PLAYER_ID.deserialize(received.get(1));
                        Map<PlayerId, String> mapNames = new EnumMap<>(PlayerId.class);
                        List<String> names = Arrays.asList(received.get(2).split(Pattern.quote(V_SEPARATOR), -1));
                        mapNames.put(ownId, SERDE_STRING.deserialize(names.get(0)));
                        mapNames.put(ownId.next(), SERDE_STRING.deserialize(names.get(1)));
                        player.initPlayers(ownId, mapNames);
                        break;

                    case RECEIVE_INFO:
                        String info = SERDE_STRING.deserialize(received.get(1));
                        player.receiveInfo(info);
                        break;

                    case UPDATE_STATE:
                        PublicGameState newState = SERDE_PUBLIC_GAME_STATE.deserialize(received.get(1));
                        PlayerState ownState = SERDE_PLAYER_STATE.deserialize(received.get(2));
                        player.updateState(newState, ownState);
                        break;

                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = SERDE_SORTED_BAG_TICKET.deserialize(received.get(1));
                        player.setInitialTicketChoice(tickets);
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> ticket = player.chooseInitialTickets();
                        writer.write(SERDE_SORTED_BAG_TICKET.serialize(ticket) + '\n');
                        writer.flush();
                        break;

                    case NEXT_TURN:
                        Player.TurnKind action = player.nextTurn();
                        writer.write(SERDE_TURN_KIND.serialize(action) + '\n');
                        writer.flush();
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = SERDE_SORTED_BAG_TICKET.deserialize(received.get(1));
                        SortedBag<Ticket> chosen = player.chooseTickets(options);
                        writer.write(SERDE_SORTED_BAG_TICKET.serialize(chosen) + '\n');
                        writer.flush();
                        break;

                    case DRAW_SLOT:
                        int drawSlot = player.drawSlot();
                        writer.write(SERDE_INT.serialize(drawSlot) + '\n');
                        writer.flush();
                        break;

                    case ROUTE:
                        Route claimedRoute = player.claimedRoute();
                        writer.write(SERDE_ROUTE.serialize(claimedRoute) + '\n');
                        writer.flush();
                        break;

                    case CARDS:
                        SortedBag<Card> initialClaimCards = player.initialClaimCards();
                        writer.write(SERDE_SORTED_BAG_CARD.serialize(initialClaimCards) + '\n');
                        writer.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> option = SERDE_LIST_OF_SORTED_BAG_CARD.deserialize(received.get(1));
                        SortedBag<Card> chooseAdditionalCards = player.chooseAdditionalCards(option);
                        writer.write(SERDE_SORTED_BAG_CARD.serialize(chooseAdditionalCards) + '\n');
                        writer.flush();
                        break;
                    case SONG:
                        String song = SERDE_STRING.deserialize(received.get(1));
                        int loop = SERDE_INT.deserialize(received.get(2));
                        player.playSong(song, loop);
                        break;
                    case LONGEST:
                        List<Route> routesP1 = SERDE_LIST_ROUTE.deserialize(received.get(1));
                        List<Route> routesP2 = SERDE_LIST_ROUTE.deserialize(received.get(2));
                        player.longest(routesP1, routesP2);
                        break;
                }
            }
        }catch(IOException e){ throw new UncheckedIOException(e); }
    }
}