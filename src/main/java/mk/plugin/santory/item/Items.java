package mk.plugin.santory.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Items {

	private static final String DATA_TAG = "santory.itemdata";
	private static final String MODEL_TAG = "santory.itemmodel";
	
	public static boolean is(ItemStack is) {
		if (is == null) return false;
		return ItemStackUtils.hasTag(is, DATA_TAG);
	}	

	public static boolean isType(ItemStack is, ItemType type) {
		if (!is(is)) return false;
		return Items.read(is).getModel().getType() == type;
	}

	public static Item read(ItemStack is) {
		if (!is(is)) return null;
		Map<String, String> tags = ItemStackUtils.getTags(is);
		ItemData data = ItemData.parse(tags.get(DATA_TAG));
		String model = tags.get(MODEL_TAG);
		
		return new Item(model, data);
	}
	
	// Update type, damage, name, lore
	public static void update(Player player, ItemStack is, Item item) {
		ItemData data = item.getData();

		// Timed trigger
		if (!player.hasPermission("timed.bypass")) {
			if (data.timedTrigger()) {
				write(player, is, item);
			}
		}

		// ...
		ItemModel model = item.getModel();
		if (model.getType() == ItemType.ARTIFACT) Artifacts.setDesc(item);
		String lvf = "§7§l[§f§l" + model.getTier().getColor() + "§l+" + data.getLevel() + "§7§l]";
		String namef = model.getTier().getColor() + "§l" + model.getName();
		String gradef = "§eĐ.phá: " + Utils.toStars(data.getAscent()) + " | §eL.chiến: §f" + calPower(item);
		String durf = "§aĐộ bền: §f" + data.getDurability() + "/" + Configs.MAX_DURABILITY;
		String element = model.getElement().getColor() + "Nguyên tố: " + model.getElement().getName();
		List<String> descf = Lists.newArrayList();
		if (model.getDesc() != null) {
			String desc = ascentCheckDesc(data.getDesc() == null ? model.getDesc() : data.getDesc(), data.getAscent());
			descf = Utils.toList(desc, 25, "§f§o");
		}
		List<String> statf = Lists.newArrayList();
		
		int c = 0;
		Map<Stat, Integer> stats = Maps.newLinkedHashMap();
		for (StatValue sv : data.getStats()) {
			stats.put(sv.getStat(), stats.getOrDefault(sv.getStat(), 0) + sv.getValue());
		}
		for (Map.Entry<Stat, Integer> sv : stats.entrySet()) {
			Stat stat = sv.getKey();
			int value = sv.getValue();
			String prefix = c == 0 ? "§6§l" : "§6";

			var t = item.getModel().getTier();
			int percentUp = t.getEnhanceUp() * data.getLevel();

			int statUp = (Items.calStat(item, stat) - item.getData().getStat(stat));

			var enhanceUp = " §7(§f+" + statUp + "§7)" + t.getColor() + " (+" + percentUp + "%)";
			statf.add(prefix + stat.getName() + ": §f" + value + enhanceUp);

			c++;
		}

		var u = "§aTrang bị bậc " + item.getData().getGrade().toString();
		var u2 = "§aThêm " + calNeedExpToNextGrade(data.getExp(), data.getGrade()) + " điểm n.tố để lên bậc";

		// Timed
		var isTimed = data.isTimed();
		var expireTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(data.getExpiredTime()), ZoneId.systemDefault());
		var timed = !data.isTriggered() ? "§6Trang bị có hạn §c" + (data.getTimed() / 86400000) + " ngày" : "§6Trang bị hết hạn vào §c" + Utils.twoNumbers(expireTime.getDayOfMonth()) + "/" + Utils.twoNumbers(expireTime.getMonth().getValue()) + "/" + expireTime.getYear();

		model.getTexture().set(is);
		
		ItemStackUtils.setDisplayName(is, lvf + " " + namef);
		List<String> lore = Lists.newArrayList();
		lore.add(gradef);
		if (descf.size() != 0) {
			lore.add("");
			lore.addAll(descf);
		}
		lore.add("");
		lore.addAll(statf);

		// Artifact
		if (model.getType() == ItemType.ARTIFACT) {
			lore.add("");
			var stat = Artifact.parse(model).getSetStat();
			for (Map.Entry<Integer, Double> e : Configs.getArtSetUp().entrySet()) {
				lore.add("§cBộ " + e.getKey() + " di vật: §f+" + Double.valueOf(e.getValue() * 100).intValue() + "% " + stat.getName());
			}
		}

		lore.add("");
		lore.add(u);
		lore.add(u2);
		lore.add("");
		lore.add(element);
		if (isTimed) {
			lore.add("");
			lore.add(timed);
		}
		ItemStackUtils.setLore(is, lore);
		
		ItemMeta meta = is.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		is.setItemMeta(meta);
	}

	private static int calNeedExpToNextGrade(int exp, Grade grade) {
		if (grade == Grade.V) return 0;
		int to = grade.getValue() + 1;
		for (Grade g : Grade.values()) {
			if (g.getValue() == to) return Configs.getExpRequires().get(g) - exp;
		}
		return 0;
	}

	// Write data into item
	public static void write(Player player, ItemStack is, Item item) {
		Map<String, String> tags = Maps.newHashMap();
		tags.put(DATA_TAG, item.getData().toString());
		tags.put(MODEL_TAG, item.getModelID());
		ItemStackUtils.setTag(is, tags);
	}
	
	public static ItemStack build(Player player, Item item) {
		if (item.getModel().getType() == ItemType.ARTIFACT) {
			Artifact art = Artifact.parse(item.getModel());
			Artifacts.check(item, art);
		}
		ItemStack is = new ItemStack(Material.STONE);
		update(player, is, item);
		write(player, is, item);
		
		return is;
	}
	
	
	public static ItemStack build(Player player, String model) {
		ItemData data = new ItemData(Configs.getModel(model));
		Item item = new Item(model, data);
		if (item.getModel().getType() == ItemType.ARTIFACT) {
			Artifact art = Artifact.parse(item.getModel());
			Artifacts.check(item, art);
		}
		ItemStack is = new ItemStack(Material.STONE);
		update(player, is, item);
		write(player, is, item);
		
		return is;
	}
	
	private static String ascentCheckDesc(String desc, Ascent ascent) {
		List<String> values = Lists.newArrayList();
		String regex = "(?<value>\\d+)[](\\|)]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(desc);
		while (m.find()) {
			String dddd = m.group("value");
			values.add(dddd);
		}
		if (values.size() != 5) return desc;
		
		String ditme = null;
		regex = "\\[.+[^]]]";
		p = Pattern.compile(regex);
		m = p.matcher(desc);
		while (m.find()) {
			ditme = m.group();
		}

		String news = "";
		for (int i = 0 ; i < values.size() ; i++) {
			if (i + 1 != ascent.getValue()) news += "§7§o" + values.get(i) + "/";
			else news += "§e§o§l" + values.get(i) + "§7§o/";
		}
		news = news.substring(0, news.length() - 1);

		return desc.replace(ditme, news);
		
	}
	
	public static List<Integer> skillValues(String desc) {
		List<Integer> values = Lists.newArrayList();
		String regex = "(?<value>\\d+)[](\\|)]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(desc);
		while (m.find()) {
			values.add(Integer.valueOf(m.group("value")));
		}
		return values;
	}

	public static int calPower(Item item) {
		int p = 0;
		for (Stat stat : Stat.values()) {
			p += Utils.calPower(calStat(item, stat));
		}

		return p;
	}

	public static int calStat(Item item, Stat stat) {
		ItemData data = item.getData();

		// Base
		int base = data.getStat(stat);
		if (base == 0) return base;

		// Enhance
		int elv = data.getLevel();
		double enhanceD = base * elv * item.getModel().getTier().getEnhanceUp() / 100;
		if (enhanceD > 0 && enhanceD < 1) enhanceD = 1;
		int enhance = Long.valueOf(Math.round(enhanceD)).intValue();

		// Ascent
		int ascent = 0;
		if (item.getModel().getType() == ItemType.ARMOR) {
			if (item.getData().getStats().get(0).getStat() == stat) {
				int buff = getAscentValue(item);
				ascent = buff * base / 100;
			}
		}

		return base + enhance + ascent;
	}

	public static int getAscentValue(Item item) {
		List<Integer> l = Items.skillValues(item.getModel().getDesc());
		return l.get(item.getData().getAscent().getValue() - 1);
	}

}
