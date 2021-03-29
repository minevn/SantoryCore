package mk.plugin.santory.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.Artifacts;
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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Items {
	
	public static final int ENHANCE_BONUS = 1;
	public static final float ASCENT_BONUS = 0.25f;
	
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
		ItemModel model = item.getModel();
		if (model.getType() == ItemType.ARTIFACT) Artifacts.setDesc(item);
		String lvf = "§7§l[§f§l" + model.getTier().getColor() + "§l+" + data.getLevel() + "§7§l]";
		String namef = model.getTier().getColor() + "§l" + model.getName();
		String gradef = "§e§l" + Utils.toStars(data.getGrade()) + " §f| §a§l" + Utils.toStars(data.getAscent());
		String durf = "§aĐộ bền: §f" + data.getDurability() + "/" + Configs.MAX_DURABILITY;
		String element = model.getElement().getColor() + "Nguyên tố: " + model.getElement().getName();
		List<String> descf = Lists.newArrayList();
		if (model.getDesc() != null) {
			String desc = gradeCheckDesc(data.getDesc() == null ? model.getDesc() : data.getDesc(), data.getGrade());
			descf = Utils.toList(desc, 25, "§f§o");
		}
		List<String> statf = Lists.newArrayList();
		
		int c = 0;
		for (StatValue sv : data.getStats()) {
			Stat stat = sv.getStat();
			int value = sv.getValue();
			String prefix = c == 0 ? "§6§l" : "§6";
			statf.add(prefix + stat.getName() + ": §f" + value + " §7(+" + (Utils.getStatOfItem(item, stat) - value) + ")");
			c++;
		}

		model.getTexture().set(is);
		
		ItemStackUtils.setDisplayName(is, lvf + " " + namef);
		List<String> lore = Lists.newArrayList();
		lore.add(gradef);
		lore.addAll(descf);
		lore.add("");
		lore.addAll(statf);
		lore.add("");
		lore.add(element);
		ItemStackUtils.setLore(is, lore);
		
		ItemMeta meta = is.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		is.setItemMeta(meta);
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
	
	private static String gradeCheckDesc(String desc, Grade grade) {
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
			if (i + 1 != grade.getValue()) news += "§7§o" + values.get(i) + "/";
			else news += "§f§o§l" + values.get(i) + "§7§o/";
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

}
