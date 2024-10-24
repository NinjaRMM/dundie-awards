package com.ninjaone.dundie_awards.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SanitizationUtils {

    /**
     * Applies the value returned by newValueSupplier to the existingValueMutator if
     * newValueSupplier does not return null.
     */
    public static <T> void mutateIfNotNull(Supplier<T> newValueSupplier, Consumer<T> existingValueMutator) {
        Optional.ofNullable(newValueSupplier.get())
                .ifPresent(existingValueMutator);
    }

    /**
     * Applies the value returned by newValueSupplier to the existingValueMutator if
     * newValueSupplier does not return null.
     */
    public static void mutateTrimmedStringIfNotNull(Supplier<String> newValueSupplier, Consumer<String> existingValueMutator) {
        Optional.ofNullable(newValueSupplier.get())
                .map(StringUtils::trimToNull)
                .ifPresent(existingValueMutator);
    }
}
