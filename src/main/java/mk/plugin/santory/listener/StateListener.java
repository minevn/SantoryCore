package mk.plugin.santory.listener;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.traveler.Travelers;

public class StateListener implements Listener {
	
	private Map<String, ItemStack> lastHand = Maps.newHashMap();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		lastHand.remove(e.getPlayer().getName());
	}
	
	@EventHandler
	public void onCloseInv(InventoryCloseEvent e) {
		if (e.getInventory().getType() != InventoryType.CRAFTING) return;
		Player player = (Player) e.getPlayer();
		Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
			Travelers.updateState(player);
			Travelers.updateLevel(player);
		});
	}
	
	@EventHandler
	public void onSwitchItem(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
			ItemStack hand = player.getInventory().getItemInMainHand();
			if (lastHand.containsKey(player.getName())) {
				ItemStack last = lastHand.get(player.getName());
				if (last.equals(hand)) return;
			}
			lastHand.put(player.getName(), hand);
			Travelers.updateState(player);
		});
	}
	
	@EventHandler
	public void onSwitchHand(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
			Travelers.updateState(player);
		});
	}
	
}
