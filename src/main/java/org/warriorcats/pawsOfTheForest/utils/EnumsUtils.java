package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class EnumsUtils {

    public static <T extends Enum<T>> T from(String str, Class<T> enumClass) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.toString().toLowerCase().startsWith(str.toLowerCase())) {
                return constant;
            }
        }
        return Enum.valueOf(enumClass, str);
    }
}
