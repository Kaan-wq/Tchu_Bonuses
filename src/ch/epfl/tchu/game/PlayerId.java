
package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    
    public final static int COUNT = ALL.size();
    
    /**
     * @return : l'identifiant du suivant joueur de la partie.
     */
    public PlayerId next() {
        if(this.equals(PLAYER_1)) {
            return PLAYER_2;
        }else {
            return PLAYER_1;
        }
    }
}