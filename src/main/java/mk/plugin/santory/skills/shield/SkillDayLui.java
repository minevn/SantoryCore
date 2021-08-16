package mk.plugin.santory.skills.shield;

import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class SkillDayLui implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        int duration = Double.valueOf((double) components.get("scale")).intValue();

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1f, 1f);
        Location main = player.getLocation().add(player.getLocation().getDirection().multiply(1));
        Utils.getLivingEntities(player, main, 3, 3, 3).forEach(le -> {
            if (!Utils.canAttack(le)) return;
            if (player == le) return;

            le.setVelocity(le.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(1));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, duration / 2));
        });
    }

}
