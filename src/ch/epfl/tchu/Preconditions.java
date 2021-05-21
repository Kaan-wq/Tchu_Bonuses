package ch.epfl.tchu;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 *
 * Classe qui permet d'effectuer des tests
 */

public final class Preconditions {
    private Preconditions(){}

    /**
     *Si le booléen est faux le programme lance une exception
     * @param shouldBeTrue: un boolean qui doit etre vrai
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
