package mk.plugin.santory.skills.shield;

import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class SkillHoiPhuc implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        var maxhealth = Travelers.getStatValue(player, Stat.HEALTH);
        double healthRegain = ((double) components.get("scale")) * maxhealth / 100;

        int speedTick = 60;

        // Heal & effect
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedTick, 1));
        Utils.addHealth(player, healthRegain);

        // Effect
        Tasks.async(() -> {
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().clone().add(0, 1, 0), 0, 0.5f, 0.5f, 0.5f, 0.1f);
        }, 0, 1, speedTick * 50L);
    }
}
