package mk.plugin.santory.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class GUIs {
	
	private static final Map<GUI, GUIStatus> statuses = Maps.newHashMap();
	
	public static GUIStatus getStatus(GUI gui) {
		return statuses.getOrDefault(gui, null);
	}
	
	public static void open(Player player, GUI gui) {
		Inventory inv = Bukkit.createInventory(new GUIHolder(gui), gui.getSize(), gui.getTitle());
		GUIStatus status = new GUIStatus(inv, gui);
		statuses.put(gui, status);
		player.openInventory(inv);
		player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
		
		Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
			for (int i = 0 ; i < gui.getSize() ; i++) inv.setItem(i, GUIs.getItemSlot(DyeColor.BLACK, "§l"));
			gui.getSlots().forEach((sl, gs) -> {
				inv.setItem(sl, gs.getIcon());
			});
		});
	}
	
	public static int countPlaced(String id, GUIStatus status) {
		int c = 0;
		GUI gui = status.getGUI();
		for (int sl : status.getPlacedItems().keySet()) {
			if (gui.getSlots().get(sl).getID().equals(id)) c++; 
		}
		return c;
	}
	
	public static int countAmountPlaced(String id, GUIStatus status) {
		int c = 0;
		GUI gui = status.getGUI();
		for (int sl : status.getPlacedItems().keySet()) {
			if (gui.getSlots().get(sl).getID().equals(id)) c += status.getPlacedItems().get(sl).getAmount(); 
		}
		return c;
	}
	
	public static void clearItems(String id, GUIStatus status) {
		GUI gui = status.getGUI();
		for (int sl : status.getPlacedItems().keySet()) {
			status.getInventory().setItem(sl, gui.getSlots().get(sl).getIcon());
		}
		status.setPlacedItems(Maps.newHashMap());
	}
	
	public static int getEmptySlot(String id, GUIStatus status) {
		GUI gui = status.getGUI();
		for (int sl : gui.getSlots().keySet()) {
			if (!gui.getSlots().get(sl).getID().equals(id)) continue;
			if (status.getPlacedItems().containsKey(sl)) continue;
			return sl;
		}
		return -1;
	}
	
	public static ItemStack getItem(String id, GUIStatus status) {
		return getItems(id, status).size() == 0 ? null : getItems(id, status).get(0);
	}
	
	public static List<ItemStack> getItems(String id, GUIStatus status) {
		List<ItemStack> l = Lists.newArrayList();
		GUI gui = status.getGUI();
		for (int sl : status.getPlacedItems().keySet()) {
			if (gui.getSlots().get(sl).getID().equals(id)) l.add(status.getPlacedItems().get(sl));
		}
		return l;
	}
	
	public static void beforeClose(Player player, GUIStatus status) {
		for (ItemStack is : status.getPlacedItems().values()) {
			player.getInventory().addItem(is);
		}
		statuses.remove(status.getGUI());
	}
	
	public static ItemStack getItemSlot(DyeColor color, String desc) {
		ItemStack item = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, Utils.getColor(color));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(desc);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}
	
}
