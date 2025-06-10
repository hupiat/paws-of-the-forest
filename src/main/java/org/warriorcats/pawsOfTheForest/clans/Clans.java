package org.warriorcats.pawsOfTheForest.clans;

import lombok.Getter;
import org.warriorcats.pawsOfTheForest.utils.StringUtils;

import java.util.Optional;
import java.util.function.Function;

@Getter
public enum Clans {
    BREEZE("#FFFACD"), ECHO("#800080"), CREEK("#008080"), SHADE("#B3002C");

    private final String color;

    Clans(String color) {
        this.color = color;
    }

    public String getColorCode() {
        return net.md_5.bungee.api.ChatColor.of(this.color).toString();
    }

    public static Clans from(String clanStr) {
        for (Clans clan : values()) {
            if (clan.toString().toLowerCase().startsWith(clanStr.toLowerCase())) {
                return clan;
            }
        }
        return valueOf(clanStr);
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(name().toLowerCase()) + StringUtils.capitalize("clan");
    }
}
