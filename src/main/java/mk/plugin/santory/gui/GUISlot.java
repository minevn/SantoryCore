package mk.plugin.santory.gui;

import org.bukkit.inventory.ItemStack;

public class GUISlot {
	
	private final String id;
	private final ItemStack icon;
	private PlaceExecutor placeExecutor;
	private ClickExecutor clicker;
	
	public GUISlot(String id, ItemStack icon) {
		this.id = id;
		this.icon = icon;
	}
	
	public GUISlot(String id, ItemStack icon, PlaceExecutor placeExecutor) {
		this(id, icon);
		this.placeExecutor = placeExecutor;
	}
	
	public GUISlot(String id, ItemStack icon, ClickExecutor clicker) {
		this(id, icon);
		this.clicker = clicker;
	}
	
	public String getID() {
		return this.id;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public PlaceExecutor getPlaceExecutor() {
		return this.placeExecutor;
	}
	
	public ClickExecutor getClicker() {
		return this.clicker;
	}
	
	
}
