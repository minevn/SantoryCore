package mk.plugin.santory.traveler;

import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.stat.Stat;

import java.util.Map;

public class TravelerOptions {
	
	public static Map<Stat, Integer> getStatsAt(int level) {
		int h = 10 + 1 * level;
		int d = 5 + 1 * level;
		Map<Stat, Integer> stats = Maps.newHashMap();
		stats.put(Stat.HEALTH, h);
		stats.put(Stat.DAMAGE, d);
		
		return stats;
	}
	
	public static long getExpOf(int level) {
		if (level == 0) return 0;
		return Configs.LEVEL_BASE_EXP + (level - 1) * Configs.LEVEL_PLUS_EXP;
	}
	
	public static int getTotalExpTo(int level) {
		int total = 0;
		for (int i = 1 ; i <= level ; i++) {
			total += getExpOf(i);
		}
		return total;
	}
	
}
