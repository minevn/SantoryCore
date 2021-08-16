package mk.plugin.santory.skills.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
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

public class WSTanXaTien implements  SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		int amount = Double.valueOf((double) components.get("scale")).intValue();
		
		double angleBetweenArrows = (45 / (amount - 1)) * Math.PI / 180;
		double pitch = (player.getLocation().getPitch() + 90) * Math.PI / 180;
		double yaw = (player.getLocation().getYaw() + 90 - 45 / 2) * Math.PI / 180;
		double sZ = Math.cos(pitch);

		List<Arrow> as = new ArrayList<Arrow> ();
		
		for (int i = 0; i < amount; i++) { 	
			double nX = Math.sin(pitch)	* Math.cos(yaw + angleBetweenArrows * i);
			double nY = Math.sin(pitch)* Math.sin(yaw + angleBetweenArrows * i);
			Vector newDir = new Vector(nX, sZ, nY);

			Arrow arrow = player.launchProjectile(Arrow.class);
			arrow.setShooter(player);
			arrow.setVelocity(newDir.normalize().multiply(3f));
			arrow.setCritical(true);
			arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

			as.add(arrow);

			Damages.setProjectileDamage(arrow, new Damage(Travelers.getStatValue(player, Stat.DAMAGE), DamageType.SKILL));
		}
		player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Arrow a : as) {
					a.remove();
				}
			}
		}.runTaskLater(SantoryCore.get(), 10);
	}

}
