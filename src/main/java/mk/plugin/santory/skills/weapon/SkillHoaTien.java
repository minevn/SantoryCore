package mk.plugin.santory.skills.weapon;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.shooter.Shooter;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class SkillHoaTien implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        int scale = Double.valueOf((double) components.get("scale")).intValue();

        var is = player.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR) return;

        Items.setTempShooter(is, Shooter.FLAMED_ARROW, scale * 1000L);

        var shooter = Items.getShooter(is);

        if (!shooter.checkRequirements(player)) return;
        shooter.shoot(player, new Damage(Travelers.getStatValue(player, Stat.DAMAGE), DamageType.ATTACK), player.getLocation().getDirection().multiply(3), null);
    }
}
