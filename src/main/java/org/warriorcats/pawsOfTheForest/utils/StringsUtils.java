package org.warriorcats.pawsOfTheForest.utils;

public abstract class StringsUtils {

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String capitalizeWithSpaces(String input, String splitSymbol) {
        StringBuilder builder = new StringBuilder();
        String[] split = input.toLowerCase().split(splitSymbol);
        for (String stub : split) {
            builder.append(StringsUtils.capitalize(stub));
            builder.append(" ");
        }
        return builder.toString().trim();
    }
}
