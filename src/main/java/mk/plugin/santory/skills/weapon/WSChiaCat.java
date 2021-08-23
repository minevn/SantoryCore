package mk.plugin.santory.skills.weapon;

import com.google.common.collect.Lists;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WSChiaCat implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1f, 1f);

		shoot(player, damage, 45);
		shoot(player, damage, 135);
	}
	
	public static void shoot(Player player, double damage, double pitch) {
		Location l = player.getLocation().clone();
		l.add(0, 1.5, 0);
		new BukkitRunnable() {
			int c = 0;		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				c++;				
				if (c > 30) {
					this.cancel();
					return;
				}
				Location center = l.clone().add(l.getDirection().setY(0).multiply(c * 1.2));
				center.getWorld().playSound(center, Sound.ENTITY_ZOMBIE_INFECT, 1, 1);
				List<Location> list = show(player, center, 5, l.getPitch() + pitch, -1 * l.getYaw() + 90, 0);
				list.forEach(loc -> {
					loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0);
				});
				list.forEach(loc -> {
					for (Entity e : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
						if (e instanceof LivingEntity && e != player) {
							if (!Utils.canAttack(e)) continue;
							new BukkitRunnable() {
								@Override
								public void run() {
									Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 15);
								}
							}.runTask(SantoryCore.get());
						}
					}
				});

			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 3, 0);
	}

	public static List<Location> createCircle(Location location, double radius) {
		int amount = new Double(radius * 20).intValue() / 3 * 2;
		double increment = (2 * Math.PI) / amount;
		ArrayList<Location> locations = new ArrayList<Location>();

		for (int i = 0; i < amount; i++) {
			double angle = i * increment;
			double x = location.getX() + (radius * Math.cos(angle));
			double z = location.getZ() + (radius * Math.sin(angle));
			locations.add(new Location(location.getWorld(), x, location.getY(), z));
		}

		return locations;
	}
	
	public static List<Location> show(Player player, Location l, double mul, double angleX, double angleY, double angleZ) {
		Location pl = l;
		Location c = l;
		List<Location> list = createCircle(pl, 4);
		List<Vector> list2 = Lists.newArrayList();
		for (int i = 0 ; i < list.size() ; i++) {
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
		
		for (int i = 0 ; i < list.size() ; i++) {
			list.set(i, c.clone().add(list2.get(i).clone()));
		}
		
		return list;	
	}
    
    public static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }
	
	
	
	
	
	
	
}
