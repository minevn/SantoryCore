package mk.plugin.santory.skills;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;

public class SkillSetDien implements SkillExecutor {

    @Override
    public void start(Map<String, Object> map) {
        String slaveID = (String) map.get("slave");
        Player player = Slaves.getMasterPlayer(slaveID);
        LivingEntity slave = Slaves.getSlaveEntity(slaveID);
        int level = (int) map.get("level");
        double basedamage = Slaves.getDamage(slaveID) * 1.5;

        boolean damageUp1 = level >= 2;
        boolean onfire = level >= 3;
        boolean damageUp2 = level >= 4;
        boolean stun = level >= 5;

        double d = basedamage;
        if (damageUp1) d += basedamage * 0.75;
        if (damageUp2) d += basedamage * 0.75;

        double damage = d;

        Location l = Utils.getLandedLocation(slave.getLocation().add(slave.getLocation().getDirection().multiply(3)).clone());
        player.getWorld().strikeLightningEffect(l.clone().add(0, 0.3, 0));

        for (Entity e : l.getWorld().getNearbyEntities(l, 1.5, 1.5, 1.5)) {
            if (e != player && e != slave && e instanceof LivingEntity && Utils.canAttack(e)) {
                Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 5);
                if (onfire) e.setFireTicks(20);
                if (stun) {
                    if (e instanceof Player) Utils.stunPlayer((Player) e, 5);
                    else Utils.stunEntity((LivingEntity) e, 5);
                }
            }
        }

    }

}
