package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Interface Serde represents an object that is able to (de)serialize values of a given type.
 * The serde are used to establish the communication between server and client by transforming
 * java values in ASCII string (serialization) or ASCII String in java values (deserialization)
 *
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public interface Serde<T> {

    /**
     * This method serialized the given type
     *
     * @param type is the type to serialize
     * @return a string that corresponds to the serialized version of type given in argument
     */
    String serialize(T type);

    /**
     * This method deserialize the given String to return the type T corresponding
     *
     * @param serializedText is the serialized text of the Type T
     * @return the type that corresponds to the serialized text given in argument (deserialization)
     */
    T deserialize(String serializedText);

    /**
     * Create a serde that corresponding to the arguments given in the method
     *
     * @param serialize   is the function to apply on the type
     * @param deserialize is the function to apply on the serialized text
     * @param <T>         is the type of the Serde
     * @return a Serde of type T that is able to (de)serialized the type T
     */
    static <T> Serde<T> of(Function<T, String> serialize, Function<String, T> deserialize) {
        return new Serde<>() {
            /**
             * This method serializes the type T
             *
             * @param type is the type to serialize
             * @return the application on the type
             */
            @Override
            public String serialize(T type) {
                return serialize.apply(type);
            }

            /**
             * This method deserializes the string given in argument in type T
             *
             * @param serializedText is the serialized text of the Type T
             * @return the application on the serialized text
             */
            @Override
            public T deserialize(String serializedText) {
                return deserialize.apply(serializedText);
            }
        };
    }

    /**
     * Create a serde that can (de)serialized enumerated values
     *
     * @param enumElements is the list of elements that are contained in an enumeration, or in a list that have finite number of values
     * @param <T>          is the type of the Serde
     * @return a Serde of type T that is able to (de)serialized enumerated values (enumerated values are the values in enumElements)
     */
    static <T> Serde<T> oneOf(List<T> enumElements) {
        return new Serde<>() {
            /**
             * This method serializes the type T
             *
             * @param type is the type to serialize
             * @return the index of the type in the list of enumerated elements
             */
            @Override
            public String serialize(T type) {
                return Integer.toString(enumElements.indexOf(type));
            }

            /**
             * This method deserializes the string given in argument in type T
             *
             * @param serializedText is the serialized text of the Type T
             * @return the type that is at the index given by the serialized text
             */
            @Override
            public T deserialize(String serializedText) {
                return enumElements.get(Integer.parseInt(serializedText));
            }
        };
    }

    /**
     * Create a serde that can (de)serialize list of (de)serialized values
     *
     * @param serde     is the serde that it used to (de)serialized each element of the list
     * @param separator is a character that separates each element of the list
     * @param <T>       is the type of the elements that are contained in the list of the serde
     * @return a serde that can (de)serialize a list of elements T
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            /**
             * This method serializes the list of elements T
             *
             * @param list is the list to serialized
             * @return the serialized list with the separator between each element of the list
             */
            @Override
            public String serialize(List<T> list) {
                List<String> serializedList = new ArrayList<>();
                for (T t : list) {
                    serializedList.add(serde.serialize(t)); // serialization of each element of the list
                }
                return String.join(separator, serializedList); // we add the separator between each element of the list

            }

            /**
             * This method deserializes the given string in a list that has elements of type T
             *
             * @param serializedText is the serialized text of the Type T
             * @return the deserialized list
             */
            @Override
            public List<T> deserialize(String serializedText) {

                // if the serialized text is a string, we return an empty list
                if (serializedText.equals("")) {
                    return List.of();
                }

                // remove the separator and store each serialized element in a string array
                String[] stringWithoutSeparator = serializedText.split(Pattern.quote(separator), -1);

                List<T> deserializedList = new ArrayList<>();
                for (String s : stringWithoutSeparator) {
                    deserializedList.add(serde.deserialize(s)); // deserialization of each element of the list
                }
                return deserializedList;
            }
        };
    }

    /**
     * Create a serde that can (de)serialize SortedBag type
     *
     * @param serde     is the serde that it used to (de)serialized each element of the sortedBag
     * @param separator is a character that separates each element of the sortedBag
     * @param <T>       is the type of the elements that are contained in the sortedBag of the serde
     * @return a serde that can (de)serialize a sortedBag of elements T
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            /**
             * This method serializes SortedBag type
             *
             * @param sortedBag is the sortedBag to serialized
             * @return the serialized sortedBag with the separator between each element of the sortedBag
             */
            @Override
            public String serialize(SortedBag<T> sortedBag) {

                List<String> serializedList = new ArrayList<>();
                for (T t : sortedBag) {
                    serializedList.add(serde.serialize(t)); // serialization of each element of the sortedBag
                }
                return String.join(separator, serializedList); // we add the separator between each element of the sortedBag

            }

            /**
             * This method deserializes SortedBag type
             *
             * @param serializedText is the serialized text of the Type T
             * @return a the deserialized sortedBag
             */
            @Override
            public SortedBag<T> deserialize(String serializedText) {

                // if the serialized text is a string, we return an empty sortedBag
                if (serializedText.equals("")) {
                    return SortedBag.of();
                }

                // remove the separator and store each serialized element in a string array
                String[] stringWithoutSeparator = serializedText.split(Pattern.quote(separator), -1);

                List<T> deserializedList = new ArrayList<>();
                for (String s : stringWithoutSeparator) {
                    deserializedList.add(serde.deserialize(s));  // deserialization of each element of the sortedBag
                }
                return SortedBag.of(deserializedList);
            }
        };
    }
}
