package mk.plugin.santory.task;

import mk.plugin.santory.hologram.Holograms;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealTask extends BukkitRunnable {

	private final int REGEN_FOOD_LEVEL = 12;
	
	@Override
	public void run() {
		// Players
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isDead()) return;
			double value = Travelers.getStatValue(player, Stat.HEAL);
			if (player.getFoodLevel() < REGEN_FOOD_LEVEL) return;

			player.setHealthScale(20);
			int regen = Double.valueOf(Utils.addHealth(player, value)).intValue();

			if (regen != 0) {
				player.setFoodLevel(player.getFoodLevel() - 1);
				String s = "§a+" + regen;
				Location l = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
				Holograms.hologram(SantoryCore.get(), s, 20, player, l, 2, true);
			}
		}

		// Slaves
		for (String id : Slaves.getSlaves()) {
			if (Slaves.isDead(id)) continue;
			LivingEntity le = Slaves.getSlaveEntity(id);
			if (le == null) continue;
			Utils.addHealth(le, Slaves.getHeal(id));
		}
	}
	
}
