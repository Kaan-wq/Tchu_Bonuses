package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class Trail {

    private final List<Route> routes;
    private final Station from;
    private final Station to;
    private final int length;

    /**
     * @param routes : liste de routes qui constituent le chemin
     * @param from : station de départ du chemin
     * @param to : station d'arrivée du chemin
     */
    private Trail(List<Route> routes, Station from, Station to){
        this.routes = routes;

        if(routes == null || routes.size() == 0){
            this.length = 0;
            this.from = null;
            this.to = null;
        }else{
            int length = 0;
            this.from = from;
            this.to = to;
            for(Route route: routes){
                length += route.length();
            }
            this.length = length;
        }
    }

    /**
     * @param routes : liste de routes dont on cherche le plus grand chemin possible
     * @return : le plus long chemin possible
     */
    public static Trail longest(List<Route> routes){
        List<Trail> singleTrails = new ArrayList<>();
        Trail longest = new Trail(null, null, null);

        if(routes == null || routes.size() == 0){
            return longest;
            //pour le moment ses attributs sont nulls donc ca va retourner "null Trail"
        }

        for(Route route: routes){
            singleTrails.add(new Trail(List.of(route), route.station1(), route.station2()));
            singleTrails.add(new Trail(List.of(route), route.station2(), route.station1()));
        }

        while(!singleTrails.isEmpty()){
            List<Trail> trails = new ArrayList<>();
            for(Trail trail : singleTrails){

                List<Route> routesCopie = new ArrayList<>(routes);
                routesCopie.removeAll(trail.routes);

                for(Route route : routesCopie){
                    if(trail.to.equals(route.station1())){

                        List<Route> ephemere = new ArrayList<>(trail.routes);
                        ephemere.add(route);

                        Trail temp = new Trail(ephemere, trail.station1(), route.station2());

                        trails.add(temp);

                        if(temp.length() >= longest.length()){
                            longest = temp;
                        }
                    }else if(trail.to.equals(route.station2())){

                        List<Route> ephemere = new ArrayList<>(trail.routes);
                        ephemere.add(route);

                        Trail temp = new Trail(ephemere, trail.station1(), route.station1());

                        trails.add(temp);

                        if(temp.length() >= longest.length()){
                            longest = temp;
                        }
                    }
                }
            }
            singleTrails = trails;
        }
        if(longest.length == 0){
            int lon = 0;
            Route routeDisconnected = null;
            for(Route r: routes){
                if(r.length() >= lon){
                    lon = r.length();
                    routeDisconnected = r;
                }
            }
            longest = new Trail(List.of(routeDisconnected), routeDisconnected.station1(), routeDisconnected.station2());
        }
        return longest;
    }

    /**
     * @return la longueur d'un chemin
     */
    public int length(){
        return length;
    }

    /**
     * @return la station de départ d'un chemin
     */
    public Station station1(){
        if(length() == 0){
            return null;
        }else{
            return from;
        }
    }

    /**
     * @return la station d'arrivée d'un chemin
     */
    public Station station2() {
        if (length() == 0) {
            return null;
        } else {
            return to;
        }
    }

    public List<Route> getRoutes() {
        return List.copyOf(routes);
    }

    /**
     * @return la représentation textuelle d'un chemin, les stations de
     * chaque routes par lesquels passe le chemin
     */
    @Override
    public String toString(){
        if(routes == null){
            return "Null Trail";
        }
        List<String> gares = new ArrayList<>();
        String currentStation = from.name();
        gares.add(currentStation);

        for(Route road : routes){
            if(currentStation.equals(road.station1().name())){
                gares.add(road.stationOpposite(station1()).name());
                currentStation = road.station2().name();
            }else if(currentStation.equals(road.station2().name())){
                gares.add(road.stationOpposite(station2()).name());
                currentStation = road.station1().name();
            }
        }
        String text = String.join(" - ", gares);

        return String.format("%s (%s)", text, this.length());
    }
}