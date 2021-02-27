package mk.plugin.santory.skills;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

public class WSQuetKiem implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		double minR = 1.5;
		int amount = 20;
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
		BukkitRunnable br = new BukkitRunnable() {
			
			int i = 0;
			
			@Override
			public void run() {
				for (int k = 3 * i ; k < 3 * (i+1) ; k++) {
					for (int j = 0 ; j < 20 ; j ++) {
						Location l = player.getLocation().clone();
						double angle = Math.PI * 2 / (amount + j * 0.2) * k * 3;
						
						double newX = l.getX() + (minR + j * 0.1) * Math.sin(angle + l.getYaw() * -1);
						double newZ = l.getZ() + (minR + j * 0.1) * Math.cos(angle + l.getYaw() * -1);
						
						l.setX(newX);
						l.setZ(newZ);
						l.setY(l.getY() + 1.0);
						
//						player.getWorld().spawnParticle(Particle.REDSTONE, l, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.SILVER, 1));
						player.getWorld().spawnParticle(Particle.CRIT, l, 1, 0, 0, 0, 0);
						player.getWorld().spawnParticle(Particle.CRIT_MAGIC, l, 1, 0, 0, 0, 0);
					}
				}

				
				i++;
				if (i * 3 > amount) {
					this.cancel();
					Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
						player.getNearbyEntities(3, 3, 3).forEach(e -> {
							if (e != player && e instanceof LivingEntity) {
								LivingEntity le = (LivingEntity) e;
								if (!Utils.canAttack(e)) return;
								Damages.damage(player, (LivingEntity) le, new Damage(damage, DamageType.SKILL), 5);
							}
						});
					});
					return;
				}
			}
		};
		br.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
	}

}
