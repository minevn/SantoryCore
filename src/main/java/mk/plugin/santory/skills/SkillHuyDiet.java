package mk.plugin.santory.skills;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class SkillHuyDiet implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        var landed = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(5)));
        var l = landed.clone().add(0, 20, 0);
        player.getWorld().playSound(landed, Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);

        var g = (Giant) player.getWorld().spawnEntity(l, EntityType.GIANT);
        g.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        g.setNoDamageTicks(1000);
        g.setCollidable(false);
        g.setVelocity(new Vector(0, -4.5, 0));


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!g.isOnGround()) return;
                this.cancel();
                player.getWorld().playSound(g.getLocation(), Sound.ENTITY_RAVAGER_HURT, 1, 1);
                player.getWorld().playSound(g.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, g.getLocation().add(0, 0.3, 0), 100, 1, 1, 1, 0.01, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, g.getLocation().add(0, 0.3, 0), 10, 2, 1, 2, 0.01);
                Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                    for (Entity entity : landed.getWorld().getEntities()) {
                        if (entity != player && entity instanceof LivingEntity && entity.getLocation().distanceSquared(landed) < 15) {
                            Damages.damage(player, (LivingEntity) entity, new Damage(damage, DamageType.SKILL), 5);
                        }
                    }
                });
                Tasks.sync(() -> {
                    g.setHealth(0);
                });
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);

    }
}
