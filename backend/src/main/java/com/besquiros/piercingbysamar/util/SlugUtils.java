package com.besquiros.piercingbysamar.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtils {

    private SlugUtils() {}

    public static String toSlug(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.FRENCH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s]+", "-");
    }
}
