package com.label305.stan;

import org.jetbrains.annotations.NotNull;

public class Dependency {

    private Dependency() {
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public static boolean isPresent(@NotNull final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable ignored) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }
}
