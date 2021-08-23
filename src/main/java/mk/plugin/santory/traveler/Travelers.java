package mk.plugin.santory.traveler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.math.Stats;
import manaki.plugin.skybattleclient.gui.room.BattleType;
import manaki.plugin.skybattleclient.rank.RankData;
import manaki.plugin.skybattleclient.rank.player.RankedPlayers;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.shield.Shield;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.skin.system.PlayerSkins;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Travelers {
	
	private static final Map<String, Traveler> travelers = Maps.newHashMap();

	private static Set<String> hackChecked = Sets.newHashSet();

	public static void addHackChecked(String player) {
		hackChecked.add(player);
	}

	public static void removeHackChecked(String player) {
		hackChecked.remove(player);
	}

	public static boolean isHackChecked(String player) {
		return hackChecked.contains(player);
	}

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
			if (item.getData().isExpired()) return;
			for (Stat stat : Stat.values()) {
				int value = item.getData().getStat(stat);
				if (value == 0) continue;
				stats.put(stat, stats.getOrDefault(stat, 0) + Items.calStat(item, stat));
			}
		});
		
		// Artifacts
		Artifacts.getBuff(player).forEach((stat, value) -> {
			stats.put(stat, Double.valueOf(stats.getOrDefault(stat, 0) * (1 + value)).intValue());
		});

		// Skin
		int skinBuff = Skins.getBuff(player);
		if (skinBuff > 0) {
			for (Stat stat : Stat.values()) {
				stats.put(stat, Double.valueOf(stats.getOrDefault(stat, 0) * (1 + (double) skinBuff / 100)).intValue());
			}
		}

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
		int lv = i - 1;

		if (lv > t.getData().getGrade().getMaxLevel()) {
			player.setLevel(t.getData().getGrade().getMaxLevel());
			player.setExp(0.9999f);
			player.sendMessage("§c§oNâng bậc để có thể tăng giới hạn cấp độ!");
		}
		else {
			player.setLevel(lv);
			player.setExp(Float.valueOf((float) remain / TravelerOptions.getExpOf(i)).floatValue());
		}
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

		// Offhand shield
		var offhand = player.getInventory().getItemInOffHand();
		if (Items.is(offhand)) {
			var item = Items.read(offhand);
			if (item.getModel().getType() == ItemType.SHIELD) items.add(item);
		}

		// Chestplate
		var armor = player.getInventory().getChestplate();
		if (armor != null) {
			if (Items.is(armor)) {
				items.add(Items.read(armor));
			}
		}

		// Skins
		var skindata = PlayerSkins.get(player.getName());
		for (Item skin : skindata.getSkins()) {
			if (skin != null) items.add(skin);
		}
		
		return items;
	}

	public static String getFormatChatWithName(Player player) {
		String format = "%xacminh% §f(§r%rank%§f) &a[%level%/#%power%] %prefix% %name%%suffix%: &f";

		String xacminh;
		if (isHackChecked(player.getName())) xacminh = "§a✔";
		else xacminh = "§7✘";

		String prefix = Configs.getChatPrefixDefault();
		for (Map.Entry<String, String> e : Configs.getChatPrefixes().entrySet()) {
			if (player.hasPermission(e.getKey())) {
				prefix = e.getValue();
				break;
			}
		}

		String suffix = "";
		for (Map.Entry<String, String> e : Configs.getChatSuffixes().entrySet()) {
			if (player.hasPermission(e.getKey())) {
				suffix = e.getValue();
				break;
			}
		}

		String level = player.getLevel() + "";
		String power;
		long p = Utils.calPower(player);
		if (p > 1000000) power = p / 1000000 + "m";
		else if (p > 10000) power = p / 1000 + "k";
		else power = p + "";

		format = format.replace("%xacminh%", xacminh).replace("%level%", level).replace("%power%", power).replace("%prefix%", prefix).replace("%suffix%", suffix).replace("%name%", player.getName()).replace("&", "§");

		if (Bukkit.getPluginManager().isPluginEnabled("SkyBattleClient")) {
			format = format.replace("%rank%", manaki.plugin.skybattleclient.util.Utils.getRankDisplay(getSkybattleRank(player)));
		}
		format = format.replace("%rank%", "");

		return format;
	}

	public static RankData getSkybattleRank(Player player) {
		// Get max rank
		BattleType bt = null;
		int maxp = -1;
		var rp = RankedPlayers.get(player.getName());
		for (BattleType type : BattleType.values()) {
			var rd = rp.getRankData(type);
			var point = manaki.plugin.skybattleclient.util.Utils.toPoint(rd.getType(), rd.getGrade(), rd.getPoint());
			if (point > maxp) {
				maxp = point;
				bt = type;
			}
		}

		// return
		return rp.getRankData(bt);
	}
	
}
