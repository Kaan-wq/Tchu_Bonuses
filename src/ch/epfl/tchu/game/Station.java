package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 * 
 * Classe qui modélise les stations du jeu
 */

public final class Station {
    private final int id;
    private final String name;

    /**
     * @param id : numéro de référence de la station, unique et positif.
     * @param name : nom de la station, peut se répéter.
     * @throws IllegalArgumentException : si l'id est inférieur à 0.
     */
    public Station(int id, String name) throws IllegalArgumentException {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * @return numéro de référence de la station
     */
    public int id() {
        return id;
    }

    /**
     * @return nom de la station
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() { return name(); }
}