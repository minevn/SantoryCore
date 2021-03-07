package mk.plugin.santory.task;

import mk.plugin.santory.slave.Slaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class HealTask extends BukkitRunnable {
	
	@Override
	public void run() {
		// Players
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (player.isDead()) return;
			double value = Travelers.getStatValue(player, Stat.HEAL);
			Utils.addHealth(player, value);
		});

		// Slaves
		for (String id : Slaves.getSlaves()) {
			if (Slaves.isDead(id)) continue;
			LivingEntity le = Slaves.getSlaveEntity(id);
			if (le == null) continue;
			Utils.addHealth(le, Slaves.getHeal(id));
		}
	}
	
}
