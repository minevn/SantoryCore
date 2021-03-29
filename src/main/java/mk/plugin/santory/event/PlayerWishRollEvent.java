package mk.plugin.santory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerWishRollEvent extends PlayerEvent {

    private final String wishID;

    public PlayerWishRollEvent(Player who, String wishID) {
        super(who);
        this.wishID = wishID;
    }

    public String getWishID() {
        return wishID;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
