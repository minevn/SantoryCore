package mk.plugin.santory.skills.weapon;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

public class WSDiaChan implements SkillExecutor {
	
	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		for (int i = 0 ; i < 3 ; i++) {
			
			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				Location l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(2)).clone());
				l.getWorld().spawnParticle(Particle.LAVA, l.add(0, 0.5, 0), 50, 2.5, 0.5, 2.5, 0);
				l.getWorld().spawnParticle(Particle.FLAME, l.add(0, 0.5, 0), 50, 2.5, 0.5, 2.5, 0);
				l.getWorld().spawnParticle(Particle.CRIT, l.add(0, 0.5, 0), 50, 2.5, 0.5, 2.5, 0);
				l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1);
				l.getWorld().playSound(l, Sound.BLOCK_STONE_PLACE, 0.1f, 1);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						for (Entity e : l.getWorld().getNearbyEntities(l, 2.5, 1, 2.5)) {
							if (e instanceof LivingEntity && e != player) {
								if (!Utils.canAttack(e)) continue;
								Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 5);
								e.setVelocity(e.getLocation().subtract(l).toVector().normalize().multiply(0.2).setY(0.4));
								e.setFireTicks(20);
							}
						}
					}
				}.runTask(SantoryCore.get());
			}, i * 15);
			
			
		}
		

	}
	
}
