package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Kaan Ucar (324467)
 * @author Félix Rodriguez Moya (325162)
 *
 * Classe qui modélise les tickets
 */


public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> voyage = new ArrayList<>();
    private final String texte;

    /**
     * Le constructeur principal de la classe Ticket
     * @param trips : liste de trips
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());

        boolean sameDeparture = true;

        for(Trip trip: trips){
            if(trip.from().name().equals(trips.get(0).from().name())){
                this.voyage.addAll(trips);
            }else{
                sameDeparture = false;
            }
        }
        Preconditions.checkArgument(sameDeparture);
        texte = toString();
    }

    /**
     * Le constructeur secondaire qui consiste
     * en un simple appel du constructeur principal
     * pour les voyages uniques ( unique destination)
     * @param from : station de départ
     * @param to : station d'arrivée
     * @param points : les points du ticket
     */
    public Ticket(Station from, Station to, int points){
        this(new ArrayList<>(Collections.singletonList(new Trip(from, to, points))));
    }

    /**
     * Méthode qui regroupe le nom des gares et points qu'elles valent
     * pour les mettre sous forme d'une chaine de caractères
     * @return la représentation textuelle d'un ticket
     */
    private String computeText(){
        TreeSet<String> destProt = new TreeSet<>();

        voyage.forEach((a)->{
            String namePoints = String.format("%s (%s)", a.to().name(), a.points());
            destProt.add(namePoints);
        });

        String text;
        if(destProt.size() > 1){
            String destinations = String.join(", ", destProt);
            text = String.format("%s - {%s}", voyage.get(0).from().name(), destinations);
        }else{
            String destinations = String.join("", destProt);
            text = String.format("%s - %s", voyage.get(0).from().name(), destinations);
        }
        return text;
    }

    /**
     * C'est un simple appel à la méthode computeText décrite ci-dessus
     * @return la représentation textuelle du ticket
     */
    public String text(){
        return texte;
    }

    /**
     * Méthode calculant le nombre de points maximal que vaut un ticker
     * @param connectivity : la connectivité des gares
     * @return le nombre de points que vaut le ticket
     */
    public int points(StationConnectivity connectivity){
        List<Integer> points = new ArrayList<>();
        voyage.forEach(e -> points.add(e.points(connectivity)));
        return Collections.max(points);
    }

    /**
     * @param that ticket à comparer
     * @return un entier strictement négatif si this
     * est strictement plus petit que that, un entier strictement positif si this
     * est strictement plus grand que that, et zéro si les deux sont égaux
     */
    @Override
    public int compareTo(Ticket that) {
        return text().compareTo(that.text());
    }

    /**
     * @return la représentation textuelle d'un billet
     * (pareil que la méthode text())
     */
    @Override
    public String toString() {
        return computeText();
    }

    /**
     * @return (List<Station>) : stations de départ et d'arrivée du ticket
     */
    public List<Station> getStations(){
        return List.of(voyage.get(0).from(), voyage.get(voyage.size() - 1).to());
    }
}