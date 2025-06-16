package org.warriorcats.pawsOfTheForest.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class PlayerOutCombatEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
