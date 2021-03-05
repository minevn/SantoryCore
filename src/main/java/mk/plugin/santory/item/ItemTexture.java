package mk.plugin.santory.item;

import mk.plugin.santory.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
			is.setType(Material.PLAYER_HEAD);
			is.setItemMeta(Utils.buildSkull((SkullMeta) is.getItemMeta(), this.headTexture));
		}
		else {
			is.setType(material);
			ItemMeta meta = is.getItemMeta();
			meta.setCustomModelData(this.getData());
			if (this.color != null) {
				LeatherArmorMeta metaa = (LeatherArmorMeta) meta;
				metaa.setColor(this.color);
				is.setItemMeta(metaa);
			}
			is.setItemMeta(meta);
		}
	}
	
}
