package mk.plugin.santory.traveler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class Travelers {
	
	private static final Map<String, Traveler> travelers = Maps.newHashMap();
	
	public static Traveler get(String name) {
		if (!travelers.containsKey(name)) travelers.put(name, new Traveler(TravelerStorage.get(name)));
		return travelers.getOrDefault(name, null);
	}
	
	public static Traveler get(Player player) {
		return get(player.getName());
	}
	
	public static void save(String name) {
		TravelerStorage.save(name, get(name).getData());
	}
	
	public static void saveAndClearCache(String name) {
		TravelerStorage.save(name, get(name).getData());
		travelers.remove(name);
	}
	
	public static double getStatValue(Player player, Stat stat) {
		Traveler t = get(player.getName());
		TravelerState ts = t.getState();
		return stat.pointsToValue(ts.getStat(player, stat));
	}
	
	public static void updateState(Player player) {
		Map<Stat, Integer> stats = Maps.newHashMap();
		int level = player.getLevel();
		
		// Level
		stats.putAll(TravelerOptions.getStatsAt(level));
		
		// Items
		getItemsOn(player).forEach(item -> {
			for (Stat stat : Stat.values()) {
				int value = item.getData().getStat(stat);
				if (value == 0) continue;
				stats.put(stat, stats.getOrDefault(stat, 0) + Utils.getStatOfItem(item, stat));
			}
		});
		
		// Artifacts
		Artifacts.getBuff(player).forEach((stat, value) -> {
			stats.put(stat, Double.valueOf(stats.getOrDefault(stat, 0) * (1 + value)).intValue());
		});
//		Map<Stat, Double> arUp = Maps.newHashMap();
//		Artifacts.getBuff(player).forEach((stat, value) -> {
//			arUp.put(stat, stats.getOrDefault(stat, 0) + Math.max(value, arUp.getOrDefault(stat, 0d)));
//		});
//		arUp.forEach((stat, up) -> {
//			stats.put(stat, stats.getOrDefault(stat, 0) + Double.valueOf(stats.getOrDefault(stat, 0) * (1 + up)).intValue());
//		});
		
		// Write
		Traveler t = get(player);
		TravelerState st = t.getState();
		st.setStats(stats);
		
		// Set
		stats.forEach((stat, value) -> {
			stat.set(player, value);
		});
	}
	
	public static void updateLevel(Player player) {
		Traveler t = get(player);
		if (Configs.LEVEL_VALLINA_UPDATE) {
			long exp = TravelerOptions.getTotalExpTo(player.getLevel() - 1);
			exp += Double.valueOf(TravelerOptions.getExpOf(player.getLevel()) * player.getExp()).longValue();
			t.getData().setExp(exp);
			return;
		}
		int i = 1;
		while (TravelerOptions.getTotalExpTo(i) <= t.getData().getExp()) i++;
		long remain = t.getData().getExp() - TravelerOptions.getTotalExpTo(i - 1);
		player.setLevel(i - 1);
		player.setExp(Float.valueOf((float) remain / TravelerOptions.getExpOf(i)).floatValue());
	}
	
	public static List<Item> getItemsOn(Player player) {
		List<Item> items = Lists.newArrayList();
		Traveler t = get(player.getName());
		
		// Artifacts
		items.addAll(t.getData().getArtifacts());
		
		// Hand
		ItemStack hand = player.getInventory().getItemInMainHand();
		if (Items.is(hand)) {
			items.add(Items.read(hand));
		}
		
		// Armor
		for (ItemStack armor : player.getInventory().getArmorContents()) {
			if (Items.is(armor)) {
				items.add(Items.read(armor));
			}
		}
		
		return items;
	}
	
}
