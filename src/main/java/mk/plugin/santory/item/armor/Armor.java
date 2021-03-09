package mk.plugin.santory.item.armor;

import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Color;

public class Armor {

	private final Color color;
	
	public Armor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}

	
	public static Armor parse(ItemModel model) {
		String cs = model.getMetadata().get("color");
		return new Armor(Utils.readColor(cs));
	}
	
}
