package ch.epfl.tchu;

/**
 * Class Precondition is used to check a condition throwing an IllegalArgumentException if it is not satisfied
 * @author Elija Dirren (310502)
 * @author Lorin Lieberherr (326858)
 */
public final class Preconditions {
    private Preconditions(){}

    /**
     *
     * @param shouldBeTrue condition that should be true
     * @throws IllegalArgumentException if the condition is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue){
            throw new IllegalArgumentException();
        }

    }


}
