package mk.plugin.santory.skills.shield;

import com.google.common.collect.Lists;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import mk.plugin.santory.utils.imagerenderer.api.ParticleImageRenderingAPI;
import mk.plugin.santory.utils.imagerenderer.math.Quaternion;
import mk.plugin.santory.utils.imagerenderer.math.Vec3d;
import mk.plugin.santory.utils.imagerenderer.util.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SkillNguyenRua implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        int time = Double.valueOf((double) components.get("scale")).intValue() * 20;
        float radius = 3;

        Location l = player.getLocation().clone().add(0, 0.2, 0);
        l.getWorld().playSound(l, Sound.ENTITY_GHAST_DEATH, 1, 1);

        // Effect
        File file = new File(SantoryCore.get().getDataFolder() + "//particles//skill-nguyenrua.png");
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld() == l.getWorld() && player.getLocation().distanceSquared(l) <= 100).collect(Collectors.toList());
        var v = Vec3d.fromLocation(l);
        ParticleImageRenderingAPI api = null;
        try {
            api = new ParticleImageRenderingAPI(file, v);
            api.setRotation(new Quaternion(0.9f, 0, 0, 0));
            api.rotate(Axis.Y, new Random().nextInt(90));
            ParticleImageRenderingAPI finalApi = api;
            Tasks.async(() -> {
                l.getWorld().spawnParticle(Particle.SOUL, l, 18, radius * 0.7, 1, radius * 0.7, 0.01f);
                finalApi.renderImage(players, 1.9f);
            }, 0, 7, time * 50L);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Checking
        Tasks.sync(() -> {
            for (LivingEntity le : l.getNearbyLivingEntities(radius, 1, radius)) {
                if (!Utils.canAttack(le) || le == player) continue;

                // Effect
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 5));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 5));
                le.getWorld().spawnParticle(Particle.SOUL, le.getLocation().clone().add(0, 1, 0), 2, 0.5, 0.5, 0.5, 0.1f);
                if (le instanceof Player) {
                    var target = (Player) le;
                    target.sendTitle("§c§l§kDASJNDOQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWD", "§c§l§kDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWDDJOQWDPQWDOQWDPQWDQWPODQWD", 0, 30, 0);
                }
            }
        }, 0, 2, time * 50L);

    }


}
