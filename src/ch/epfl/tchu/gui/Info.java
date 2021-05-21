package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

public final class Info {

    private final String playerName;

    /**
     * @param playerName: nom du joueur
     */
    public Info(String playerName){
        this.playerName = playerName;
    }

    /**
     * @param card: une carte
     * @param count: un entier pour déterminer si c'est au singulier ou pluriel
     * @return le nom de la carte card
     */
    public static String cardName(Card card, int count){
        String cardName;
        switch(card){
            case BLACK:
                cardName = StringsFr.BLACK_CARD;
                break;
            case BLUE:
                cardName = StringsFr.BLUE_CARD;
                break;
            case GREEN:
                cardName = StringsFr.GREEN_CARD;
                break;
            case ORANGE:
                cardName = StringsFr.ORANGE_CARD;
                break;
            case RED:
                cardName = StringsFr.RED_CARD;
                break;
            case VIOLET:
                cardName = StringsFr.VIOLET_CARD;
                break;
            case WHITE:
                cardName = StringsFr.WHITE_CARD;
                break;
            case YELLOW:
                cardName = StringsFr.YELLOW_CARD;
                break;
            case LOCOMOTIVE:
                cardName = StringsFr.LOCOMOTIVE_CARD;
                break;
            default:
                cardName = "";
        }
        return String.format("%s%s", cardName, StringsFr.plural(count));
    }

    /**
     * @param playerNames: liste de joueurs
     * @param points: les points détenus par les deux joueurs (même nombre)
     * @return les deux joueurs ont fait une égalité avec le nombre de points
     */
    public static String draw(List<String> playerNames, int points){
        return String.format(StringsFr.DRAW, String.format("%s" + StringsFr.AND_SEPARATOR + "%s", playerNames.get(0), playerNames.get(1)), points);
    }

    /**
     * @return le joueur va commencer
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * @param count le nombre de tickets
     * @return le nombre de tickets que le joueur à gardés
     */
    public String keptTickets(int count){ return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count)); }

    /**
     * @return c'est au tour du joueur de jouer
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * @param count le nombre de tickets tirés
     * @return le nombre de tickets que le joueur à tirés
     */
    public String drewTickets(int count){ return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count)); }

    /**
     * @return le joueur à tiré une carte de la pioche
     */
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * @param card la carte que le joueur à tiré
     * @return le joueur à tiré une carte et précise le nom de la carte
     */
    public String drewVisibleCard(Card card){ return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1)); }

    /**
     * @param route dont le joueur s'est emparé
     * @param cards les cartes qu'il à utilisé
     * @return le joueur s'est emparé d'une route avec les cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeText(route), cardText(cards));
    }

    /**
     * @param route dont le joueur tente de s'emparer
     * @param initialCards les cartes avec lesquels il tente le coup
     * @return le joueur tente de s'emparer du tunnel avec les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeText(route), cardText(initialCards));
    }

    /**
     * @param drawnCards les cartes supplémentaires tirées
     * @param additionalCost la coût additionnel
     * @return un coût additionnel si les cartes tirées l'implique
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String addition = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardText(drawnCards));
        if(additionalCost > 0){
            addition += String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        }else{
            addition += StringsFr.NO_ADDITIONAL_COST;
        }
        return addition;
    }

    /**
     * @param route dont le joueur à cesser de s'emparer
     * @return le joueur ne s'empare pas de la route donnée
     */
    public String didNotClaimRoute(Route route){ return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route)); }

    /**
     * @param carCount le nombre de wagons restant au joueur
     * @return le joueur à carCount wagons et le dernier tour commence
     */
    public String lastTurnBegins(int carCount){ return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount)); }

    /**
     * @param longestTrail le plus long chemin
     * @return le joueur reçoit un bonus car il possède le plus long chemin
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        String longest = String.format("%s%s%s", longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());
        return String.format(StringsFr.GETS_BONUS, playerName, longest);
    }

    /**
     * @param points le nombre de points du gagnant
     * @param loserPoints le nombre de points du perdant
     * @return les scores des joueurs, leur nom et le gagnant
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * méthode utilisée pour simplifier l'écriture d'autres méthodes
     * @param route une route
     * @return la représentation textuelle d'une route
     */
    private static String routeText(Route route){
        return String.format("%s%s%s", route.station1().name(), StringsFr.EN_DASH_SEPARATOR, route.station2().name());
    }

    /**
     * méthode utilisée pour simplifier l'écriture d'autres méthodes
     * @param cards une liste de cartes
     * @return la réprésentation textuelle attendue d'une liste de cartes
     */
    static String cardText(SortedBag<Card> cards){
        List<String> texts = new ArrayList<>();
        for (Card card : cards.toSet()) {
            String temp;
            int numberCards = cards.countOf(card);
            temp = numberCards + " " + cardName(card, numberCards);
            texts.add(temp);
        }
        String stringCards = "";
        if(texts.size() > 1){
            List<String> tempOne = texts.subList(0, texts.size() - 1);
            stringCards += String.join(", ", tempOne) + StringsFr.AND_SEPARATOR + texts.get(texts.size() - 1);
        }else{
            stringCards = texts.get(0);
        }
        return stringCards;
    }
}