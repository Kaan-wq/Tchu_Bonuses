package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public interface ActionHandlers {


    interface DrawTicketsHandler{

        /**
         * Méthode appelée lorsaue le joueur décide de tirer des billets
         */
        void onDrawTickets();
    }

    interface DrawCardHandler{

        /**
         * Méthode appelée lorsque le joueur désire tirer une carte de l'emplacement donné,
         * @param drawSlot (Int) : emplacement d'où l'on désir tirer la carte
         */
        void onDrawCard(int drawSlot);
    }

    interface ClaimRouteHandler{

        /**
         * Méthode appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes initiales données
         * @param route (Route) : route dont le joueur désire s'emparer
         * @param claimCards (SortedBag<Card>) : les cartes initiales pour s'emparer de la route
         */
        void onClaimRoute(Route route, SortedBag<Card> claimCards);
    }

    interface ChooseTicketsHandler{

        /**
         * Méthode appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets
         * @param chooseTickets (SortedBag<Ticket>) : les billets que le joueur souhaite garder
         */
        void onChooseTickets(SortedBag<Ticket> chooseTickets);
    }

    interface ChooseCardsHandler{

        /**
         * Méthode  appelée lorsque le joueur a choisi d'utiliser les cartes données
         * comme cartes initiales ou additionnelles lors de la prise de possession d'une route
         * @param chooseCards (SortedBag<Card>) : les cartes initiales ou additionnelles pour s'emparer d'un tunnel
         */
        void onChooseCards(SortedBag<Card> chooseCards);
    }
}