package org.warriorcats.pawsOfTheForest.clans;

import lombok.Getter;

@Getter
public enum Clans {
    BREEZE("#FFFACD"), ECHO("#800080"), CREEK("#008080"), SHADE("#800020");

    private final String color;

    Clans(String color) {
        this.color = color;
    }
}
