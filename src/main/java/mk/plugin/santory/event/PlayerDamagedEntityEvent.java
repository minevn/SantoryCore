package mk.plugin.santory.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDamagedEntityEvent extends PlayerEvent {

    /*
     * Detection:
     * 1. Interact Left Click Air >> Hand animation
     * 2. Damage >> Hand animation
     */

    private LivingEntity target;
    private double damage;

    public PlayerDamagedEntityEvent(Player who, LivingEntity target, double damage) {
        super(who);
        this.target = target;
        this.damage = damage;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public double getDamage() {
        return damage;
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