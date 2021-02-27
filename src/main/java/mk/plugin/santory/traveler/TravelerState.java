package mk.plugin.santory.traveler;

import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import mk.plugin.santory.stat.Stat;

public class TravelerState {

	private Map<Stat, Integer> stats;
	
	public TravelerState() {
		this.stats = Maps.newHashMap();
	}
	
	public TravelerState(Map<Stat, Integer> stats) {
		this.stats = stats;
	}
	
	public int getStat(Player player, Stat stat) {
		int value = stats.getOrDefault(stat, 0);
		return value;
	}
	
	public Map<Stat, Integer> getStats() {
		return this.stats;
	}
	
	public void setStats(Map<Stat, Integer> stats) {
		this.stats = stats;
	}
	
}
