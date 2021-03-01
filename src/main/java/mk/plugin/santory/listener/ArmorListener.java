package mk.plugin.santory.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.collect.Lists;

import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.armor.Armor;

public class ArmorListener implements Listener {
	
	@EventHandler
	public void onArmorClick(InventoryClickEvent e) {
		if (Lists.newArrayList(38, 37, 36).contains(e.getSlot())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		ItemStack is = e.getCursor();
		if (is == null || Items.is(is)) e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteractPlace(PlayerInteractEvent e) {
		if (!e.getAction().name().contains("RIGHT_CLICK")) return;
		Player player = e.getPlayer();
		
		ItemStack is = player.getInventory().getItemInMainHand();
		if (!Items.is(is)) return;
		Item item = Items.read(is);
		if (item.getModel().getType() != ItemType.ARMOR) return;
		PlayerInventory inv = player.getInventory();
		
		if (!onEquip(inv, item)) {
			e.setCancelled(true);
			return;
		}
		
		e.setCancelled(true);
		player.getInventory().setItemInMainHand(inv.getHelmet());
		inv.setHelmet(is);		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onShift(InventoryClickEvent e) {
		if (e.getInventory().getType() != InventoryType.CRAFTING) return;
		if (!e.getClick().name().contains("SHIFT")) return;
		
		ItemStack is = e.getCurrentItem();
		if (!Items.is(is)) return;
		Item item = Items.read(is);
		if (item.getModel().getType() != ItemType.ARMOR) return;
		PlayerInventory inv = e.getWhoClicked().getInventory();
		
		// Take
		if (e.getSlot() == 39 && e.isCancelled() == false) {
			inv.setChestplate(new ItemStack(Material.AIR));
			return;
		}
		
		if (!onEquip(inv, item)) {
			e.setCancelled(true);
			return;
		}
		
		e.setCancelled(true);
		e.setCurrentItem(inv.getHelmet());
		inv.setHelmet(is);
	}
	
	@EventHandler
	public void onClickPlace(InventoryClickEvent e) {
		if (e.getInventory().getType() != InventoryType.CRAFTING) return;
		if (e.getSlot() != 39) return;
		
		ItemStack is = e.getCursor();
		if (!Items.is(is)) return;
		Item item = Items.read(is);
		if (item.getModel().getType() != ItemType.ARMOR) return;
		PlayerInventory inv = e.getWhoClicked().getInventory();
		
		if (!onEquip(inv, item)) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onTake(InventoryClickEvent e) {
		if (e.getInventory().getType() != InventoryType.CRAFTING) return;
		PlayerInventory inv = e.getWhoClicked().getInventory();
		if (e.getSlot() != 39) return;
		
		ItemStack is = e.getCurrentItem();
		if (!Items.is(is)) return;
		onUnEquip(inv);
		
		is = e.getCursor();
		if (!Items.is(is)) return;
		Item item = Items.read(is);
		if (item.getModel().getType() != ItemType.ARMOR) return;
		
		if (!onEquip(inv, item)) {
			e.setCancelled(true);
			return;
		}
	}
	
	private boolean onEquip(PlayerInventory inv, Item item) {
		Armor a = Armor.parse(item.getModel());
		ItemStack chest = a.buildChestplate();
		
		inv.setChestplate(chest);
		return true;
	}
	
	private boolean onUnEquip(PlayerInventory inv) {
		inv.setChestplate(new ItemStack(Material.AIR));
		return true;
	}
	
}
