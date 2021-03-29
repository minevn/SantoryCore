package mk.plugin.santory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerItemModifyEvent extends PlayerEvent {

    private boolean isSuccess;

    public PlayerItemModifyEvent(Player who, boolean isSuccess) {
        super(who);
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
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
