package me.frostdev.frostyspawners.api.event;

import me.frostdev.frostyspawners.spawners.Spawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class SpawnerBreakEvent extends SpawnerEvent implements Cancellable {
    private Player player;
    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    public SpawnerBreakEvent(Spawner s, Player p) {
        super(s);
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

