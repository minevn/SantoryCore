package mk.plugin.santory.skills;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.item.shooter.Shooter;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class SkillBaoTen implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        new BukkitRunnable() {
            int i = 0;

            LivingEntity target = null;

            @Override
            public void run() {
                i++;
                if (i > 25) {
                    this.cancel();
                    return;
                }

                if (target == null || target.isDead() || target.getWorld() != player.getWorld()) target = Utils.getTarget(player, 30);

                Location targetL;
                if (target != null) targetL = target.getLocation().clone().add(0, 1.5, 0);
                else targetL = player.getLocation().clone().add(player.getLocation().getDirection().multiply(8));

                Location rl = player.getLocation().add(0, 10, 0);
                Location sl = Utils.ranLoc(rl, 8);
                Vector v = targetL.clone().subtract(sl).toVector().normalize().multiply(2.1f);

                Arrow a = (Arrow) Shooter.BOW.shoot(player, new Damage(damage, DamageType.SKILL), v, sl);
                a.setCritical(true);
                a.setGlowing(true);
            }
        }.runTaskTimer(SantoryCore.get(), 0, 4);
    }

}