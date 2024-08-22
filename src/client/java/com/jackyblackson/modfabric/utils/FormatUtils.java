package com.jackyblackson.modfabric.utils;

import java.text.DecimalFormat;

public class FormatUtils {
    private static DecimalFormat doubleFormat = new DecimalFormat("0.#");
    public static String formatDouble(double number) {
        return doubleFormat.format(number);
    }
}
