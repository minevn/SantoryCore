package mk.plugin.santory.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {

	private final GUI gui;
	private GUIStatus status;

	public GUIHolder(GUI gui) {
		this.gui = gui;
		this.status = status;
	}
	
	public GUI getGUI() {
		return this.gui;
	}

	public GUIStatus getStatus() {
		return status;
	}

	public void setStatus(GUIStatus status) {
		this.status = status;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

}
