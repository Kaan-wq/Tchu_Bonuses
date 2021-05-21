package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Félix Rodriguez Moya (325162)
 * @author Kaan Ucar (324467)
 */

public interface Serde<T>{

    String serialize(T object);
    T deserialize(String string);

    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize){
        return new Serde<>(){

            @Override
            public String serialize(T object) {
                return serialize.apply(object);
            }

            @Override
            public T deserialize(String string) {
                return deserialize.apply(string);
            }
        };
    }

    static <T> Serde<T> oneOf(List<T> list){
        return Serde.of(
                (T object) -> String.valueOf(list.indexOf(object)),
                (String txt) -> list.get(Integer.parseInt(txt))
        );
    }

    static <T> Serde<List<T>> listOf(Serde<T> serde, String c){
        return Serde.of(
                (List<T> txtList) -> txtList.stream().map(serde::serialize).collect(Collectors.joining(c)),
                (String serialisedTxt) -> serialisedTxt.equals("") ? List.of() : Arrays.stream(serialisedTxt.split(Pattern.quote(c), -1)).map(serde::deserialize).collect(Collectors.toList())
        );
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String c){
        return Serde.of(
                (SortedBag<T> txtList) -> txtList.stream().map(serde::serialize).collect(Collectors.joining(c)),
                (String serialisedTxt) -> serialisedTxt.equals("") ? SortedBag.of() : SortedBag.of(Arrays.stream(serialisedTxt.split(Pattern.quote(c), -1)).map(serde::deserialize).collect(Collectors.toList()))
                );
    }
}