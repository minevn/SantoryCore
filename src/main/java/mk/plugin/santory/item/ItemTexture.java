package mk.plugin.santory.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import mk.plugin.santory.utils.Utils;

public class ItemTexture {
	
	private Material material;
	private int data;
	private String headTexture;
	private Color color;
	
	public ItemTexture(Material material, int data) {
		this.material = material;
		this.data = data;
	}
	
	public ItemTexture(String headTexture) {
		this.headTexture = headTexture;
	}
	
	public ItemTexture(Material material, Color color) {
		this.material = material;
		this.color = color;
	}
	
	public ItemTexture(Material material, int data, String headTexture, Color color) {
		this.material = material;
		this.data = data;
		this.headTexture = headTexture;
		this.color = color;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public String getHeadTexture() {
		return this.headTexture;
	}
	
	public int getData() {
		return this.data;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void set(ItemStack is) {
		if (this.headTexture != null) {
			is.setType(Material.SKULL_ITEM);
			is.setDurability((short) 3);
			is.setItemMeta(Utils.buildSkull((SkullMeta) is.getItemMeta(), this.headTexture));
		}
		else {
			is.setType(material);
			is.setDurability(new Integer(this.data).shortValue());
			if (this.color != null) {
				LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
				meta.setColor(this.color);
				is.setItemMeta(meta);
			}
		}
	}
	
}
