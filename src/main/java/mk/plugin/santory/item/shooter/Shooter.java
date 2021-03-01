package mk.plugin.santory.item.shooter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;

public enum Shooter {
	
	BOW {
		@Override
		public Arrow shoot(Player player, Damage damage, Vector v, Location location) {
			Arrow arrow = player.getWorld().spawnArrow(location, v, 0, 0);
			arrow.setVelocity(v);
			Damages.setProjectileDamage(arrow, damage);
			player.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1, 1);
			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				arrow.remove();
			}, 10);
			return arrow;
		}
	};
	
	Shooter() {}
	
	public abstract Arrow shoot(Player player, Damage damage, Vector v, Location location);
	
}
