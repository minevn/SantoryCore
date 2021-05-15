package mk.plugin.santory.skills;

import com.google.common.collect.Sets;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;

public class SkillHacSinh implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        Location l = player.getLocation().add(player.getLocation().getDirection()).add(0, 1.3, 0);
        Vector d = player.getLocation().getDirection();
        Phantom p = (Phantom) player.getWorld().spawnEntity(l, EntityType.PHANTOM);
        p.setVelocity(d.multiply(2));
        p.setAI(false);
        p.setNoDamageTicks(1000);
        p.setMetadata("settings.bypass", new FixedMetadataValue(SantoryCore.get(), ""));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 1, 1);

        long start = System.currentTimeMillis();
        long delay = 50 * 7; // 7 tick
        Set<LivingEntity> targets = Sets.newHashSet();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= delay) {
                    p.remove();
                    Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                        for (LivingEntity le : targets)  Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
                    });
                    this.cancel();
                    return;
                }
                for (Entity entity : p.getWorld().getEntities()) {
                    if (entity != player && entity instanceof LivingEntity && entity.getLocation().distanceSquared(p.getLocation()) < 3.5)
                        targets.add((LivingEntity) entity);
                }
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
    }

}
