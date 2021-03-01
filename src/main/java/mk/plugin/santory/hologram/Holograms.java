package mk.plugin.santory.hologram;

import mk.plugin.santory.utils.HoloUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class Holograms {
	
	public static void hologram(Plugin plugin, String message, int tick, Player player, LivingEntity target, double radius) {
		hologram(plugin, randomLocation(target.getEyeLocation().add(0, 0.5, 0), radius), message, tick, player);
	}
	
	public static void hologram(Plugin plugin, Location location, String message, int tick, Player player) {
		HoloUtils.hologram(message, location, tick);
	}
	
	public static void hologram(Plugin plugin, Location location, List<String> message, int tick, Player player) {
		HoloUtils.hologram(message, location, tick);
	}
	
	public static Location randomLocation(Location loc, double max) {
		Vector direct1 = loc.getDirection().clone().setY(0);
		Vector direct2 = direct1.clone().setX(direct1.getZ()).setZ(direct1.getX() * -1f);
		
		double ranY = (new Random().nextInt(new Double(max * 1000).intValue()) - max * 500) / 1000;
		double ranM = (new Random().nextInt(new Double((max * 1000)).intValue()) - max / 2 * 1000)  / 1000;
		Location result = loc.clone();
		result.setY(ranY + loc.getY());
		result.add(direct2.multiply(ranM));
		
		return result;
	}

	public static void hologram(Plugin plugin, List<String> messages, int tick, Player player, LivingEntity target, int radius) {
		hologram(plugin, randomLocation(target.getEyeLocation(), radius), messages, tick, player);
	}
	
}
