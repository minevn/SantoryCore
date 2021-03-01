package mk.plugin.santory.wish;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import mk.plugin.santory.tier.Tier;

public class WishData {

	private final String wish;
	private final int times;
	private final Map<Tier, Integer> insures;
	
	public WishData(String wish) {
		this.wish = wish;
		this.times = 0;
		this.insures = Maps.newHashMap();
		for (Tier t : Tier.values()) insures.put(t, 0);
	}
	
	public WishData(String wish, int times, Map<Tier, Integer> insures) {
		this.wish = wish;
		this.times = times;
		this.insures = insures;
	}
	
	public String getWish() {
		return this.wish;
	}
	
	public int getTimes() {
		return this.times;
	}
	
	public Map<Tier, Integer> getInsures() {
		return this.insures;
	}
	
	public void setInsure(Tier tier, int time) {
		this.insures.put(tier, time);
	}
	
	public String toString() {
		String s = this.wish + ";" + this.times + ";";
		for (Entry<Tier, Integer> e : insures.entrySet()) {
			s += e.getKey() + "-" + e.getValue() + ":";
		}
		s = s.substring(0, s.length() - 1);
 		return s;
	}
	
	public static WishData parse(String s) {
		String w = s.split(";")[0];
		int t = Integer.valueOf(s.split(";")[1]);
		Map<Tier, Integer> m = Maps.newHashMap();
		for (String l : s.split(";")[2].split(":")) {
			m.put(Tier.valueOf(l.split("-")[0]), Integer.valueOf(l.split("-")[1]));
		}
		return new WishData(w, t, m);
	}
	
}
