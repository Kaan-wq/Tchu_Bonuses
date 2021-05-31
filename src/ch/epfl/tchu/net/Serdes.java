package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.game.Player.TurnKind;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 */

public final class Serdes {

    private Serdes(){}

    private static final String V_SEPARATOR = ",";
    private static final String PV_SEPARATOR = ";";
    private static final String P_SEPARATOR = ":";

    public static final Serde<Integer> SERDE_INT = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    public static final Serde<String> SERDE_STRING = Serde.of(
            i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)),
            (String serialized) -> new String(Base64.getDecoder().decode(serialized), StandardCharsets.UTF_8)
    );

    public static final Serde<PlayerId> SERDE_PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    public static final Serde<TurnKind> SERDE_TURN_KIND = Serde.oneOf(TurnKind.ALL);

    public static final Serde<Card> SERDE_CARD = Serde.oneOf(Card.ALL);

    public static final Serde<Route> SERDE_ROUTE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> SERDE_TICKET = Serde.oneOf(ChMap.tickets());

    public static final Serde<List<String>> SERDE_LIST_STRING = Serde.listOf(SERDE_STRING, V_SEPARATOR);

    public static final Serde<List<Card>> SERDE_LIST_CARD = Serde.listOf(SERDE_CARD, V_SEPARATOR);

    public static final Serde<List<Route>> SERDE_LIST_ROUTE = Serde.listOf(SERDE_ROUTE, V_SEPARATOR);

    public static final Serde<SortedBag<Card>> SERDE_SORTED_BAG_CARD = Serde.bagOf(SERDE_CARD, V_SEPARATOR);

    public static final Serde<SortedBag<Ticket>> SERDE_SORTED_BAG_TICKET = Serde.bagOf(SERDE_TICKET, V_SEPARATOR);

    public static final Serde<List<SortedBag<Card>>> SERDE_LIST_OF_SORTED_BAG_CARD = Serde.listOf(SERDE_SORTED_BAG_CARD, PV_SEPARATOR);

    /*public static final Serde<Trail> SERDE_TRAIL = Serde.of(
            (Trail trail) -> String.join(PV_SEPARATOR, List.of(SERDE_LIST_ROUTE.serialize(trail.getRoutes()),
                    SERDE_INT.serialize(trail.station1().id()), SERDE_STRING.serialize(trail.station1().name()),
                    SERDE_INT.serialize(trail.station2().id()), SERDE_STRING.serialize(trail.station2().name()))),
            (String serialized) ->{
                List<String> tabs = Arrays.asList(serialized.split(Pattern.quote(PV_SEPARATOR),-1));

                List<Route> trailRoutes = SERDE_LIST_ROUTE.deserialize(tabs.get(0));
                Station from = new Station(SERDE_INT.deserialize(tabs.get(1)), SERDE_STRING.deserialize(tabs.get(2)));
                Station to = new Station(SERDE_INT.deserialize(tabs.get(3)), SERDE_STRING.deserialize(tabs.get(4)));

                return new Trail(trailRoutes, from, to);
            }
    );*/

    /**
     * Le serde associée au PublicCardState
     */
    public static final Serde<PublicCardState> SERDE_PUBLIC_CARD_STATE = Serde.of(
            (PublicCardState cardState) -> String.join(PV_SEPARATOR, List.of(SERDE_LIST_CARD.serialize(cardState.faceUpCards()),
                    SERDE_INT.serialize(cardState.deckSize()), SERDE_INT.serialize(cardState.discardsSize()))),
            (String serialized) ->{
                List<String> tabs = Arrays.asList(serialized.split(Pattern.quote(PV_SEPARATOR),-1));

                List<Card> faceUpCardsNew = SERDE_LIST_CARD.deserialize(tabs.get(0));
                int deckSizeNew = SERDE_INT.deserialize(tabs.get(1));
                int discardSizeNew = SERDE_INT.deserialize(tabs.get(2));

                return new PublicCardState(faceUpCardsNew, deckSizeNew, discardSizeNew);
            }
    );

    /**
     * Le serde associée au PublicPlayerState
     */
    public static final Serde<PublicPlayerState> SERDE_PUBLIC_PLAYER_STATE = Serde.of(
            (PublicPlayerState state) -> String.join(PV_SEPARATOR, List.of(SERDE_INT.serialize(state.ticketCount()),
                    SERDE_INT.serialize(state.cardCount()), SERDE_LIST_ROUTE.serialize(state.routes()))),
            (String serialized) ->{
                List<String> tabs = Arrays.asList(serialized.split(Pattern.quote(PV_SEPARATOR),-1));

                int ticketCountNew = SERDE_INT.deserialize(tabs.get(0));
                int cardCountNew = SERDE_INT.deserialize(tabs.get(1));
                List<Route> routesNew = SERDE_LIST_ROUTE.deserialize(tabs.get(2));

                return new PublicPlayerState(ticketCountNew, cardCountNew, routesNew);
            }
    );

    /**
     * Le serde associée au PlayerState
     */
    public static final Serde<PlayerState> SERDE_PLAYER_STATE = Serde.of(
            (PlayerState playerState) -> String.join(PV_SEPARATOR, List.of(SERDE_SORTED_BAG_TICKET.serialize(playerState.tickets()),
                    SERDE_SORTED_BAG_CARD.serialize(playerState.cards()), SERDE_LIST_ROUTE.serialize(playerState.routes()))),
            (String serialized) ->{
                List<String> tabs = Arrays.asList(serialized.split(Pattern.quote(PV_SEPARATOR),-1));

                SortedBag<Ticket> ticketsNew = SERDE_SORTED_BAG_TICKET.deserialize(tabs.get(0));
                SortedBag<Card> cardsNew = SERDE_SORTED_BAG_CARD.deserialize(tabs.get(1));
                List<Route> routesNew = SERDE_LIST_ROUTE.deserialize(tabs.get(2));

                return new PlayerState(ticketsNew, cardsNew, routesNew);
            }
    );

    /**
     * Le serde associée au PublicGameState
     */
    public static final Serde<PublicGameState> SERDE_PUBLIC_GAME_STATE = Serde.of(
            (PublicGameState game) ->{
                String txt = String.join(P_SEPARATOR, List.of(SERDE_INT.serialize(game.ticketsCount()), SERDE_PUBLIC_CARD_STATE.serialize(game.cardState()),
                        SERDE_PLAYER_ID.serialize(game.currentPlayerId()), SERDE_PUBLIC_PLAYER_STATE.serialize(game.playerState(PlayerId.PLAYER_1)),
                        SERDE_PUBLIC_PLAYER_STATE.serialize(game.playerState(PlayerId.PLAYER_2))));
                txt +=  game.lastPlayer() == null ? P_SEPARATOR + "" : P_SEPARATOR + SERDE_PLAYER_ID.serialize(game.lastPlayer());
                return txt;
            },
            (String serialized) ->{
                List<String> tabs = Arrays.asList(serialized.split(Pattern.quote(P_SEPARATOR),-1));
                
                int ticketCountNew = SERDE_INT.deserialize(tabs.get(0));
                PublicCardState cardStateNew = SERDE_PUBLIC_CARD_STATE.deserialize(tabs.get(1));
                PlayerId playerIdNew = SERDE_PLAYER_ID.deserialize(tabs.get(2));
                PublicPlayerState playerPublicStateOneNew = SERDE_PUBLIC_PLAYER_STATE.deserialize(tabs.get(3));
                PublicPlayerState playerPublicStateTwoNew = SERDE_PUBLIC_PLAYER_STATE.deserialize(tabs.get(4));
                
                Map<PlayerId, PublicPlayerState> mapeStateNew = new EnumMap<>(PlayerId.class);
                mapeStateNew.put(PlayerId.PLAYER_1, playerPublicStateOneNew);
                mapeStateNew.put(PlayerId.PLAYER_2, playerPublicStateTwoNew);

                String lastPlayerTxt = tabs.get(5);
                PlayerId lastPlayerIdNew = lastPlayerTxt.equals("") ? null : SERDE_PLAYER_ID.deserialize(tabs.get(5));

                return new PublicGameState(ticketCountNew, cardStateNew, playerIdNew, mapeStateNew, lastPlayerIdNew);
            }
    );
}