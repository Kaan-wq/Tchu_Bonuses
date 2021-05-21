package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class RemotePlayerProxy implements Player {

    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final String SPACE = " ";

    public RemotePlayerProxy(Socket socket){
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){
        Map playerNamesEnumMap = new EnumMap(playerNames);
        List<String> listInfos = List.of(SERDE_PLAYER_ID.serialize(ownId), SERDE_LIST_STRING.serialize(new ArrayList<>(playerNamesEnumMap.values())));
        sendInfo(INIT_PLAYERS, listInfos);
    }

    @Override
    public void receiveInfo(String info) {
        List<String> listInfo = List.of(SERDE_STRING.serialize(info));
        sendInfo(RECEIVE_INFO, listInfo);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState){
        List<String> listInfos = List.of(SERDE_PUBLIC_GAME_STATE.serialize(newState), SERDE_PLAYER_STATE.serialize(ownState));
        sendInfo(UPDATE_STATE, listInfos);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        List<String> listInfos = List.of(SERDE_SORTED_BAG_TICKET.serialize(tickets));
        sendInfo(SET_INITIAL_TICKETS, listInfos);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() { return Serdes.SERDE_SORTED_BAG_TICKET.deserialize(sendInfoAndReceive(CHOOSE_INITIAL_TICKETS, List.of())); }

    @Override
    public TurnKind nextTurn() { return SERDE_TURN_KIND.deserialize(sendInfoAndReceive(NEXT_TURN, List.of())); }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<String> listInfos = List.of( Serdes.SERDE_SORTED_BAG_TICKET.serialize(options));
        return SERDE_SORTED_BAG_TICKET.deserialize(sendInfoAndReceive(CHOOSE_TICKETS, listInfos));
    }

    @Override
    public int drawSlot() {
        return SERDE_INT.deserialize(sendInfoAndReceive(DRAW_SLOT, List.of()));
    }

    @Override
    public Route claimedRoute() {
        return SERDE_ROUTE.deserialize(sendInfoAndReceive(ROUTE, List.of()));
    }

    @Override
    public SortedBag<Card> initialClaimCards() { return SERDE_SORTED_BAG_CARD.deserialize(sendInfoAndReceive(CARDS, List.of())); }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        List<String> listInfos = List.of(Serdes.SERDE_LIST_OF_SORTED_BAG_CARD.serialize(options));
        return SERDE_SORTED_BAG_CARD.deserialize(sendInfoAndReceive(CHOOSE_ADDITIONAL_CARDS, listInfos));
    }

    /**
     * Méthode pour créer les messages à partir du MessageId et d'une liste
     *
     * @param message (MessageId) : le MessageId correspondant
     * @param listInfo (List<String>) : la liste des éléments à envoyer
     * @return le message à envoyer
     */
    private String makeInfoString(MessageId message, List<String> listInfo ){ return message.name() + SPACE + String.join(SPACE, listInfo) + '\n'; }

    /**
     * Méthode privée pour envoyer des informations seulement
     *
     * @param message (MessageId) . le MessageId correspondant
     * @param listInfo (List<String>) : la liste des éléments à envoyer
     */
    private void sendInfo(MessageId message, List<String> listInfo ){
        try{
            writer.write(makeInfoString(message, listInfo));
            writer.flush();
        }catch (IOException e){ throw new UncheckedIOException(e); }
    }

    /**
     * Méthode privée pour envoyer les informations et aussi en recevoir (Utilse sendInfo)
     *
     * @param message (MessageId) : le messageId correspondant
     * @param listInfo (List<String>) : la liste des éléments à envoyer
     * @return la réponse du client
     */
    private String sendInfoAndReceive(MessageId message, List<String> listInfo){
        try{
            sendInfo(message, listInfo);
            return reader.readLine();
        }
        catch(IOException ex){ throw new UncheckedIOException(ex); }
    }
}