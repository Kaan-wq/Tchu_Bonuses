package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 *
 * Classe qui attribut une couleur aux cartes wagons et routes
 */

public enum Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    public final static List<Color> ALL = List.of(Color.values());
    public static final int COUNT = ALL.size();

}
