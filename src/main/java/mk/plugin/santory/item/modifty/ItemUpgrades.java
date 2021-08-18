package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.event.PlayerItemUpgradeEvent;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.modifty.upgrade.UpgradeStone;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemUpgrades {

	private static final int ITEM_SLOT = 1;
	private static final int BUTTON_SLOT = 8;
	private static final int MATERIAL_SLOT = 2;
	private static final int AMULET_SLOT = 3;
	private static final int RESULT_SLOT = 5;

	public static Map<Integer, GUISlot> getSlots() {
		Map<Integer, GUISlot> slots = Maps.newHashMap();
		slots.put(MATERIAL_SLOT, new GUISlot("material", GUIs.getItemSlot(Icon.UPGRADE_STONE.clone(), "§a§oĐặt Đá Nguyên tố"), getInputExecutor()));
		slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(Icon.ITEM.clone(), "§a§oĐặt trang bị"), getInputExecutor()));
		slots.put(AMULET_SLOT, new GUISlot("amulet", GUIs.getItemSlot(Icon.AMULET.clone(), "§a§oĐặt bùa may (Không bắt buộc)"), getInputExecutor()));
		slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(Icon.RESULT.clone(), "§aKết quả")));
		slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(0), getButtonExecutor()));
		
		return slots;
	}
	
	public static AmountChecker getAmountChecker() {
		return new AmountChecker() {
			@Override
			public boolean allowMulti(ItemStack is) {
				return false;
			}
		};
	}
	
	public static PlaceChecker getInputChecker() {
		return (player, is, status) -> {
			// AMULET
			if (Amulet.is(is)) {
				if (GUIs.countPlaced("amulet", status) != 0) return false;
				status.place(player, GUIs.getEmptySlot("amulet", status), is);
				return true;
			}

			if (is(is)) {
				if (GUIs.countPlaced("material", status) != 0) return false;

				// Check if has item
				var in = GUIs.getItem("item", status);
				if (in != null && in.getType() != Material.AIR) {
					var us = UpgradeStone.read(is);
					var item = Items.read(in);
					if (item.getModel().getElement() != us.getElement()) {
						player.sendMessage("§cĐá nguyên tố không cùng nguyên tố với trang bị");
						return false;
					}
				}

				status.place(player, GUIs.getEmptySlot("material", status), is);
				return true;
			}

			// ITEM
			if (!Items.is(is)) {
				player.sendMessage("§cVật phẩm không hợp lệ!");
				return false;
			}

			// Check base item if placed
			if (GUIs.countPlaced("item", status) == 0) {
				status.place(player, GUIs.getEmptySlot("item", status), is);
				return true;
			}

			// Base item placed
			else {
				// Check if full material
				if (GUIs.countPlaced("material", status) == 1) {
					player.sendMessage("§cĐã đặt đủ trang bị và nguyên liệu!");
					return false;
				}
				// Can place material
				else {
					// Check if right material
					if (!is(is)) {
						player.sendMessage("§cPhải là Đá nâng bậc!");
						return false;
					}

					// Place material
					status.place(player, GUIs.getEmptySlot("material", status), is);
					return true;
				}
			}
		};
	}
	
	public static PlaceExecutor getInputExecutor() {
		return (player, slot, status) -> Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
			if (GUIs.countPlaced("item", status) != 1) return;

			// Create result
			ItemStack r = GUIs.getItem("item", status).clone();
			ItemStack m = GUIs.getItem("material", status);
			if (m == null) return;

			Item i = Items.read(r);
			ItemData data = i.getData();
			int expUp = Configs.UPGRADE_EXP * m.getAmount();
			data.setExp(data.getExp() + expUp);
			Items.write(player, r, i);
			Items.update(player, r, i);

			// Icon result
			ItemStack icon = r.clone();
			ItemStackUtils.setDisplayName(icon, ItemStackUtils.getName(icon) + " §7§o(Sản phẩm)");
			ItemStackUtils.addLoreLine(icon, "");
			ItemStackUtils.addLoreLine(icon, "§a§oĐiểm nguyên tố: " + (data.getExp() - expUp) + " >> " + data.getExp());
			status.getInventory().setItem(RESULT_SLOT, icon);

			status.setData("result", r);

			Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
				// Check can do
				if (GUIs.countAmountPlaced("material", status) == GUIs.countAmountPlaced("amulet", status) || GUIs.countPlaced("amulet", status) == 0) {
					double chance = getChance(status);
					status.getInventory().setItem(BUTTON_SLOT, getOkButton(chance, Configs.getUpgradeFee(i.getData().getGrade())));
					status.setData("canDo", "");
					Tasks.async(() -> {
						player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
					});
				}
				else {
					status.getInventory().setItem(BUTTON_SLOT, getDefaultButton(Configs.getUpgradeFee(i.getData().getGrade())));
					status.removeData("canDo");
				}

			});

		});
	}
	
	public static ClickExecutor getButtonExecutor() {
		return (player, status) -> {
			// Check inventory empty slot
			if (player.getInventory().firstEmpty() == -1) {
				if (!Configs.FULL_DROP) {
					player.sendMessage("§c§lCần chỗ trống trong kho để tránh mất đồ!");
					return;
				}
			}

			// Check can execute
			if (!status.hasData("canDo")) {
				player.sendMessage("§cChưa thể nâng bậc");
				return;
			}

			// Check element
			ItemStack is = GUIs.getItem("item", status);
			ItemStack stone = GUIs.getItem("material", status);
			var item = Items.read(is);
			var us = UpgradeStone.read(stone);
			if (item.getModel().getElement() != us.getElement()) {
				player.sendMessage("§cĐá không cùng nguyên tố với trang bị!");
				return;
			}

			// Do
			ItemStack r = (ItemStack) status.getData("result");
			var readR = Items.read(r);

			int fee = Configs.getUpgradeFee(readR.getData().getGrade());
			double chance = getChance(status);

			// Check fee
			if (!EcoType.MONEY.take(player, fee)) {
				player.sendMessage("§cKhông đủ tiền!");
				return;
			}

			// Get
			int previous = Items.read(is).getData().getAscent().getValue();
			int after = Items.read(is).getData().getAscent().getValue();

			// Amulet
			boolean amulet = GUIs.countPlaced("amulet", status) != 0;

			// Success
			boolean success = false;
			if (Utils.rate(chance)) {
				success = true;
				player.sendTitle("§a§lTHÀNH CÔNG UwU", "", 0, 30, 0);
				player.sendMessage("§a§lThành công UwU");
				player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

				// Give
				if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(r.clone());
				else if (Configs.FULL_DROP) {
					player.getWorld().dropItemNaturally(player.getLocation(), r.clone());
				}

				// Event
				Bukkit.getPluginManager().callEvent(new PlayerItemUpgradeEvent(player, true, previous, after));
			}
			// Fail
			else {
				player.sendTitle("§c§lTHẤT BẠI >_<", "§fMất nguyên liệu", 0, 30, 0);
				player.sendMessage("§c§lThất bại >_<");
				player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 1);

				// Give
				if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(is);
				else if (Configs.FULL_DROP) {
					player.getWorld().dropItemNaturally(player.getLocation(), is);
				}

				// Event
				Bukkit.getPluginManager().callEvent(new PlayerItemUpgradeEvent(player, false, previous, previous));
			}


			// History
			boolean finalSuccess = success;
			Tasks.async(() -> {
				SantoryCore.get().getUpgradeHistory().write(player, readR.getModelID(), readR.getData().getExp(), finalSuccess, amulet);
			});

			GUIs.clearItems("item", status);
			GUIs.clearItems("amulet", status);
			GUIs.clearItems("material", status);
			player.closeInventory();

			if (!Configs.FAST_TIEMREN) Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				GUIs.open(player, GUI.UPGRADE);
			}, 30);
			else GUIs.open(player, GUI.UPGRADE);
		};
	}
	
	private static double getChance(GUIStatus status) {
		double chance = Configs.UPGRADE_BASE_CHANCE;

		// Cal chance
		if (GUIs.countPlaced("amulet", status) != 0) {
			ItemStack isa = GUIs.getItem("amulet", status);
			chance *= ((double) Amulet.read(isa).getBonus() + 100) / 100;
		}
		
		return chance;
	}
	
	public static ItemStack getDefaultButton(double fee) {
		ItemStack is = new ItemStack(Material.RED_CONCRETE);
		ItemStackUtils.setDisplayName(is, "§c§lChưa thể nâng bậc");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		for (Grade g : Grade.values()) {
			lore.add("§6§o- Bậc " + g.name() + ": " + Configs.getExpRequires().get(g) + " điểm");
		}
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getOkButton(double chance, double fee) {
		ItemStack is = new ItemStack(Material.LIME_CONCRETE);
		ItemStackUtils.setDisplayName(is, "§a§lCó thể nâng bậc");
		List<String> lore = Lists.newArrayList();
		lore.add("§a§o- Tỉ lệ §l" + chance + "%");
		lore.add("§f§o- Phí §l" + fee + "$");
		for (Grade g : Grade.values()) {
			lore.add("§6§o- Bậc " + g.name() + ": " + Configs.getExpRequires().get(g) + " điểm");
		}
		lore.add("");
		lore.add("§a§lCLICK để nâng bậc");

		ItemStackUtils.setLore(is, lore);
		ItemStackUtils.addEnchantEffect(is);
		
		return is;
	}

	public static boolean is(ItemStack is) {
		return UpgradeStone.read(is) != null;
	}

}
