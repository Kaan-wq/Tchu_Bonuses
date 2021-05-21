/*
 *	Author:      Félix Rodriguez
 *	Date:        3 mar. 2021
 */
package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class testRoute {
    private ArrayList<Integer> numbers = new ArrayList<Integer>();
    private Station st1 = new Station(0, "st1");
    private Station st2 = new Station(1,"st2");
    private Route tester = new Route("try", st1 , st2,2, Level.UNDERGROUND, Color.BLACK);
    
    @Test
    void testClaimPoints() {
        numbers.add(1);
        numbers.add(2);
        numbers.add(4);
        numbers.add(7);
        numbers.add(10);
        numbers.add(15);
        for(int i=1; i<7; i++) {
            Route ash = new Route("try", new Station(0, "st1"), new Station(1,"st2"),i, Level.OVERGROUND, Color.BLACK);
            assertEquals((int)numbers.get(i-1), ash.claimPoints());
        }
        
    }

    @Test
    void testOpposite(){
        assertEquals(st1, tester.stationOpposite(st2));
    }
    
    @Test
    void testAdditionalCards(){
        
        SortedBag.Builder<Card> a = new SortedBag.Builder<>();
        a.add(Card.LOCOMOTIVE);
        a.add(Card.BLACK);
        
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.LOCOMOTIVE);
        b.add(Card.BLACK);
        
        SortedBag.Builder<Card> c = new SortedBag.Builder<>();
        c.add(Card.LOCOMOTIVE);
        c.add(Card.BLUE);
        c.add(Card.LOCOMOTIVE);
        
        SortedBag<Card> claimCards = a.build();
        SortedBag<Card> drawnCardsError = b.build();
        SortedBag<Card> drawnCards = c.build();
        
        assertThrows(IllegalArgumentException.class, ()->{
            tester.additionalClaimCardsCount(claimCards, drawnCardsError);
        });
        
        assertEquals(2, tester.additionalClaimCardsCount(claimCards, drawnCards));
        
    }
    
    @Test
    void testPossibleClaimCards() {
        for(Color b : Color.ALL) {
     
            List<SortedBag<Card>> t1 = new ArrayList<SortedBag<Card>>();
            List<SortedBag<Card>> t2 = new ArrayList<SortedBag<Card>>();
            
            SortedBag.Builder<Card> combos = new SortedBag.Builder<>();
            SortedBag.Builder<Card> combos1 = new SortedBag.Builder<>();
            SortedBag.Builder<Card> combos2 = new SortedBag.Builder<>();

            combos.add(2, Card.of(b));
            SortedBag<Card> deckX = combos.build();

            t1.add(deckX);
            t2.add(deckX);
            
            combos1.add(Card.LOCOMOTIVE);
            combos1.add(Card.of(b));
            t2.add(combos1.build());
            
            combos2.add(Card.LOCOMOTIVE);
            combos2.add(Card.LOCOMOTIVE);
            t2.add(combos2.build());
            
            Route tester4 = new Route("try", st1 , st2,2, Level.UNDERGROUND, b);
            Route tester3 = new Route("try", st1 , st2,2, Level.OVERGROUND, b);
            
            for(int i=0; i< t1.size(); i++) {
                assertEquals(t1.get(i), tester3.possibleClaimCards().get(i));
            }
            
            for(int i=0; i<t2.size(); i++) {
                assertEquals(t2.get(i), tester4.possibleClaimCards().get(i));
            }
            
            

        }
         
         Route tester2 = new Route("try", st1 , st2,2, Level.UNDERGROUND, null);

         Route tester1 = new Route("try", st1 , st2,2, Level.OVERGROUND, null);
         
         List<SortedBag<Card>> t1 = new ArrayList<SortedBag<Card>>();
         for(Card a : Card.CARS) {
             t1.add(SortedBag.of(2, a));            
         }
         for(Card a : Card.CARS) {
            t1.add(SortedBag.of(1, a, 1, Card.LOCOMOTIVE));
         }
         t1.add(SortedBag.of(2, Card.LOCOMOTIVE));
         
         for(int i=0; i<t1.size();i++) {
             assertEquals(t1.get(i), tester2.possibleClaimCards().get(i));
         }
         
         List<SortedBag<Card>> t2 = new ArrayList<SortedBag<Card>>();
         for(Card a : Card.CARS) {
             t2.add(SortedBag.of(2, a));            
         }
         
         for(int i=0; i<t2.size();i++) {
             assertEquals(t2.get(i), tester1.possibleClaimCards().get(i));
         }
         

         


    }
    



    //Test du constructeur

    @Test
    void constructorFailsIfStation1EqualsStation2(){
        assertThrows(IllegalArgumentException.class, () -> {
            Route testConstructor = new Route("test", ChMap.stations().get(1), ChMap.stations().get(1), 4, Route.Level.UNDERGROUND, Color.RED);
        });
    }

    @Test
    void constructorFailsIfLengthIsNotRight(){
        assertThrows(IllegalArgumentException.class, () -> {
            Route testConstructor1 = new Route("test", ChMap.stations().get(1), ChMap.stations().get(2), 7, Route.Level.UNDERGROUND, Color.RED);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Route testConstructor2 = new Route("test", ChMap.stations().get(1), ChMap.stations().get(2), 0, Route.Level.UNDERGROUND, Color.RED);
        });
    }

    @Test
    void constructorFailsIfStationsAreNull(){
        assertThrows(NullPointerException.class, () -> {
            Route testConstructor1 = new Route("test", null, ChMap.stations().get(2), 4, Route.Level.UNDERGROUND, Color.RED);
        });

        assertThrows(NullPointerException.class, () -> {
            Route testConstructor2 = new Route("test", ChMap.stations().get(1), null, 4, Route.Level.UNDERGROUND, Color.RED);
        });

        assertThrows(NullPointerException.class, () -> {
            Route testConstructor1 = new Route("test", null, ChMap.stations().get(2), 4, Route.Level.UNDERGROUND, Color.RED);
        });

        assertThrows(NullPointerException.class, () -> {
            Route testConstructor2 = new Route("test", null, null, 4, Route.Level.UNDERGROUND, Color.RED);
        });
    }

    @Test
    void constructorFailsIfLevelIsUndefined(){
        assertThrows(NullPointerException.class, () -> {
            Route testConstructor = new Route("test", ChMap.stations().get(1), ChMap.stations().get(2), 4, null, Color.RED);
        });
    }

//Pas besoin de tester les getters je pense...

    @Test
    void methodStationWorkAsExpected(){
        Route test = new Route("BAD_OLT_1", ChMap.stations().get(0), ChMap.stations().get(20), 2, Route.Level.OVERGROUND, Color.VIOLET);
        assertEquals(new ArrayList<Station>(List.of(ChMap.stations().get(0),ChMap.stations().get(20))), test.stations());
    }

    @Test
    void methodStationOppositeWorkAsExpected() {
        Route test = new Route("BAD_OLT_1", ChMap.stations().get(0), ChMap.stations().get(20), 2, Route.Level.OVERGROUND, Color.VIOLET);
        assertEquals(ChMap.stations().get(20), test.stationOpposite(ChMap.stations().get(0)));
    }


    @Test
    void StationOppositeFailsIfArgumentIsNotRight(){
        Route test = new Route("BAD_OLT_1", ChMap.stations().get(0), ChMap.stations().get(20), 2, Route.Level.OVERGROUND, Color.VIOLET);
        assertThrows(IllegalArgumentException.class, () -> {
            test.stationOpposite(ChMap.stations().get(1));
        });
    }



    @Test
    void AdditionalClaimCardsCountFailsIfArgumentIsNotRight() {
        Route test = new Route("BAD_OLT_1", ChMap.stations().get(0), ChMap.stations().get(20), 2, Route.Level.OVERGROUND, Color.VIOLET);
        assertThrows(IllegalArgumentException.class, () -> {
            test.additionalClaimCardsCount(SortedBag.of(1, Card.of(Color.RED)), SortedBag.of(3, Card.of(Color.RED)));
        });

        Route test2 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            test2.additionalClaimCardsCount(SortedBag.of(1, Card.of(Color.RED)), SortedBag.of(2, Card.of(Color.RED)));
        });

        Route test3 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        assertThrows(IllegalArgumentException.class, () -> {
            test3.additionalClaimCardsCount(SortedBag.of(1, Card.of(Color.RED)), SortedBag.of(32, Card.of(Color.RED)));
        });
    }


    //test pour une route de 1 de longueur
    @Test
    void AdditionalClaimCardWorkAsExpected1() {
        Route test = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        int n = test.additionalClaimCardsCount(SortedBag.of(1, Card.of(Color.RED)), SortedBag.of(3, Card.of(Color.RED)));
        assertEquals(3, n);
    }
    @Test
    void AdditionalClaimCardWorkAsExpected2(){
        Route test2 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        int m = test2.additionalClaimCardsCount(SortedBag.of(1, Card.of(Color.GREEN)), SortedBag.of(3, Card.of(Color.RED)));
        assertEquals(0, m);
    }
    @Test
    void AdditionalClaimCardWorkAsExpected3(){
        Route test3 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        int o = test3.additionalClaimCardsCount(SortedBag.of(1, Card.LOCOMOTIVE), SortedBag.of(2, Card.of(Color.RED), 1, Card.LOCOMOTIVE));
        assertEquals(1, o);
    }
    @Test
    void AdditionalClaimCardWorkAsExpected4(){
        Route test4 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        int p = test4.additionalClaimCardsCount(SortedBag.of(1, Card.GREEN), SortedBag.of(2, Card.of(Color.RED), 1, Card.LOCOMOTIVE));
        assertEquals(1, p);
    }


    @Test
    void AdditionalClaimCardWorkAsExpected5(){
        Route test5 = new Route("AT2_VAD_1", ChMap.stations().get(40), ChMap.stations().get(28), 1, Route.Level.UNDERGROUND, Color.RED);
        SortedBag.Builder<Card> michel = new SortedBag.Builder<Card>();
        int n = test5.additionalClaimCardsCount(SortedBag.of(1, Card.ORANGE),
                 michel.add(Card.of(Color.GREEN)).add(Card.LOCOMOTIVE ).add(Card.ORANGE).build());
        assertEquals(2, n);

    }


    //test pour des routes avec plus de 1 de longueur

    @Test
    void AdditionalClaimCardWorkAsExpected6() {
        Route test = new Route("AT1_STG_1", ChMap.stations().get(39), ChMap.stations().get(27), 4, Route.Level.UNDERGROUND, null);
        int n = test.additionalClaimCardsCount(SortedBag.of(4, Card.of(Color.RED)), SortedBag.of(3, Card.of(Color.RED)));
        assertEquals(3, n);
    }


    @Test
    void methodClaimPointsWorkAsExpected() {
        Route test = new Route("BAD_OLT_1", ChMap.stations().get(0), ChMap.stations().get(20), 2, Route.Level.OVERGROUND, Color.VIOLET);
        assertEquals(2, test.claimPoints());
    }




}
