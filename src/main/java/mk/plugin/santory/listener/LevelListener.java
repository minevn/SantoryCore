package mk.plugin.santory.listener;

import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.TravelerOptions;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.Map;

public class LevelListener implements Listener {
	
	@EventHandler
	public void onLevelChange(PlayerLevelChangeEvent e) {
		Player player = e.getPlayer();
		int lv = e.getNewLevel();
		Traveler trvl = Travelers.get(player);
		if (lv > trvl.getData().getGrade().getMaxLevel()) {
			int oldlv = e.getOldLevel();
			float oldExp = player.getExp();
			Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
				player.setLevel(oldlv);
				player.setExp(oldExp);
			});
		}
		
		Map<Stat, Integer> stats = TravelerOptions.getStatsAt(lv);
		String t = "§2§lTHĂNG CẤP " + lv;
		String subt = "§aMáu: §f" + stats.get(Stat.HEALTH) + " §7| §aSát thương: §f" + stats.get(Stat.DAMAGE);
		player.sendTitle(t, subt, 0, 40, 40);
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
	}
	
}
