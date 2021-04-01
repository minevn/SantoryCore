package mk.plugin.santory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class PlayerItemModifyEvent extends PlayerEvent {

    private boolean isSuccess;
    private int previous;
    private int after;

    public PlayerItemModifyEvent(Player who, boolean isSuccess, int previous, int after) {
        super(who);
        this.isSuccess = isSuccess;
        this.previous = previous;
        this.after = after;
    }

    public int getPrevious() {
        return this.previous;
    }

    public int getAfter() {
        return after;
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
