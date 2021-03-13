package mk.plugin.santory.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class WSBaoKiem implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		Location location = player.getLocation();
		var d = location.getDirection();
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				if (i >= 10) {
					this.cancel();
					return;
				}
				Location newLocation = location.clone().add(d.clone().multiply(i * 1.8));
				player.playSound(newLocation, Sound.ENTITY_GHAST_SHOOT, 0.5f, 1.5f);

				int i = 0;
				List<Location> list = getListLocation(newLocation);
				for (i = 0 ; i < list.size() ; i ++) {
					player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, list.get(i), new Double(i / 3).intValue(), i*0.05f, i*0.05f, i*0.05f, i*0.01f);
				}
				for (Entity e : location.getWorld().getNearbyEntities(newLocation, 2, 5, 2)) {
					if (e instanceof LivingEntity && e != player) {
						if (!Utils.canAttack(e)) continue;
						new BukkitRunnable() {
							@Override
							public void run() {
//								Utils.damage((LivingEntity) e, player, damage, 20, MainSky2RPGCore.getMain());
								Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 5);
								e.setVelocity(d.multiply(1.5).setY(0.8));
							}
						}.runTask(SantoryCore.get());
					}
				}
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 2);
	}
	
	private List<Location> getListLocation(Location main) {
		List<Location> list = new ArrayList<Location> ();
		Location newLocation = main.clone().add(0, -2, 0);
		for (int i = 1 ; i < 15; i ++) {
			list.add(newLocation.clone().add(0, 0.5f * i, 0));
		}
		return list;
	}

}
