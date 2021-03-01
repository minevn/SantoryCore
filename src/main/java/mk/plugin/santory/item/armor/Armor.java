package mk.plugin.santory.item.armor;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.utils.Utils;

public class Armor {
	
	private final Material chestplate;
	private final Color color;
	
	public Armor(Material chestplate, Color color) {
		this.chestplate = chestplate;
		this.color = color;
	}
	
	public Material getChestplate() {
		return this.chestplate;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public ItemStack buildChestplate() {
		ItemStack is = new ItemStack(chestplate);
		LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
		meta.setDisplayName("§a§lTrang bị phụ");
		meta.setColor(this.color);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		is.setItemMeta(meta);
		return is;
	}
	
	public static Armor parse(ItemModel model) {
		String ms = model.getMetadata().get("chest-texture").split(" ")[0] + "_CHESTPLATE";
		String cs = model.getMetadata().get("chest-texture").split(" ")[1];
		return new Armor(Material.valueOf(ms), Utils.readColor(cs));
	}
	
}
