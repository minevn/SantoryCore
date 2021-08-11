package mk.plugin.santory.listener;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.TravelerOptions;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import mk.plugin.santory.wish.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import remvn.reanticheatspigot.event.PlayerCheckedEvent;

import java.util.Map;

public class PlayerListener implements Listener {

	/*
	Chat
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String format = Travelers.getFormatChatWithName(player);
		e.setFormat(format + e.getMessage().replace("%", ""));
	}

	/*
	Xac Minh
	 */
	@EventHandler
	public void onXM(PlayerCheckedEvent e) {
		var player = e.getPlayer();
		player.setMetadata("santory.xacminh", new FixedMetadataValue(SantoryCore.get(), ""));
		for (String cmd : Configs.getXmSuccess()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

	/*
	Keep Stone
	 */
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setKeepInventory(true);
		e.setKeepLevel(true);
		e.getDrops().clear();
		e.setDroppedExp(0);

		Player player = e.getEntity();
		if (player.hasPermission("dabaoho.bypass") || Configs.isPvPWorld(player.getWorld())) {
			player.sendMessage("§a§o>> Bạn được giữ kinh nghiệm (Exp) khi chết tại đây (PvP)");
			return;
		}
		ItemStack[] contents = player.getInventory().getContents();
		boolean keep = false;
		for (int i = 0 ; i < contents.length ; i++) {
			ItemStack item = contents[i];
			if (item != null) {
				if (Configs.isKeepStone(item)) {
					keep = true;
					if (item.getAmount() == 1) contents[i] = null;
					else item.setAmount(item.getAmount() - 1);
					player.getInventory().setContents(contents);

					player.sendMessage("§a§o>> Giữ kinh nghiệm (Exp) khi chết, tiêu thụ 1 Đá bảo hộ");

					break;
				}
			}
		}
		if (!keep) {
			long lvTotalExp = TravelerOptions.getExpOf(player.getLevel() + 1);
			long expLost = lvTotalExp * Configs.DIE_EXP_LOST_PERCENT / 100;
			var t = Travelers.get(player);
			t.getData().setExp(Math.max(0, t.getData().getExp() - expLost));
			player.sendMessage("§c§l§o>> Không có Đá bảo hộ, chết mất " + expLost + " Exp");
		}
	}


	/*
	Crate hit
	 */
	@EventHandler
	public void onCrateInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		if (b == null) return;
		if (e.getHand() != EquipmentSlot.HAND) return;

		var player = e.getPlayer();
		for (Map.Entry<String, Wish> entry : Configs.getWishes().entrySet()) {
			for (var ld : entry.getValue().getLocations()) {
				Block bld = ld.toBukkitLocation().getBlock();
				if (bld.getWorld() == b.getWorld() && bld.getX() == b.getX() && bld.getY() == b.getY() && bld.getZ() == b.getZ()) {
					e.setCancelled(true);
					String id = entry.getKey();

					if (!Configs.checkPermission(player, "wish")) return;

					// Left click
					if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
						WishGUI.open(player, id);
						return;
					}

					// Right crate, key
					var is = player.getInventory().getItemInMainHand();
					var keyID = Wishes.keyFrom(is);
					if (keyID == null) {
						player.sendMessage("§cPhải cầm chìa để mở hòm!");
						return;
					}

					// Match create + key
					var wk = Configs.getWishKey(keyID);
					if (wk.getWishes().contains(id)) {
						// x1
						if (!player.isSneaking()) {
							is.setAmount(is.getAmount() - 1);
							player.updateInventory();
							WishRolls.roll(entry.getValue(), player);
						}
						// x10
						else {
							if (is.getAmount() < 10) {
								player.sendMessage("§cCầm ít nhất x10 Chìa để có thể quay nhanh 10 lần");
								return;
							}
							else {
								is.setAmount(is.getAmount() - 10);
								player.updateInventory();
								WishRolls10.roll(entry.getValue(), player);
							}
						}
					}
					else {
						player.sendMessage("§cChìa không khớp với hòm!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Travelers.saveAndClearCache(e.getPlayer().getName());
		Masters.saveAndClearCache(e.getPlayer());
		SantoryCore.get().getTargetTask().removePlayer(e.getPlayer());
	}

	
}
