package mk.plugin.santory.skills;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class WSThauXuong implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				if (i > 5) {
					this.cancel();
					return;
				}
				player.swingMainHand();
				Location mainLoc = player.getLocation().add(0, 0.9, 0);
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.2f, 1.1f);
				Vector v = random(mainLoc.clone().getDirection(), 1f);
				for (int i = 0 ; i < 10; i ++) {
					Location loc =  mainLoc.clone().add(v.clone().multiply(i));
					player.getWorld().spawnParticle(Particle.CRIT, loc, 4, 0.1f, 0.1f, 0.1f, 0);
					player.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 4, 0.1f, 0.1f, 0.1f, 0);
					for (Entity entity : loc.getWorld().getEntities()) {
						if (entity.getLocation().distanceSquared(loc) > 1) continue;
						if (entity instanceof LivingEntity && entity != player) {
							if (!Utils.canAttack(entity)) return;
							Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
								Damages.damage(player, (LivingEntity) entity, new Damage(damage, DamageType.SKILL), 5);
							});
						}
					}
				}
			}
			
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 3);
		
		
	}
	
	private Vector random(Vector v, float tl) {
		double x = v.getX() * (1 + Utils.random(0, tl));
		double y = v.getY() * (1 + Utils.random(0, tl));
		double z = v.getZ() * (1 + Utils.random(0, tl));
		
		return new Vector(x, y, z);
	}
	
}
