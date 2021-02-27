package mk.plugin.santory.gui;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

public class GUIStatus {
	
	private Inventory inv;
	private GUI gui;
	private Map<Integer, ItemStack> placedItems;
	private Map<String, Object> data;
	
	public GUIStatus(Inventory inv, GUI gui) {
		this.inv = inv;
		this.gui = gui;
		this.placedItems = Maps.newHashMap();
		this.data = Maps.newHashMap();
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	public GUI getGUI() {
		return this.gui;
	}
	
	public Map<Integer, ItemStack> getPlacedItems() {
		return this.placedItems;
	}
	
	public void setPlacedItems(Map<Integer, ItemStack> map) {
		this.placedItems = map;
	}
	
	public void place(Player player, int slot, ItemStack is) {
		this.inv.setItem(slot, is);
		this.placedItems.put(slot, is);
		if (gui.getSlots().containsKey(slot)) {
			GUISlot gs = gui.getSlots().get(slot);
			if (gs.getPlaceExecutor() != null) gs.getPlaceExecutor().execute(player, slot, this);
		}
	}
	
	public Map<String, Object> getData() {
		return this.data;
	}
	
	public void setData(String k, Object v) {
		this.data.put(k, v);
	}
	
	public void removeData(String k) {
		this.data.remove(k);
	}
	
	public Object getData(String k) {
		return this.data.get(k);
	}
	
	public boolean hasData(String k) {
		return this.data.containsKey(k);
	}
	
}
