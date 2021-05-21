package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
import ch.epfl.tchu.gui.ActionHandlers.*;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

class MapViewCreator {
    /**
     * Constructeur privé pour éviter le constructeur par défaut et rendre la classe non instanciable.
     */
    private MapViewCreator(){}

    @FunctionalInterface
    public interface CardChooser {
       void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler chooseHandler);
    }

    /**
     * @param jeu (ObservableGameState)
     * @param routeHandler (ObjectProperty<ClaimRouteHandler>)
     * @param select (CardChooser)
     * @return (Node) : le graphe de scène des routes de la map
     */
    public static Node createMapView(ObservableGameState jeu,
                                     ObjectProperty<ClaimRouteHandler> routeHandler,
                                     CardChooser select){

        ImageView map = new ImageView("map.png");

        Pane home = new Pane(map);
        home.getStylesheets().add("map.css");
        home.getStylesheets().add("colors.css");

        for(Route route : ChMap.routes()){
            home.getChildren().add(makeGroupOfRoute(route, jeu, routeHandler, select));
        }

        return  home;
    }

    /**
     * @param route route pour laquelle construire les groupes d'affichage
     * @return groupes imbriqués de visualisation
     */
    private static Group makeGroupOfRoute(Route route, ObservableGameState jeu,
                                          ObjectProperty<ClaimRouteHandler> routeHandler, CardChooser select){
        Group groupRoute = new Group();
        groupRoute.setId(route.id());
        groupRoute.getStyleClass().addAll("route", route.level().name());

        //Pour avoir la couleure de la route
        if(route.color() == null){
            groupRoute.getStyleClass().add("NEUTRAL");
        }else{
            groupRoute.getStyleClass().add(route.color().name());
        }

        //Auditeur de la route
        jeu.getLordRoute(route).addListener((p, o, n) ->{
            if(n != null){
                groupRoute.getStyleClass().add(n.name());


            }
        });

        //Liens pour la route
        groupRoute.disableProperty().bind(routeHandler.isNull().or(jeu.getBooleanRoute(route).not()));

        groupRoute.setOnMouseClicked(e ->{
            List<SortedBag<Card>> possibilities = jeu.possibleClaimCards(route);

            if(possibilities.size() == 1){

                SortedBag<Card> claimCards = possibilities.get(0);
                routeHandler.get().onClaimRoute(route, claimCards);

            }else if(possibilities.size() > 1){

                ActionHandlers.ChooseCardsHandler chooseCardsH =
                        chosenCards -> routeHandler.get().onClaimRoute(route, chosenCards);
                select.chooseCards(possibilities, chooseCardsH);
            }
        });

        for (int i = 0 ; i < route.length(); ++i){
            //initialiser le groupe de la partie de route
            Group subGroupRoute = new Group(makeVoie(), makeWagon());
            String lengthValue = String.valueOf(i + 1);
            String newId = route.id() + "_" + lengthValue;
            subGroupRoute.setId(newId);

            //mettre en lien le group de la partie avec le group principal de la route
            groupRoute.getChildren().add(subGroupRoute);
        }
        return groupRoute;
    }

    /**
     * @return une voie avec les styles complets
     */
    private static Rectangle makeVoie(){

        Rectangle voie = new Rectangle(36, 12);
        voie.getStyleClass().addAll("track", "filled");

        return voie;
    }


    /**
     * @return wagon avec cercles et styles complets
     */
    private static Group makeWagon(){

        Rectangle rectangle = new Rectangle(36, 12);
        rectangle.getStyleClass().add("filled");

        Circle circleOne = new Circle(12, 6, 3);
        Circle circleTwo = new Circle(24, 6, 3);

        Group wagon = new Group(rectangle, circleOne, circleTwo);
        wagon.getStyleClass().add("car");

        return wagon;
    }
}