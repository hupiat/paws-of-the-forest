package org.warriorcats.pawsOfTheForest.clans;

import lombok.Getter;
import org.warriorcats.pawsOfTheForest.utils.StringUtils;

@Getter
public enum Clans {
    BREEZE("#FFFACD"), ECHO("#800080"), CREEK("#008080"), SHADE("#800020");

    private final String color;

    Clans(String color) {
        this.color = color;
    }

    public String getColorCode() {
        return net.md_5.bungee.api.ChatColor.of(this.color).toString();
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(name().toLowerCase()) + StringUtils.capitalize("clan");
    }
}
