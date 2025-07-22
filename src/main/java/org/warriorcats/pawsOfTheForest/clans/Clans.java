package org.warriorcats.pawsOfTheForest.clans;

import lombok.Getter;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

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
        return EnumsUtils.from(clanStr, Clans.class);
    }

    @Override
    public String toString() {
        return StringsUtils.capitalize(name().toLowerCase()) + StringsUtils.capitalize("clan");
    }
}
