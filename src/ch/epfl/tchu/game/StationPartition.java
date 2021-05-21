package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 */

public final class StationPartition implements StationConnectivity {

    private final int []partitions;

    private StationPartition(int[] tab) {
        this.partitions = tab;
    }

    /**
     * @param s1 : Station de départ de la liaison, ne peut pas être null.
     * @param s2 : Station d'arrivée de la liaison, ne peut pas être null.
     * @return true si les deux stations sont connectées
     */
    @Override
    public boolean connected(Station s1, Station s2) {

        if(s1.id() < partitions.length && s2.id() < partitions.length){
            return partitions[s1.id()] == partitions[s2.id()];
        }else{
            return s1.equals(s2);
        }
    }

    public final static class Builder{

        private int []partitionBuilder;

        /**
         * @param stationCount la longueur du tableau qui correspond au nombre de gares
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            int []tab = new int[stationCount];
            for(int i = 0; i < tab.length; ++i){
                tab[i] = i;
            }
            this.partitionBuilder = tab;
        }

        /**
         * @param s1 : station à connecter
         * @param s2 : station à connecter
         * @return :joint les sous-ensembles contenant les deux gares passées en argument,
         * en «élisant» l'un des deux représentants comme représentant du sous-ensemble joint
         */
        public Builder connect(Station s1, Station s2) {
            this.partitionBuilder[representative(s2.id())] = this.representative(s1.id());
            return this;
        }

        /**
         * @return la partition aplatie des gares correspondant à la partition profonde
         * en cours de construction par ce bâtisseur
         */
        public StationPartition build() {
            for(int i = 0; i < this.partitionBuilder.length; ++i){
                this.partitionBuilder[i] = this.representative(i);
            }
            return new StationPartition(this.partitionBuilder);
        }

        /**
         * @param stationId l'indice de la station dans le tableau (aussi son id)
         * @return le représentant du sous-ensemble auquel il appartient
         */
        private int representative(int stationId) {
            while(stationId != this.partitionBuilder[stationId]){
                stationId = this.partitionBuilder[stationId];
            }
            return stationId;
        }
    }
}