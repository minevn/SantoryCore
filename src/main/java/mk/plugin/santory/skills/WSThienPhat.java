package mk.plugin.santory.skills;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class WSThienPhat implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		for (int i = 0 ; i < 3 ; i++) {
			final int index = i;
			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				Location l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(4)).clone());
				player.getWorld().strikeLightningEffect(l.clone().add(0, 0.3, 0));
				player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, l.clone().add(0, 0.7, 0), 1, 0, 0, 0, 0);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						for (Entity e : l.getWorld().getNearbyEntities(l, 2.5, 10, 2.5)) {
							if (e instanceof LivingEntity && e != player) {
								if (!Utils.canAttack(e)) continue;
								Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 5);
								if (index == 2) e.setVelocity(e.getLocation().subtract(l).toVector().normalize().multiply(0.3).setY(0.8));
								e.setFireTicks(20);
							}
						}
					}
				}.runTask(SantoryCore.get());
			}, i * 10);
		}
	}

}
