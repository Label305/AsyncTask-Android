package com.label305.stan.utils;

/**
 * Created by Label305 on 02/06/2014.
 */
public class Dependency {
    public static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }
}
