package mk.plugin.santory.skills.weapon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import mk.plugin.santory.utils.imagerenderer.api.ParticleImageRenderingAPI;
import mk.plugin.santory.utils.imagerenderer.math.Quaternion;
import mk.plugin.santory.utils.imagerenderer.math.Vec3d;
import mk.plugin.santory.utils.imagerenderer.util.Axis;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SkillBocPhaThien implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        int allTime = 7;
        int startShowToOthers = 5;
        long start = System.currentTimeMillis();

        File file = new File(SantoryCore.get().getDataFolder() + "//particles//skill-bocphathien.png");
        List<Float> list = Lists.newArrayList(1.3f, 0.7f, 1.1f, 1f, 0.6f, 0.5f, 0.3f);
        var targetBlock = player.getTargetBlock(Sets.newHashSet(Material.AIR), 200);
        Location center = targetBlock.getLocation().add(0.5, 0, 0.5);

        // Sound
        center.getWorld().playSound(center, Sound.ENTITY_WITHER_DEATH, 10, 1);

        // Check players
        List<Player> players = Lists.newArrayList(player);
        Tasks.sync(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != player && p.getWorld() == center.getWorld() && p.getLocation().distance(center) <= 50) {
                    players.add(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
                }
            }
        }, startShowToOthers * 20);

        // Center
        Tasks.async(() -> {
            for (int i = 0 ; i < 70 ; i++) {
                var d = (double) 20 / 70 * i;
                for (Player p : players) {
                    p.spawnParticle(Particle.REDSTONE, center.clone().add(0, d, 0), 1, 0, 0, 0, new Particle.DustOptions(Color.RED, 1.5f));
                }
            }
        }, 0, 5, allTime * 1000L);

        // Main effect
        for (int i = 0; i < list.size(); i++) {
            Location l = center.clone().add(0, 20 - i * 2.5, 0);
            float redstoneSize = list.get(i) * 1.5f;
            var v = Vec3d.fromLocation(l);
            ParticleImageRenderingAPI api = null;
            try {
                api = new ParticleImageRenderingAPI(file, v);
                api.setRotation(new Quaternion(list.get(i), 0, 0, 0));
                api.rotate(Axis.Y, new Random().nextInt(90));

                ParticleImageRenderingAPI finalApi = api;
                Tasks.async(() -> {
                    finalApi.rotate(Axis.Y, 2);

                    // Show
                    finalApi.renderImage(players, redstoneSize);
                }, 0, 2, allTime * 1000L);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Explode
        Tasks.async(() -> {
            // Damage
            Tasks.sync(() -> {
                int max = 13;
                for (Entity entity : center.getWorld().getEntities()) {
                    var d = entity.getLocation().distance(center);
                    if (entity != player && Utils.canAttack(entity) && entity instanceof LivingEntity && d <= max) {
                        var dmg = damage * (max - d) / max;
                        Damages.damage(player, (LivingEntity) entity, new Damage(dmg, DamageType.SKILL), 5);
                    }
                }
            }, 5);

            // Effect
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    i++;
                    if (i > 10) {
                        this.cancel();
                        return;
                    }
                    if (i % 3 == 1) center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
                    if (i == 1) {
                        center.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center.clone().add(0, 1.2, 0), 1, 0, 0, 0);
                        return;
                    }
                    Utils.circleParticles(Particle.EXPLOSION_LARGE, center.clone().add(0, 1.2, 0), i * 1.5);
                }
            }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
        }, allTime * 20);
    }

}
