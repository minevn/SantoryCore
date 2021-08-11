package mk.plugin.santory.listener;

import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.modifty.ModifyGUI;
import mk.plugin.santory.skin.gui.SkinGUI;
import mk.plugin.santory.skin.gui.SkinNPCGUI;
import mk.plugin.santory.slave.gui.SlaveInfoGUI;
import mk.plugin.santory.slave.gui.SlaveSelectGUI;
import mk.plugin.santory.traveler.TravelerInfoGUI;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.wish.WishGUI;
import mk.plugin.santory.wish.WishRolls;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.gui.GUI;
import mk.plugin.santory.gui.GUIHolder;
import mk.plugin.santory.gui.GUISlot;
import mk.plugin.santory.gui.GUIStatus;
import mk.plugin.santory.gui.GUIs;
import mk.plugin.santory.main.SantoryCore;

public class GUIListener implements Listener {
	
	/*
	 * Other GUIs
	 */
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) return;
		ArtifactGUI.eventClick(e);
		SlaveSelectGUI.onClick(e);
		SlaveInfoGUI.onClick(e);
		WishRolls.onClick(e);
		TravelerInfoGUI.onClick(e);
		ModifyGUI.onClick(e);
		SkinGUI.onClick(e);
		WishGUI.onClick(e);
		SkinNPCGUI.onClick(e);
	}
	
	@EventHandler
	public void onGUIClose(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		if (!check(player)) return;
		ArtifactGUI.eventClose(e);
		SlaveInfoGUI.onClose(e);
		WishRolls.onClose(e);
		SkinGUI.onClose(e);
	}

	@EventHandler
	public void onGUIDrag(InventoryDragEvent e) {
		SlaveSelectGUI.onDrag(e);
		TravelerInfoGUI.onDrag(e);
	}
	
	/*
	 * System GUI
	 */
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getInventory().getHolder() instanceof GUIHolder)) return;
		e.setCancelled(true);
		
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		var holder = (GUIHolder) e.getInventory().getHolder();
		GUI gui = holder.getGUI();
		GUIStatus status = holder.getStatus();
		if (status == null) {
			player.sendMessage("§cCó lỗi gì đó xảy ra, thử lại lần nữa xem");
			player.sendMessage("§cVẫn bị thì báo với admin/helper");
			return;
		}
		GUISlot gs = gui.getSlots().get(slot);
		
		// Place
		// Bot item
		if (e.getClickedInventory() == player.getOpenInventory().getBottomInventory()) {
			if (e.getClick().name().startsWith("SHIFT")) {
				if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) return;
				ItemStack ci = e.getCurrentItem(); // current item
				doPlace(ci, player, status, gui, gs, slot);
			}
			else {
				player.sendMessage("§cẤn §fShift + Chuột trái §cđể đặt item");

				ItemStack ci = e.getCurrentItem();
				var item = Items.read(ci);
				if (item != null) {
					if (item.getData().getLevel() == 0) {
						Tasks.sync(() -> {
							player.closeInventory();
							player.sendTitle("§c§lSHIFT + Chuột trái", "§fShift + Chuột trái để đặt item", 5, 40, 0);
							player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							Tasks.sync(() -> {
								GUIs.open(player, gui);
							}, 45);
						});
					}
				}
			}
		}
		
		// Detect button
		if (gs != null && gs.getClicker() != null && e.getClickedInventory() == player.getOpenInventory().getTopInventory()) {
			gs.getClicker().execute(player, status);
			player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
		}
	}
	
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (e.getInventory().getHolder() instanceof GUIHolder) e.setCancelled(true);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (!(e.getInventory().getHolder() instanceof GUIHolder)) return;
		Player player = (Player) e.getPlayer();

		var holder = (GUIHolder) e.getInventory().getHolder();
		GUIStatus status = holder.getStatus();

		if (status == null) {
			player.sendMessage("§cCó lỗi gì đó xảy ra, thử lại lần nữa xem");
			player.sendMessage("§cVẫn bị thì báo với admin/helper");
			return;
		}

		GUIs.beforeClose(player, status);
	}
	
	public void doPlace(ItemStack ci, Player player, GUIStatus status, GUI gui, GUISlot gs, int slot) {
		player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
		if (ci == null || ci.getType() == Material.AIR) return;
		ItemStack pi = ci.clone(); // placed item
		int amount = 1;
		if (gui.getAmounter().allowMulti(pi)) amount = pi.getAmount();
		pi.setAmount(amount);
		if (!gui.getPlacer().place(player, pi, status)) return;
		ci.setAmount(ci.getAmount() - amount);
		if (gs != null && gs.getPlaceExecutor() != null) gs.getPlaceExecutor().execute(player, slot, status);
		player.updateInventory();
	}
	
	private void addCheck(Player player) {
		player.setMetadata("checkGUI", new FixedMetadataValue(SantoryCore.get(), ""));
	}
	
	private void removeCheckGUI(Player player) {
		player.removeMetadata("checkGUI", SantoryCore.get());
	}
	
	public synchronized boolean check(Player player) {
		if (player.hasMetadata("checkGUI")) {
			removeCheckGUI(player);
			return false;
		} 
		addCheck(player);
		
		Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
			if (player.hasMetadata("checkGUI")) {
				removeCheckGUI(player);
			}
		}, 2);
		return true;
	}
	
}
