package mk.plugin.santory.event;

import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.skill.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSkillExecuteEvent extends PlayerEvent {

    private final Skill skill;

    public PlayerSkillExecuteEvent(Player who, Skill skill) {
        super(who);
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
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
