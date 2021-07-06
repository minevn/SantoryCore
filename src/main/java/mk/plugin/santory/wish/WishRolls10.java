package mk.plugin.santory.wish;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.event.PlayerWishRollEvent;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.ItemStackManager;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WishRolls10 {
	
	private static final List<Integer> resultSlots = List.of(11, 12, 13, 14, 15, 20, 21, 22, 23, 24);
	
	private static final List<Integer> rollSlots = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 16, 25, 34, 33, 32, 31, 30, 29, 28, 19, 10);

	private static Set<String> rollings = Sets.newHashSet();

	public static void roll(Wish wish, Player player) {
		var t = Travelers.get(player.getName());
		List<WishRewardItem> rewards = Lists.newArrayList();

		Tier tier = Tier.RARE;
		for (int i = 0 ; i < 10 ; i++) {
			WishRewardItem wri = null;
			// Insure
			// Add 1 to all
			WishData wd = t.getData().getWish(wish.getID());
			wish.getInsures().keySet().forEach(ti -> wd.setInsure(ti, wd.getInsures().getOrDefault(ti, 0) + 1));
			// Check first time
			if (t.getData().getWish(wish.getID()).getTimes() == 0) {
				wri = wish.getFirstTime().get(Utils.randomInt(0, wish.getFirstTime().size() - 1));
				player.sendMessage("§aÁ đù mở hòm lần đầu");
			} else wri = Wishes.finalRate(wish, player);

			// Check tier
			if (wri.getTier().getNumber() > tier.getNumber()) tier = wri.getTier();

			// Add to list
			rewards.add(wri);

			// Save data
			t.getData().getWish(wish.getID()).addCount();
		}

		Travelers.save(player.getName());
		ItemStack ri = getIcon(tier);
		Inventory inv = Bukkit.createInventory(new WishGUIHolder(), 36, "§0§lQUAY ĐỀU, QUAY ĐỀU,...");
		player.openInventory(inv);
		rollings.add(player.getName());

		// History
		Tasks.async(() -> {
			for (WishRewardItem finalWri : rewards) {
				if (wish.getID().contains("weapon")) {
					boolean insure = player.hasMetadata("insure-weapon");
					if (insure) {
						player.removeMetadata("insure-weapon", SantoryCore.get());
					}
					SantoryCore.get().getWeaponWishHistory().write(player, finalWri.getTier(), finalWri.getValue(), insure);
				}
				else if (wish.getID().contains("armor")) {
					boolean insure = player.hasMetadata("insure-armor");
					if (insure) {
						player.removeMetadata("insure-armor", SantoryCore.get());
					}
					SantoryCore.get().getArmorWishHistory().write(player, finalWri.getTier(), finalWri.getValue(), insure);
				}
			}
		});

		// Roll
		Tier finalTier = tier;
		Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
			for (int i = 0 ; i < inv.getSize() ; i++) inv.setItem(i, Utils.getBlackSlot());
			for (int i = 0 ; i < 4 ; i++) inv.setItem(rollSlots.get(i * 4), Utils.getColoredSlot(DyeColor.LIME));

			// Roll
			long current = System.currentTimeMillis();
			long mili = Utils.randomInt(8000, 10000);
			new BukkitRunnable() {
				
				final long buff = 15;
				long interval = 10;
				long lastCheck = current;
				int c = 0;
				
				@Override
				public void run() {
					// Check roll
					if (System.currentTimeMillis() - lastCheck >= interval) {
						c++;
						
						// End
						if (System.currentTimeMillis() - current >= mili) {
							this.cancel();

							// Pre
							for (int i = 0; i < resultSlots.size(); i++) {
								inv.setItem(resultSlots.get(i), getIcon(rewards.get(i).getTier()));
							}
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);

							// Shows
							Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
								for (int i = 0 ; i < rewards.size() ; i++) {
									var wri = rewards.get(i);
									var slot = resultSlots.get(i);
									var icon = wri.getIcon();
									inv.setItem(slot, icon);

									// Give
									wri.give(player);
								}
								player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
								player.sendMessage("§aNhận quà thành công!");
							}, 20);
							rollings.remove(player.getName());

							// Event
							Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
								Bukkit.getPluginManager().callEvent(new PlayerWishRollEvent(player, wish.getID()));
							});

							// Broadcast
							if (finalTier == Tier.RARE || finalTier == Tier.EPIC) {
								for (Player p : Bukkit.getOnlinePlayers()) {
									p.sendMessage("§7§o>> Người chơi " + player.getName() + " mở được đồ " + finalTier.getName() + " tại " + wish.getName());
								}
							}

							return;
						}
						
						// Continue
						for (int i = 0 ; i < rollSlots.size() ; i++) {
							int slot = rollSlots.get(i);
							var isColorSlot = i % 3 == c % 3;

							// Change color
							if (System.currentTimeMillis() - current >= mili / 2) {
//								inv.setItem(inv.getSize() - 1, getInfo(wish.getID(), player));
								if (isColorSlot) inv.setItem(slot, ri);
								else inv.setItem(slot, Utils.getColoredSlot(DyeColor.BLACK));
							}
							else {
								if (isColorSlot) inv.setItem(slot, Utils.getColoredSlot(DyeColor.WHITE));
								else inv.setItem(slot, Utils.getColoredSlot(DyeColor.BLACK));
							}
						}
						
						lastCheck = System.currentTimeMillis();
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
						if (System.currentTimeMillis() - current < mili) interval += buff;
					}
				}
			}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
			
		});
	}
	
	private static ItemStack getIcon(Tier tier) {
		ItemStack is = Utils.getTieredIcon(tier);
		ItemStackUtils.setDisplayName(is, tier.getColor() + "§l" + tier.getName());
		return is;
	}

	private static ItemStack getInfo(String id, Player player) {
		var w = Configs.getWish(id);
		var t = Travelers.get(player.getName());
		var m = t.getData().getWish(id).getInsures();

		var is = new ItemStack(Material.BOOK);
		var im = new ItemStackManager(SantoryCore.get(), is);
		im.setName("§a§lBảo hiểm");
		List<String> lore = Lists.newArrayList();
		for (Map.Entry<Tier, Integer> e : m.entrySet()) {
			if (!w.getInsures().containsKey(e.getKey())) continue;
			lore.add(e.getKey().getColor() + e.getKey().getName() + ": §f" + e.getValue());
		}
		im.setLore(lore);

		return is;
	}

	public static void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof WishGUIHolder) e.setCancelled(true);
	}

	public static void onClose(InventoryCloseEvent e) {
		if (!rollings.contains(e.getPlayer().getName())) return;
		if (e.getInventory() != null && e.getInventory().getHolder() instanceof WishGUIHolder) {
			Tasks.sync(() -> {
				e.getPlayer().openInventory(e.getInventory());
			});
		}
	}
	
}

