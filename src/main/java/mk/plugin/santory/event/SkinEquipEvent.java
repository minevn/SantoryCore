package mk.plugin.santory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class SkinEquipEvent extends PlayerEvent {

    private final String skin;

    public SkinEquipEvent(@NotNull Player who, String skin) {
        super(who);
        this.skin = skin;
    }

    public String getSkin() {
        return skin;
    }

    // Required

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
