package ch.epfl.tchu.game;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 * 
 * Interface sur la connectivité d'un joueur
 */

public interface StationConnectivity {

    /**
     * @param s1 : Station de départ de la liaison, ne peut pas être null.
     * @param s2 : Station d'arrivée de la liaison, ne peut pas être null.
     * @return : vrai si les deux stations sont connectées avec la connectivité du joueur ou négatif dans le cas contraire.
     */
    public  boolean connected(Station s1, Station s2);
}