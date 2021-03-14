package mk.plugin.santory.skills;

import com.google.common.collect.Lists;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WSXoayNuoc implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		Location l = player.getLocation().clone().add(0, 1, 0);
		
		player.getWorld().playSound(l, Sound.ENTITY_PLAYER_SWIM, 1, 1);
		player.getWorld().playSound(l, Sound.ENTITY_PLAYER_SWIM, 1, 1);
		player.getWorld().playSound(l, Sound.ENTITY_PLAYER_SWIM, 1, 1); 
		new BukkitRunnable() {			
			int c = 0;
			@Override
			public void run() {
				c++;
				Location newl = l.clone().add(l.getDirection().multiply(c * 0.6));
				show(player, newl, 1.5, l.getPitch() + 90, -1 * l.getYaw(), 0, c * 10).forEach(lc -> {
					player.getWorld().spawnParticle(Particle.REDSTONE, lc, 0, (double) 204 / 255, (double) 255 / 255, 255 / 255, 1, new Particle.DustOptions(Color.AQUA, 1));
					player.getWorld().spawnParticle(Particle.WATER_DROP, lc, 0, (double) 204 / 255, (double) 255 / 255, 255 / 255, 1);
					player.getWorld().spawnParticle(Particle.DRIP_WATER, lc, 0, (double) 204 / 255, (double) 255 / 255, 255 / 255, 1);

					for (Entity entity : l.getWorld().getEntities()) {
						if (entity.getLocation().distanceSquared(lc) > 1) continue;
						if (entity instanceof LivingEntity && entity != player) {
							if (!Utils.canAttack(entity)) return;
							Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
								Damages.damage(player, (LivingEntity) entity, new Damage(damage, DamageType.SKILL), 15);
							});
						}
					}
					
				});
				if (c > 20) this.cancel();
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
		
	}
	

	public static List<Location> createCircle(Location location, double radius, double startAngle) {
		int amount = 9;
		double increment = (2 * Math.PI) / amount;
		double start = startAngle / 180 * Math.PI;
		ArrayList<Location> locations = new ArrayList<Location>();

		for (int i = 0; i < amount; i++) {
			double angle = start + i * increment;
			double x = location.getX() + (radius * Math.cos(angle));
			double z = location.getZ() + (radius * Math.sin(angle));
			locations.add(new Location(location.getWorld(), x, location.getY(), z));
		}

		return locations;
	}

	public List<Location> show(Player player, Location l, double r, double angleX, double angleY, double angleZ,
			double startAngle) {
		Location pl = l;
		Location c = l;
		List<Location> list = createCircle(pl, r, startAngle);
		List<Vector> list2 = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			list2.add(list.get(i).clone().subtract(c.clone()).toVector().clone());
		}

		double sinX = Math.sin(Math.toRadians(angleX));
		double cosX = Math.cos(Math.toRadians(angleX));
		list2 = list2.stream().map(vec -> rotateAroundAxisX(vec, cosX, sinX)).collect(Collectors.toList());

		double sinY = Math.sin(Math.toRadians(angleY));
		double cosY = Math.cos(Math.toRadians(angleY));
		list2 = list2.stream().map(vec -> rotateAroundAxisY(vec, cosY, sinY)).collect(Collectors.toList());

		double sinZ = Math.sin(Math.toRadians(angleZ));
		double cosZ = Math.cos(Math.toRadians(angleZ));
		list2 = list2.stream().map(vec -> rotateAroundAxisZ(vec, cosZ, sinZ)).collect(Collectors.toList());

		for (int i = 0; i < list.size(); i++) {
			list.set(i, c.clone().add(list2.get(i).clone()));
		}

		return list;
	}

	public Vector rotateAroundAxisX(Vector v, double cos, double sin) {
		double y = v.getY() * cos - v.getZ() * sin;
		double z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public Vector rotateAroundAxisY(Vector v, double cos, double sin) {
		double x = v.getX() * cos + v.getZ() * sin;
		double z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
		double x = v.getX() * cos - v.getY() * sin;
		double y = v.getX() * sin + v.getY() * cos;
		return v.setX(x).setY(y);
	}

}
