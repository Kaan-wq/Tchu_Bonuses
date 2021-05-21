package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 * 
 * Classe qui modélise les trajets du jeu.
 */

public final class Trip {

    private final Station depart;
    private final Station end;
    private final int value;

    /**
     * @param from : Station utilisée comme point de départ pour le trajet, ne peut pas être null.
     * @param to : Station utilisée comme point d'arrivée pour le trajet,  ne peut pas être null.
     * @param pts : quantité de points donné pour la réussite du trajet, strictement positif.
     * @throws NullPointerException : en cas de Station null, manque d'un point de départ ou d'arrivée.
     * @throws IllegalArgumentException : en cas de quantité de points négative ou nulle.
     */
    public Trip(Station from , Station to, int pts) throws NullPointerException, IllegalArgumentException{
        Preconditions.checkArgument(pts > 0);

        value = pts;

        if(from == null || to == null ) {
            throw new NullPointerException();
        }else{
            depart = from;
            end = to;
        }
    }

    /**
     * @param from :liste de stations de départ des trajets, ne peut pas être nulle.
     * @param to : liste de stations d'arrivée des trajets, ne peut pas être nulle.
     * @param points : points attribuées à chaque voyage de la liste, strictement négatif.
     * @return : liste avec tous les trajets disponibles entre une station de départ et une station d'arrivée
     * @throws IllegalArgumentException : en cas de quantité de points négative ou nulle, et/ou liste de stations d'arrivée ou départ vide.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points)throws IllegalArgumentException{
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(!from.isEmpty() && !to.isEmpty() && points > 0);

        ArrayList <Trip> all = new ArrayList<>();

        from.forEach(e -> to.forEach(f-> all.add(new Trip(e,f,points))));
        return all;
    }

    /**
     * @return Station de départ du trajet.
     */
    public Station from() {
        return depart;
    }
    
    /**
     * @return Station d'arrivée du trajet.
     */
    public Station to() {
        return end;
    }
    
    /**
     * @return Quantité de points du trajet.
     */
    public int points() {
        return value;
    }

    /**
     * @param connectivity : connectivité du joueur qui détermine si celui-ci à réussi à connecter les stations du trajet.
     * @return : quantité de points attribuée au joueur si il a connecté les stations ou retirée dans le cas contraire.
     */
    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(depart, end)) {
            return value;
        }else {
            return -value;
        }
    }
}