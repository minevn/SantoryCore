package mk.plugin.santory.amulet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import mk.plugin.santory.item.ItemTexture;
import mk.plugin.santory.utils.ItemStackUtils;

public enum Amulet {
	
	I("§9§lBùa may I", 25, new ItemTexture(Material.IRON_NUGGET, 3)),
	II("§c§lBùa may II", 50, new ItemTexture(Material.IRON_NUGGET, 4)),
	III("§e§lBùa may III", 100, new ItemTexture(Material.IRON_NUGGET, 5));
	
	private final String name;
	private final int bonus;
	private final ItemTexture texture;
	
	Amulet(String name, int bonus, ItemTexture texture) {
		this.name = name;
		this.bonus = bonus;
		this.texture = texture;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getBonus() {
		return this.bonus;
	}
	
	public ItemTexture getItemTexture() {
		return this.texture;
	}
	
	public ItemStack get() {
		ItemStack item = new ItemStack(this.getItemTexture().getMaterial());
		var meta = item.getItemMeta();
		meta.setCustomModelData(this.texture.getData());
		item.setItemMeta(meta);
		ItemStackUtils.setDisplayName(item, this.getName());
		ItemStackUtils.addLoreLine(item, "§7§oGiúp thực thi thất bại không bị");
		ItemStackUtils.addLoreLine(item, "§7§omất vật phẩm và tăng " + this.bonus + "% thành công");
		ItemStackUtils.setTag(item, "sRPG.sachmayman", this.name());
		
		return item;
	}
	
	public static boolean is(ItemStack item) {
		if (item == null) return false;
		if (!item.hasItemMeta()) return false;
		return ItemStackUtils.hasTag(item, "sRPG.sachmayman");
	}
	
	public static Amulet read(ItemStack item) {
		if (!is(item)) return null;
		return Amulet.valueOf(ItemStackUtils.getTag(item, "sRPG.sachmayman"));
	}
	
}
