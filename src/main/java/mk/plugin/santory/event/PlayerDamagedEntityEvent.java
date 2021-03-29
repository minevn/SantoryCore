package mk.plugin.santory.event;

import mk.plugin.santory.damage.DamageType;
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
    private DamageType damageType;

    public PlayerDamagedEntityEvent(Player who, LivingEntity target, double damage, DamageType damageType) {
        super(who);
        this.target = target;
        this.damage = damage;
        this.damageType = damageType;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public double getDamage() {
        return damage;
    }

    private static final HandlerList handlers = new HandlerList();

    public DamageType getDamageType() {
        return damageType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}