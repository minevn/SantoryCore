package mk.plugin.santory.artifact;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.StatValue;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class Artifacts {
	
	public static boolean is(ItemStack is) {
		return Items.is(is) && Items.read(is).getModel().getType() == ItemType.ARTIFACT;
	}
	
	public static void check(Item item, Artifact art) {
		ItemData data = item.getData();
		int amount = data.getGrade().getValue() + 1;
		List<StatValue> stats = data.getStats();
		if (stats.size() >= amount) return;
	
		while (stats.size() < amount) {
			// Do main stat
			if (stats.size() == 0) {
				int base = Double.valueOf(Configs.ART_BASE_MAIN_STAT * Configs.getArtTierUp().get(item.getModel().getTier())).intValue();
				int value = Double.valueOf(Utils.random(base * (1 - Configs.ART_STAT_RANGE), base * (1 + Configs.ART_STAT_RANGE))).intValue();
				Stat stat = rate(art.getMainStats());
				stats.add(new StatValue(stat, value));
			}
			else {
				// Do sub stats
				int base = Double.valueOf(Configs.ART_BASE_SUB_STAT * Configs.getArtTierUp().get(item.getModel().getTier())).intValue();
				int value = Double.valueOf(Utils.random(base * (1 - Configs.ART_STAT_RANGE), base * (1 + Configs.ART_STAT_RANGE))).intValue();
				Stat stat = rate(art.getSubStats());
				stats.add(new StatValue(stat, value));
			}
		}
		
		data.setStats(stats);
	}
	
	private static Stat rate(Map<Stat, Double> m) {
		double s = 0;
		Map<Stat, Double> check = Maps.newHashMap();
		for (Stat stat : m.keySet()) {
			s += m.get(stat);
			check.put(stat, s);
		}
		
		double random = Utils.random(1, s);
		
		Stat last = null;
		for (Stat stat : check.keySet()) {
			if (check.get(stat) >= random) return stat;
			last = stat;
		}
		
		return last;
	}
	
	public static Map<Stat, Double> getBuff(Player player) {
		Traveler t = Travelers.get(player);
		Map<String, Integer> m = Maps.newHashMap();
		
		Map<String, Stat> ditmemay = Maps.newHashMap();
		Map<Stat, Double> stats = Maps.newHashMap();
		Map<String, List<String>> checked = Maps.newHashMap();
		
		t.getData().getArtifacts().forEach(item -> {
			Artifact a = Artifact.parse(item.getModel());
			ditmemay.put(a.getSetID(), a.getSetStat());
			
			if (checked.containsKey(a.getSetID())) {
				if (checked.get(a.getSetID()).contains(item.getModelID())) return;
			}
			
			List<String> concac = checked.getOrDefault(a.getSetID(), Lists.newArrayList());
			concac.add(item.getModelID());
			checked.put(a.getSetID(), concac);
			
			
			m.put(a.getSetID(), m.getOrDefault(a.getSetID(), 0) + 1);
		});
		
		m.forEach((id, amount) -> {
			double max = 0;
			for (int i = 0 ; i <= amount ; i++) {
				double buff = Configs.getArtSetUp().getOrDefault(i, 0d);
				max = Math.max(max, buff);
			}
			stats.put(ditmemay.get(id), max);
		});
		
		return stats;
	}
	
	public static void setDesc(Item item) {
		Artifact art = Artifact.parse(item.getModel());
		Stat stat = art.getSetStat();
		String desc = "Trang bị %1 và %2 bộ khác nhau tăng lần lượt %r1% và %r2% chỉ số " + stat.getName();
		
		int c = 0;
		for (Entry<Integer, Double> up : Configs.getArtSetUp().entrySet()) {
			c++;
			desc = desc.replace("%" + c, up.getKey() + "");
			desc = desc.replace("%r" + c, Double.valueOf(up.getValue() * 100).intValue() + "");
		}
		
		ItemData data = item.getData();
		data.setDesc(desc);
	}
	
	
	
	
}
