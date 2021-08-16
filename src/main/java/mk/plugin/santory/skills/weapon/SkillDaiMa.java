package mk.plugin.santory.skills.weapon;

import com.google.common.collect.Sets;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;

public class SkillDaiMa implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        var l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection()).add(0, 1.3, 0));
        var z = (Zombie) player.getWorld().spawnEntity(l, EntityType.ZOMBIE);
        z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1000);
        z.setAdult();
        z.setNoDamageTicks(1000);
        z.setCollidable(false);
        z.setMetadata("settings.bypass", new FixedMetadataValue(SantoryCore.get(), ""));
        z.setMetadata("Dungeon3", new FixedMetadataValue(SantoryCore.get(), ""));

        long start = System.currentTimeMillis();
        long delay = 1000 * 3;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= delay) {
                    this.cancel();
                    z.remove();

                    // Get targets
                    Set<LivingEntity> targets = Sets.newHashSet();
                    for (Entity entity : z.getWorld().getEntities()) {
                        if (entity != player && entity instanceof LivingEntity && entity.getLocation().distanceSquared(z.getLocation()) < 9) {
                            targets.add((LivingEntity) entity);
                        }
                    }

                    // Explode
                    player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, z.getLocation().add(0, 0.5, 0), 2, 0.3, 0.3, 0.3);
                    player.getWorld().playSound(z.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                    Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                        for (LivingEntity le : targets)  Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
                    });
                }

                // Set target
                for (Entity entity : z.getWorld().getEntities()) {
                    if (entity instanceof Mob && entity.getLocation().distanceSquared(z.getLocation()) < 25) {
                        ((Mob) entity).setTarget(z);
                    }
                }

                // Particle
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, z.getLocation().add(0, 0.5, 0), 20, 0.3, 0.3, 0.3, 0);
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 5);
    }

}
