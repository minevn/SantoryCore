package mk.plugin.santory.artifact;

import com.google.common.collect.Lists;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArtifactGUI implements InventoryHolder {
	
	private static final List<Integer> SLOTS = Lists.newArrayList(11, 12, 13, 14, 15);
	
	public static void open(Player player) {
		Traveler t = Travelers.get(player);
		Inventory inv = Bukkit.createInventory(new ArtifactGUI(), 27, "§0§lDI VẬT");
		player.openInventory(inv);
		player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
		
		Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
			for (int i = 0 ; i < 27 ; i++) inv.setItem(i, Utils.getBlackSlot());
			SLOTS.forEach(slot -> inv.setItem(slot, getIcon()));
			for (int i = 0 ; i < t.getData().getArtifacts().size() ; i++) {
				Item item = t.getData().getArtifacts().get(i);
				ItemStack is = Items.build(player, item);
				inv.setItem(SLOTS.get(i), is);
			}
		});
	}
	

	public static void eventClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof ArtifactGUI == false) return;	
		int slot = e.getSlot();
		
		if (e.getInventory() == e.getWhoClicked().getOpenInventory().getTopInventory()) {
			var player = (Player) e.getWhoClicked();
			player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
			e.setCancelled(true);
			ItemStack current = e.getCurrentItem();
			if (SLOTS.contains(slot) || Artifacts.is(current)) {
				ItemStack cursor = e.getCursor();
				if (Artifacts.is(cursor)) {
					if (isBlankSlot(current)) {
						e.setCursor(null);
					} else 	e.setCursor(current);
					e.setCurrentItem(cursor);
				}
				else {
					if (Artifacts.is(current)) {
						// Check slot
						e.setCursor(current);
						e.setCurrentItem(getIcon());
					}
				}
			}
		}
				
	}
	

	public static void eventClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof ArtifactGUI == false) return;
		
		List<Item> items = Lists.newArrayList();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		
		SLOTS.forEach(slot -> {
			ItemStack is = inv.getItem(slot);
			if (isBlankSlot(is)) return;
			items.add(Items.read(is));
		});
		
		Traveler t = Travelers.get(player);
		t.getData().setArtifacts(items);
		Travelers.save(player.getName());
		Travelers.updateState(player);
		
		player.sendMessage("§aDữ liệu về Di vật của bạn đã được lưu!");
		Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
			Artifacts.getBuff(player).forEach((stat, v) -> {
				if (v == 0) return;
				player.sendMessage("§aBạn được nhận bổ trợ §l" + v * 100 + "% " + stat.getName());
			});
		});
	}
	
	public static boolean isBlankSlot(ItemStack item) {
		if (item == null) return false;
		return !Items.is(item);
	}
	
	private static ItemStack getIcon() {
		ItemStack item = Icon.ARTIFACT.clone();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a§oÔ để Di vật");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

