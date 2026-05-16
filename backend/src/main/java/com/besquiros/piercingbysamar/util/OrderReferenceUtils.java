package com.besquiros.piercingbysamar.util;

import java.time.Year;

public class OrderReferenceUtils {

    private OrderReferenceUtils() {}

    public static String generate(long orderCount) {
        return String.format("PBS-%d-%04d", Year.now().getValue(), orderCount + 1);
    }
}
