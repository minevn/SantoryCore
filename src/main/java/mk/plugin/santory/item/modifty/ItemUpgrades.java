package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
		slots.put(MATERIAL_SLOT, new GUISlot("material", GUIs.getItemSlot(Icon.UPGRADE_STONE.clone(), "§a§oĐặt Đá nâng bậc"), getInputExecutor()));
		slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(Icon.ITEM.clone(), "§a§oĐặt trang bị"), getInputExecutor()));
		slots.put(AMULET_SLOT, new GUISlot("amulet", GUIs.getItemSlot(Icon.AMULET.clone(), "§a§oĐặt bùa may"), getInputExecutor()));
		slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(Icon.RESULT.clone(), "§aKết quả")));
		slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(), getButtonExecutor()));
		
		return slots;
	}
	
	public static AmountChecker getAmountChecker() {
		return new AmountChecker() {
			@Override
			public boolean allowMulti(ItemStack is) {
				return is(is) || Amulet.is(is);
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
			Items.update(player, r, i);

			boolean isArt = i.getModel().getType() == ItemType.ARTIFACT;

			// Icon result
			ItemStack icon = r.clone();
			ItemStackUtils.setDisplayName(icon, ItemStackUtils.getName(icon) + " §7§o(Sản phẩm)");
			ItemStackUtils.addLoreLine(icon, "");
			ItemStackUtils.addLoreLine(icon, "§a§oExp: " + (data.getExp() - expUp) + " >> " + data.getExp());
			if (isArt) ItemStackUtils.addLoreLine(icon, "§a§oTăng ngẫu nhiên một chỉ số khi lên bậc");
			status.getInventory().setItem(RESULT_SLOT, icon);

			if (isArt) {
				Artifact art = Artifact.parse(i.getModel());
				Artifacts.check(i, art);
			}
			r = Items.write(player, r, i);
			Items.update(player, r, i);
			status.setData("result", r);

			Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
				// Check can do
				if (GUIs.countAmountPlaced("material", status) == GUIs.countAmountPlaced("amulet", status) || GUIs.countPlaced("amulet", status) == 0) {
					double chance = getChance(status);
					status.getInventory().setItem(BUTTON_SLOT, getOkButton(chance));
					status.setData("canDo", "");
				}
				else {
					status.getInventory().setItem(BUTTON_SLOT, getDefaultButton());
					status.removeData("canDo");
				}

			});

		});
	}
	
	public static ClickExecutor getButtonExecutor() {
		return (player, status) -> {
			// Check can execute
			if (!status.hasData("canDo")) {
				player.sendMessage("§cChưa thể nâng bậc");
				return;
			}

			int fee = Configs.UPGRADE_FEE;
			double chance = getChance(status);

			// Check fee
			if (!EcoType.MONEY.take(player, fee)) {
				player.sendMessage("§cKhông đủ tiền!");
				return;
			}

			// Do
			ItemStack is = GUIs.getItem("item", status);
			ItemStack r = (ItemStack) status.getData("result");

			// Success
			if (Utils.rate(chance)) {
				player.sendTitle("§a§lTHÀNH CÔNG ^_^", "", 0, 15, 0);
				player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
				player.getInventory().addItem(r.clone());
			}
			// Fail
			else {
				player.sendTitle("§7§lTHẤT BẠI T_T", "", 0, 15, 0);
				player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 1);
				player.getInventory().addItem(is.clone());
			}

			GUIs.clearItems("item", status);
			GUIs.clearItems("amulet", status);
			GUIs.clearItems("material", status);
			player.closeInventory();

			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				GUIs.open(player, GUI.UPGRADE);
			}, 10);
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
	
	public static ItemStack getDefaultButton() {
		double fee = Configs.UPGRADE_FEE;
		ItemStack is = Icon.BUTTON.clone();
		ItemStackUtils.setDisplayName(is, "§c§lChưa thể nâng bậc");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		lore.add("§f§o- Yêu cầu số lượng đá bằng số lượng bùa");
		lore.add("§f§o  hoặc không có bùa");
		for (Grade g : Grade.values()) {
			lore.add("§6§o- Bậc " + g.name() + ": " + Configs.getExpRequires().get(g) + " exp");
		}
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getOkButton(double chance) {
		double fee = Configs.UPGRADE_FEE;
		ItemStack is = Icon.BUTTON.clone();
		ItemStackUtils.setDisplayName(is, "§a§lCó thể nâng bậc");
		List<String> lore = Lists.newArrayList();
		lore.add("§a§o- Tỉ lệ §l" + chance + "%");
		lore.add("§f§o- Phí §l" + fee + "$");
		for (Grade g : Grade.values()) {
			lore.add("§6§o- Bậc " + g.name() + ": " + Configs.getExpRequires().get(g) + " exp");
		}
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	private static final String NAME = "§a§lĐá nâng bậc";
	
	public static ItemStack get() {
		ItemStack item = ItemStackUtils.create(Material.IRON_NUGGET, 2);
		ItemStackUtils.setDisplayName(item, NAME);
		ItemStackUtils.addLoreLine(item, "§7§oCó tác dụng nâng bậc cho trang bị");
		ItemStackUtils.addEnchantEffect(item);
		return item;
	}
	
	public static boolean is(ItemStack item) {
		if (item == null) return false;
		if (!item.hasItemMeta()) return false;
		return ItemStackUtils.getName(item).contains(NAME);
	}
	
}
