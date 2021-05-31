package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.util.Duration;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

class MapViewCreator {

    private static final int ROUTE_WIDTH = 36;
    private static final int ROUTE_HEIGHT = 12;
    private static final int CIRCLE_ROUTE_RADIUS = 3;

    /**
     * Constructeur privé pour éviter le constructeur par défaut et rendre la classe non instanciable.
     */
    private MapViewCreator(){}

    @FunctionalInterface
    public interface CardChooser {
        /**
         * @param options (List<SortedBag<Card>>) : combinaisons de cartes parmi lesquelles il faut faire un choix
         * @param chooseHandler (ChooseCardsHandler) : handler asociée au choix des cartes
         */
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
        home.getStylesheets().addAll("map.css", "colors.css");

        for(Route route : ChMap.routes()){
            home.getChildren().add(makeGroupOfRoute(route, jeu, routeHandler, select));
        }

        for(Station station : ChMap.stations().subList(0, 34)){
            home.getChildren().add(makeGroupTickets(station, jeu.getClickedStations(station)));
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
            Group subGroupRoute = new Group(makeVoie(), makeWagon(),
                    bigRec(route, jeu.getLonguestTrailP1Property()),
                    bigRec(route, jeu.getLonguestTrailP2Property()));

            String lengthValue = String.valueOf(i + 1);
            String newId = route.id() + "_" + lengthValue;
            subGroupRoute.setId(newId);

            //mettre en lien le group de la partie avec le group principal de la route
            groupRoute.getChildren().add(subGroupRoute);
        }
        return groupRoute;
    }

    /**
     * @return (Rectangle) : une voie avec les styles complets
     */
    private static Rectangle makeVoie(){

        Rectangle voie = new Rectangle(ROUTE_WIDTH, ROUTE_HEIGHT);
        voie.getStyleClass().addAll("track", "filled");

        return voie;
    }

    /**
     * @return (Group) : wagon avec cercles et styles complets
     */
    private static Group makeWagon(){

        Rectangle rectangle = new Rectangle(ROUTE_WIDTH, ROUTE_HEIGHT);
        rectangle.getStyleClass().add("filled");

        Circle circleOne = new Circle(ROUTE_WIDTH /3, ROUTE_HEIGHT /2, CIRCLE_ROUTE_RADIUS);
        Circle circleTwo = new Circle(ROUTE_WIDTH * 2/3, ROUTE_HEIGHT /2, CIRCLE_ROUTE_RADIUS);

        Group wagon = new Group(rectangle, circleOne, circleTwo);
        wagon.getStyleClass().add("car");

        return wagon;
    }

    private static Circle makeGroupTickets(Station station, ReadOnlyBooleanProperty clicked){
        Circle point = new Circle(6, Paint.valueOf("RED"));
        point.setId(String.valueOf(station.id()));
        point.getStyleClass().addAll("station", "filled");
        point.setVisible(false);

        //Animation
        ScaleTransition transition = new ScaleTransition(Duration.seconds(3), point);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.setToX(2.2);
        transition.setToY(2.2);

        clicked.addListener((p, o, n) ->{
            point.setVisible(p.getValue());
            transition.play();

            Timer timer = new Timer();
            timer.schedule(new TicketsAnim(point), 12000);
        });

        return point;
    }

    private static Rectangle bigRec(Route route, ReadOnlyObjectProperty<List<Route>> trailProp){
        Rectangle border = new Rectangle(36, 12);
        border.setVisible(false);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(4);

        trailProp.addListener((p,o,n) ->{
            if(n.contains(route)){
                border.setVisible(true);
            }
        });
        return border;
    }

    private static final class TicketsAnim extends TimerTask {
        private final Circle node;

        private TicketsAnim(Circle node){ this.node = node; }

        @Override
        public void run() { node.setVisible(false); }
    }
}