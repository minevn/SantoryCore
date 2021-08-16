package mk.plugin.santory.skills.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;

public class WSBongDem implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				Location center = player.getLocation().clone().add(0, 2, 0);
				circle(player.getLocation(), i, 0, true, true, 0).forEach(l -> {
					player.spawnParticle(Particle.SMOKE_LARGE, l, 1, 0f, 0f, 0f, 0f);
				});
				if (i == 6) {
					Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 5));
						center.getWorld().getNearbyEntities(center, 5, 5, 5).forEach(e -> {
							if (e instanceof Player && e != player) {
								Player target = (Player) e;
								Damages.damage(player, target, new Damage(damage, DamageType.SKILL), 5);
								effectPlayer(target);
							}
							else if (e instanceof LivingEntity && e != player) {
								LivingEntity le = (LivingEntity) e;
								Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
								effectEntity(le);
							}
						});
					});
					this.cancel();
					return;
				}
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);

	}

	public void effectPlayer(Player player) {
		int seconds = 5;
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * seconds, 5));
		long start = System.currentTimeMillis();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - start >= seconds * 1000) {
					this.cancel();
					return;
				}
				player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.3);
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 5);
	}
	
	public void effectEntity(LivingEntity le) {
		int seconds = 3;
		le.setAI(false);
		World world = le.getWorld();
		long start = System.currentTimeMillis();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - start >= seconds * 1000) {
					this.cancel();
					le.setAI(true);
					return;
				}
				world.spawnParticle(Particle.SMOKE_LARGE, le.getLocation().add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.0);
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 5);
	}
	
	public static List<Location> circle(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere,
			int plus_y) {
		List<Location> circleblocks = new ArrayList<Location>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for (int x = cx - r; x <= cx + r; x++)
			for (int z = cz - r; z <= cz + r; z++)
				for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
						Location l = new Location(loc.getWorld(), x, y + plus_y, z);
						circleblocks.add(l);
					}
				}

		return circleblocks;
	}

}
