package mk.plugin.santory.skills;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class SkillDocTo implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        var l = player.getLocation().add(0, 1.3, 0).clone();

        Vector v = player.getLocation().getDirection().setY(0);
        v.setX(v.getX() * -1);
        v.setZ(v.getZ() * -1);
        v.setY(1.5);

        player.setVelocity(v);
        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);

        // 6s >> 12 x 10tick
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if (i > 12) {
                    this.cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, l, 40, 1.5, 1, 1.5, 0.01);
                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l, 40, 1.5, 1, 1.5, 0.01);
                for (Entity entity : l.getWorld().getEntities()) {
                    if (entity != player && entity instanceof LivingEntity && entity.getLocation().distanceSquared(l) < 15) {
                        Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                            Damages.damage(player, (LivingEntity) entity, new Damage(damage, DamageType.SKILL), 5);
                        });
                    }
                }

            }
        }.runTaskTimer(SantoryCore.get(), 0, 10);

    }
}
