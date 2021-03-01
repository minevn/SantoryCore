package mk.plugin.santory.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {

	private final GUI gui;
	
	public GUIHolder(GUI gui) {
		this.gui = gui;
	}
	
	public GUI getGUI() {
		return this.gui;
	}
	
	@Override
	public Inventory getInventory() {
		return null;
	}

}
