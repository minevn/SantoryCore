package mk.plugin.santory.skills;

import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.item.shooter.Shooter;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class WSMuaTen implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		Location l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(5)));
		int times = 5;
		
		// Arrow
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				if (i >= times * 2) this.cancel();
				for (int i = 0 ; i < 8 ; i++) {
					Location main = l.clone().add(0, 10, 0);
					double y = main.getY() + (double) (new Random().nextInt(10000) - 5000) / 1000;
					double x = main.getX() + (double) (new Random().nextInt(10000) - 5000) / 1500;
					double z = main.getZ() + (double) (new Random().nextInt(10000) - 5000) / 1500;
					main.setX(x);
					main.setZ(z);
					main.setY(y);
					
					Arrow a = Shooter.BOW.shoot(player, new Damage(damage, DamageType.SKILL), new Vector(0, -1, 0), main);
					a.setCritical(true);
					a.setMetadata("arrow.MuaTen", new FixedMetadataValue(SantoryCore.get(), damage * 0.5));	
				}
			}
		}.runTaskTimer(SantoryCore.get(), 0, 7);
	}


}
