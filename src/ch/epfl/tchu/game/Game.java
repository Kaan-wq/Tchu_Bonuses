package ch.epfl.tchu.game;

import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import javax.sound.sampled.Clip;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class Game {

    private Game(){}

    /**
     * @param players (Map<PlayerId, Player>) : la map associant les PlayerId au Player
     * @param playerNames (Map<PlayerId, String>) : la map associant les PlayerId aux noms des joueurs
     * @param tickets (SortedBag<Ticket>)
     * @param rng (Random)
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId,
            String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        
        //map avec les informations des joueurs
        Map<PlayerId, Info> info = new EnumMap<>(PlayerId.class);
        
        //etat du jeu initial, avec pioche de base, et choix du premier joueur au hasard.
        GameState jeu = GameState.initial(tickets, rng);

        //initialisation des players, et leur info.
        players.forEach((id, player) -> {
            player.initPlayers(id, playerNames);
            info.put(id, new Info(playerNames.get(id)));
        });

        //info du premier joueur.
        sendInfo(players, info.get(jeu.currentPlayerId()).willPlayFirst());

        //distribution des billets intiales.
        for (Player player : players.values()) {
            player.setInitialTicketChoice(jeu.topTickets(Constants.INITIAL_TICKETS_COUNT));
            jeu = jeu.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        updateGraphics(players, jeu);

        //choix initial de billets, rajoutés après à sa main, avec update de l'etat du jeu.
        for (PlayerId player : players.keySet()) {
            SortedBag<Ticket> initialTickets = players.get(player).chooseInitialTickets();
            jeu = jeu.withInitiallyChosenTickets(player, initialTickets);
            sendInfo(players, info.get(player).keptTickets(initialTickets.size()));
            players.get(player).playSong("sounds/TicketDraw.wav", 0);
        }

        //boucle d'itération pour les tours des joueurs

        boolean endGame = true;

        players.forEach((id, player) -> player.playSong("sounds/Musique_fond.wav", Clip.LOOP_CONTINUOUSLY));

        while(endGame){
            updateGraphics(players, jeu);
            Player.TurnKind action = players.get(jeu.currentPlayerId()).nextTurn();
            Player currentPlayer = players.get(jeu.currentPlayerId());

            //choix de l'action

            switch (action) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> topTickets = jeu.topTickets(Constants.IN_GAME_TICKETS_COUNT);

                    //info sur la pioche de tickets
                    sendInfo(players, info.get(jeu.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> keptTickets = players.get(jeu.currentPlayerId()).chooseTickets(topTickets);

                    //info sur les tickets qu'il garde
                    sendInfo(players, info.get(jeu.currentPlayerId()).keptTickets(keptTickets.size()));
                    currentPlayer.playSong("sounds/TicketDraw.wav", 0);

                    jeu = jeu.withChosenAdditionalTickets(topTickets, keptTickets);

                    break;

                case DRAW_CARDS:
                    
                    //premier choix de carte
                    Card carteSlot;
                    int faceUpSlot = players.get(jeu.currentPlayerId()).drawSlot();

                    jeu = jeu.withCardsDeckRecreatedIfNeeded(rng);

                    if (faceUpSlot == Constants.DECK_SLOT) {
                        jeu = jeu.withBlindlyDrawnCard();
                        //info pioche carte du tas
                        sendInfo(players, info.get(jeu.currentPlayerId()).drewBlindCard());
                        currentPlayer.playSong("sounds/DrawCards.wav", 0);

                    }else if(Constants.FACE_UP_CARD_SLOTS.contains(faceUpSlot)){
                        //carte visible si possible
                        carteSlot = jeu.cardState().faceUpCard(faceUpSlot);
                        jeu = jeu.withDrawnFaceUpCard(faceUpSlot);
                        //info carte retourné
                        sendInfo(players, info.get(jeu.currentPlayerId()).drewVisibleCard(carteSlot));
                        currentPlayer.playSong("sounds/DrawCards.wav", 0);
                    }

                    //mise à jour de la pioche.
                    jeu = jeu.withCardsDeckRecreatedIfNeeded(rng);
                    updateGraphics(players, jeu);

                    //deuxieme choix de carte.
                    int faceUpSlotTwo = players.get(jeu.currentPlayerId()).drawSlot();


                    if (faceUpSlotTwo == Constants.DECK_SLOT) {
                        jeu = jeu.withBlindlyDrawnCard();
                        //info pioche carte du tas
                        sendInfo(players, info.get(jeu.currentPlayerId()).drewBlindCard());

                    } else {
                        //carte visible si possible
                        carteSlot = jeu.cardState().faceUpCard(faceUpSlotTwo);
                        jeu = jeu.withDrawnFaceUpCard(faceUpSlotTwo);
                        //info carte retourné
                        sendInfo(players, info.get(jeu.currentPlayerId()).drewVisibleCard(carteSlot));
                    }
                    currentPlayer.playSong("sounds/DrawCards.wav", 0);

                    //mise à jour de la pioche.
                    jeu = jeu.withCardsDeckRecreatedIfNeeded(rng);

                    break;
                case CLAIM_ROUTE:
                    Route claimed = players.get(jeu.currentPlayerId()).claimedRoute();
                    SortedBag<Card> cartesUtiles = players.get(jeu.currentPlayerId()).initialClaimCards();

                    if (claimed.level().equals(Level.UNDERGROUND)) {

                        //info que le joueur essaye de construire un tunnel.
                        sendInfo(players, info.get(jeu.currentPlayerId()).attemptsTunnelClaim(claimed, cartesUtiles));

                        SortedBag.Builder<Card> topCards = new SortedBag.Builder<>();

                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                            jeu = jeu.withCardsDeckRecreatedIfNeeded(rng);
                            topCards.add(jeu.topCard());
                            jeu = jeu.withoutTopCard();
                        }
                        
                        SortedBag<Card> cartesTopDeck = topCards.build();

                        jeu = jeu.withMoreDiscardedCards(cartesTopDeck);
                        int count = claimed.additionalClaimCardsCount(cartesUtiles, cartesTopDeck);
                        
                        //info pioche cartes extra tunnel
                        sendInfo(players, info.get(jeu.currentPlayerId()).drewAdditionalCards(cartesTopDeck, count));
                        

                        if (count != 0) {
                            //Dans le cas où besoin de deposer des cartes extra, options de cartes.
                            List<SortedBag<Card>> options = jeu.currentPlayerState().possibleAdditionalCards(count, cartesUtiles);
                            if (!options.isEmpty()) {

                                //choix des cartes parmis les possibilités, seulement si non vide.
                                SortedBag<Card> chosen = players.get(jeu.currentPlayerId()).chooseAdditionalCards(options);

                                if (chosen.isEmpty()) {
                                    sendInfo(players, info.get(jeu.currentPlayerId()).didNotClaimRoute(claimed));
                                } else {
                                    SortedBag<Card> unionChoice = cartesUtiles.union(chosen);

                                    jeu = jeu.withClaimedRoute(claimed, unionChoice);
                                    //info construction avec additional
                                    sendInfo(players, info.get(jeu.currentPlayerId()).claimedRoute(claimed, unionChoice));
                                    currentPlayer.playSong("sounds/TrainWhoosh.wav", 0);
                                }
                            }else{
                                //info sur manque de cartes pour "payer" cartes additionnelles.
                                sendInfo(players, info.get(jeu.currentPlayerId()).didNotClaimRoute(claimed));
                            }

                        } else {
                            jeu = jeu.withClaimedRoute(claimed, cartesUtiles); //info construction tunnel?
                            //info construction tunnel normale.
                            sendInfo(players, info.get(jeu.currentPlayerId()).claimedRoute(claimed, cartesUtiles));
                            currentPlayer.playSong("sounds/TrainWhoosh.wav", 0);
                        }
                    } else {
                        jeu = jeu.withClaimedRoute(claimed, cartesUtiles);
                        
                        //info prend un route normale
                        sendInfo(players, info.get(jeu.currentPlayerId()).claimedRoute(claimed, cartesUtiles));
                        currentPlayer.playSong("sounds/HornOne.wav", 0);
                    }
                    break;
            }

            if(jeu.lastPlayer() == jeu.currentPlayerId()){
                endGame = false;
            }

            if(jeu.lastTurnBegins()){
                sendInfo(players, info.get(jeu.currentPlayerId()).lastTurnBegins(jeu.currentPlayerState().carCount()));
            }

            //prochain tour
            jeu = jeu.forNextTurn();

            if(endGame){
                sendInfo(players, info.get(jeu.currentPlayerId()).canPlay());
            }else{
                //informations etat du jeu fin de partie.
                updateGraphics(players, jeu);
            }
        }

        //Calcul des points de joueurs, avec variables pour aléger le code.
        
        //points de base de chaque joueur.
        int playerPointsOne = jeu.currentPlayerState().finalPoints();
        int playerPointsTwo = jeu.playerState(jeu.currentPlayerId().next()).finalPoints();
        
        //taille du chemin le plus long
        int lengthMaxOne = Trail.longest(jeu.currentPlayerState().routes()).length();
        int lengthMaxTwo = Trail.longest(jeu.playerState(jeu.currentPlayerId().next()).routes()).length();

        if(lengthMaxOne == lengthMaxTwo){
            playerPointsOne += Constants.LONGEST_TRAIL_BONUS_POINTS;
            playerPointsTwo += Constants.LONGEST_TRAIL_BONUS_POINTS;

            sendInfo(players, info.get(jeu.currentPlayerId()).getsLongestTrailBonus(Trail.longest(jeu.currentPlayerState().routes())));
            sendInfo(players, info.get(jeu.currentPlayerId().next()).getsLongestTrailBonus(Trail.longest(jeu.playerState(jeu.currentPlayerId().next()).routes())));

        }else if(lengthMaxOne > lengthMaxTwo){
            playerPointsOne += Constants.LONGEST_TRAIL_BONUS_POINTS;

            sendInfo(players, info.get(jeu.currentPlayerId()).getsLongestTrailBonus(Trail.longest(jeu.currentPlayerState().routes())));

        }else{
            playerPointsTwo += Constants.LONGEST_TRAIL_BONUS_POINTS;

            sendInfo(players, info.get(jeu.currentPlayerId().next()).getsLongestTrailBonus(Trail.longest(jeu.playerState(jeu.currentPlayerId().next()).routes())));
        }

        updateGraphics(players, jeu);

        //Calcul du gagant de la partie et message aux joueurs

        if(playerPointsOne == playerPointsTwo){
            List<String> names = List.of(playerNames.get(jeu.currentPlayerId()), playerNames.get(jeu.currentPlayerId().next()));
            sendInfo(players, Info.draw(names, playerPointsOne));

        }else if (playerPointsOne > playerPointsTwo){
            sendInfo(players, info.get(jeu.currentPlayerId()).won(playerPointsOne, playerPointsTwo));

        }else{
            sendInfo(players, info.get(jeu.currentPlayerId().next()).won(playerPointsTwo, playerPointsOne));
        }
        players.forEach((id, player)-> player.playSong("sounds/Applauses.wav", 0));
    }
    
    /**
     * @param idplayers : groupe de joueurs au quel communiquer le message.
     * @param txt : message à communiquer.
     */
    private static void sendInfo(Map < PlayerId, Player > idplayers, String txt){
        for (Player player : idplayers.values()) {
            player.receiveInfo(txt);
        }
    }
    
    /**
     * @param idplayers :  groupe de joueurs au quel communiquer un état de la partie.
     * @param etat : état du jeu actuel à communiquer.
     */
    private static void updateGraphics(Map < PlayerId, Player > idplayers, GameState etat){
        for (PlayerId player : idplayers.keySet()) {
            idplayers.get(player).updateState(etat, etat.playerState(player));
        }
    }
}