/*
 *	Author:      Félix Rodriguez
 *	Date:        4 mar. 2021
 */
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;

public class TestMad {

    public static void main(String[] args) {
        Station st1 = new Station(0, "st1");
        Station st2 = new Station(1,"st2");
        Route tester3 = new Route("try", st1 , st2,2, Level.OVERGROUND, null);
        int size = tester3.possibleClaimCards().size();
        System.out.println(tester3.possibleClaimCards());
        System.out.println(size);
        
        List<SortedBag<Card>> t1 = new ArrayList<SortedBag<Card>>();
        for(Card a : Card.CARS) {
            t1.add(SortedBag.of(2, a));            
        }
        for(Card a : Card.CARS) {
           t1.add(SortedBag.of(1, a, 1, Card.LOCOMOTIVE));
        }
        t1.add(SortedBag.of(2, Card.LOCOMOTIVE));
        
        System.out.println(t1);
        
        List<SortedBag<Card>> t2 = new ArrayList<SortedBag<Card>>();
        for(Card a : Card.CARS) {
            t2.add(SortedBag.of(2, a));            
        }
        
        System.out.println(t2);
        
        SortedBag<Card> tester =SortedBag.of(2, Card.BLACK);
        SortedBag<Card> tester1 =SortedBag.of(2, Card.LOCOMOTIVE);

        List<SortedBag<Card>> yep = new ArrayList<>();
        yep.add(tester1);
        yep.add(tester);
        
        SortedBag<Card> tester2 =SortedBag.of(2, Card.BLACK, 3, Card.WHITE);

        for(SortedBag<Card> a : yep) {
            if(tester2.contains(a)) {
                System.out.println("works");
            }
        }
        System.out.println(tester2.toList());

        Route ab = new Route("bjr", new Station(2, "Lausanne"), new Station(3, "Geneve"), 3, Level.OVERGROUND, Color.BLUE);
        
        List<Card> test = List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.GREEN, Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.RED);
        List<Ticket> test1 = List.of(new Ticket(new Station(2, "Lausanne"), new Station(3, "Geneve"), 4), new Ticket(new Station(1, "Fribourg"), new Station(3, "Geneve"), 7), new Ticket(new Station(2, "Lausanne"), new Station(7, "Fribourg"), 5));
        SortedBag<Card> cards =  SortedBag.of(test);
        SortedBag<Ticket> tickets = SortedBag.of(test1);
        List<Route> routes = List.of(new Route("bjr", new Station(3, "Lausanne"), new Station(4, "Geneve"), 4, Level.OVERGROUND, Color.BLACK));
        PlayerState player = new PlayerState(tickets, cards, routes);
        
        System.out.print(player.canClaimRoute(ab));
    }

}
