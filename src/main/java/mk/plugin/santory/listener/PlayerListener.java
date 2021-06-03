package mk.plugin.santory.listener;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import mk.plugin.santory.wish.Wish;
import mk.plugin.santory.wish.WishRolls;
import mk.plugin.santory.wish.Wishes;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

		Player player = e.getEntity();
		if (player.hasPermission("dabaoho.bypass") || Configs.isPvPWorld(player.getWorld())) {
			e.setKeepInventory(true);
			e.setKeepLevel(true);
			e.getDrops().clear();
			e.setDroppedExp(0);
			player.sendMessage("§a§o>> Bạn được giữ đồ khi chết tại đây (PvP)");
			return;
		}
		ItemStack[] contents = player.getInventory().getContents();
		boolean keep = false;
		for (int i = 0 ; i < contents.length ; i++) {
			ItemStack item = contents[i];
			if (item != null) {
				if (Configs.isKeepStone(item)) {
					keep = true;
					player.sendMessage("§a§o>> Giữ đồ khi chết, tiêu thụ 1 Đá bảo hộ");
					e.setKeepInventory(true);
					e.setKeepLevel(true);
					e.getDrops().clear();
					e.setDroppedExp(0);
					if (item.getAmount() == 1) contents[i] = null;
					else item.setAmount(item.getAmount() - 1);
					player.getInventory().setContents(contents);
					break;
				}
			}
		}
		if (!keep) {
			player.sendMessage("§c§o>> Không có Đá bảo hộ, rơi đồ khi chết");
			e.setKeepInventory(false);
			e.setKeepLevel(true);
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
						is.setAmount(is.getAmount() - 1);
						player.updateInventory();
						WishRolls.roll(entry.getValue(), player);
					}
					else {
						player.sendMessage("§cChìa không khớp với hòm!");
					}
				}
			}
		}
	}

	// Tab tag
//	@EventHandler
//	public void onTabComplete(TabCompleteEvent e) {
//		String s = e.getBuffer();
//		if (s.contains("@")) {
//			String regex = "@(?<preName>\\S+)";
//			System.out.println("nani");
//			Pattern pt = Pattern.compile(regex);
//			Matcher m = pt.matcher(s);
//
//			String preName = null;
//			while (m.find()) {
//				preName = m.group("preName");
//			}
//			if (preName == null) return;
//
//			List<String> avl = Lists.newArrayList();
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				if (p.getName().toLowerCase().startsWith(preName.toLowerCase())) {
//					avl.add("@" + p.getName());
//				}
//			}
//
//			e.setCompletions(avl);
//		}
//	}


	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Travelers.saveAndClearCache(e.getPlayer().getName());
		Masters.saveAndClearCache(e.getPlayer());
		SantoryCore.get().getTargetTask().removePlayer(e.getPlayer());
	}
	
//	@EventHandler
//	public void onHitMuaTen(EntityDamageByEntityEvent e) {
//		if (e.getDamager() instanceof Arrow) {
//			Arrow a = (Arrow) e.getDamager();
//			if (a.hasMetadata("arrow.MuaTen")) {
//				Player player = (Player) a.getShooter();
//				a.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, a.getLocation(), 1, 0, 0, 0, 0);
//				a.getWorld().playSound(a.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);
//				double damage = a.getMetadata("arrow.MuaTen").get(0).asDouble();
//				Utils.getLivingEntities(player, a.getLocation(), 2, 2, 2).forEach(le -> {
//					if (!Utils.canAttack(le)) return;
//					Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
//				});
//			}
//		}
//	}
	
}
