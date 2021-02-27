package mk.plugin.santory.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class HealTask extends BukkitRunnable {
	
	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (player.isDead()) return;
			double value = Travelers.getStatValue(player, Stat.HEAL);
			Utils.addHealth(player, value);
		});
	}
	
}
