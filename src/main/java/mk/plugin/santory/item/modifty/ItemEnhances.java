package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.event.PlayerItemAscentEvent;
import mk.plugin.santory.event.PlayerItemEnhanceEvent;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemEnhances {
	
	public static final int MAX_LEVEL = 50;
	
	private static final int ITEM_SLOT = 1;
	private static final int BUTTON_SLOT = 8;
	private static final int MATERIAL_SLOT = 2;
	private static final int AMULET_SLOT = 3;
	private static final int RESULT_SLOT = 5;

	public static Map<Integer, GUISlot> getSlots() {
		Map<Integer, GUISlot> slots = Maps.newHashMap();
		slots.put(MATERIAL_SLOT, new GUISlot("material", GUIs.getItemSlot(Icon.ENHANCE_STONE.clone(), "§a§oĐặt Đá cường hóa"), getInputExecutor()));
		slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(Icon.ITEM.clone(), "§a§oĐặt trang bị"), getInputExecutor()));
		slots.put(AMULET_SLOT, new GUISlot("amulet", GUIs.getItemSlot(Icon.AMULET.clone(), "§a§oĐặt bùa may"), getInputExecutor()));
		slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(Icon.RESULT.clone(), "§aKết quả")));
		slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(0), getButtonExecutor()));
		
		return slots;
	}
	
	public static AmountChecker getAmountChecker() {
		return is -> {
			return false;
//				return is(is) || Amulet.is(is);
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
				// Check level
				var item = Items.read(is);
				int lv = item.getData().getLevel();
				var grade = item.getData().getGrade();
				if (lv >= grade.getMaxEnhance()) {
					player.sendMessage("§cNâng bậc để cường hóa cao hơn!");
					return false;
				}

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
						player.sendMessage("§cPhải là Đá cường hóa!");
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
			int lvUp = m.getAmount();
			data.setLevel(Math.min(data.getLevel() + lvUp, data.getGrade().getMaxEnhance()));
			Items.write(player, r, i);
			Items.update(player, r, i);

			status.setData("level", data.getLevel());

			// Icon result
			ItemStack icon = r.clone();
			ItemStackUtils.setDisplayName(icon, ItemStackUtils.getName(icon) + " §7§o(Sản phẩm)");
			ItemStackUtils.addLoreLine(icon, "");
			ItemStackUtils.addLoreLine(icon, "§c§oGiới hạn cường hóa là Lv." + data.getGrade().getMaxEnhance());
			ItemStackUtils.addLoreLine(icon, "§c§oNâng bậc để gia tăng giới hạn");

			status.getInventory().setItem(RESULT_SLOT, icon);
			status.setData("result", r);

			// Amulet
			Amulet a = null;
			if (GUIs.countPlaced("amulet", status) == 1) {
				a = Amulet.read(GUIs.getItem("amulet", status));
				status.setData("hasAmulet", true);
			}

			Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
				// Check can do
				if (GUIs.countAmountPlaced("material", status) == GUIs.countAmountPlaced("amulet", status) || GUIs.countPlaced("amulet", status) == 0) {
					status.getInventory().setItem(BUTTON_SLOT, getOkButton(status, Configs.getEnanceFee(i.getData().getLevel())));
					status.setData("canDo", "");
				}
				else {
					status.getInventory().setItem(BUTTON_SLOT, getDefaultButton(Configs.getEnanceFee(i.getData().getLevel())));
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
				player.sendMessage("§cChưa thể cường hóa");
				return;
			}

			// Do
			ItemStack is = GUIs.getItem("item", status);
			ItemStack r = (ItemStack) status.getData("result");

			var readR = Items.read(r);

			int fee = Configs.getEnanceFee(readR.getData().getLevel());
			double chance = getChance(status);

			// Check fee
			if (!EcoType.MONEY.take(player, fee)) {
				player.sendMessage("§cKhông đủ tiền!");
				return;
			}

			// Get
			int previous = Items.read(is).getData().getAscent().getValue();
			int after = Items.read(r).getData().getAscent().getValue();

			// Amulet
			boolean amulet = status.hasData("hasAmulet");

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
				Bukkit.getPluginManager().callEvent(new PlayerItemEnhanceEvent(player, true, previous, after));
			}
			// Fail
			else {
				player.sendMessage("§c§lThất bại >_<");
				player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 1);

				if (amulet) player.sendTitle("§c§lTHẤT BẠI >_<", "§aKhông bị trừ cấp vì có Bùa may mắn", 0, 30, 0);
				else {
					Item i = Items.read(is);
					i.getData().setLevel(Math.max(0, i.getData().getLevel() - 1));
					after = i.getData().getLevel();
					Items.write(player, is, i);
					Items.update(player, is, i);
					player.sendTitle("§c§lTHẤT BẠI >_<", "§aBị trừ cấp vì không có Bùa may mắn", 0, 30, 0);
				}

				// Give
				if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(is);
				else if (Configs.FULL_DROP) {
					player.getWorld().dropItemNaturally(player.getLocation(), is);
				}

				// Event
				Bukkit.getPluginManager().callEvent(new PlayerItemEnhanceEvent(player, false, previous, after));
			}

			// History
			boolean finalSuccess = success;
			Tasks.async(() -> {
				SantoryCore.get().getEnhanceHistory().write(player, readR.getModelID(), readR.getData().getLevel(), finalSuccess, amulet);
			});

			GUIs.clearItems("item", status);
			GUIs.clearItems("amulet", status);
			GUIs.clearItems("material", status);
			player.closeInventory();

			if (!Configs.FAST_TIEMREN) Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
				GUIs.open(player, GUI.ENHANCE);
			}, 30);
			else GUIs.open(player, GUI.ENHANCE);
		};
	}
	
	private static double getChance(GUIStatus status) {
		int level = Integer.parseInt(status.getData("level").toString());
		double chance = Configs.getEnhanceRate(level);

		// Cal chance
		if (GUIs.countPlaced("amulet", status) != 0) {
			ItemStack isa = GUIs.getItem("amulet", status);
			chance *= ((double) Amulet.read(isa).getBonus() + 100) / 100;
		}
		
		return chance;
	}
	
	public static ItemStack getDefaultButton(double fee) {
		ItemStack is = Icon.BUTTON.clone();
		ItemStackUtils.setDisplayName(is, "§c§lChưa thể cường hóa");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		lore.add("§f§o- Yêu cầu số lượng đá bằng số lượng bùa");
		lore.add("§f§o  hoặc không có bùa");
		
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getOkButton(GUIStatus status, double fee) {
		ItemStack is = Icon.BUTTON.clone();
		ItemStackUtils.setDisplayName(is, "§a§lCó thể cường hóa");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		double chance = status != null ? getChance(status) : 0;
		lore.add("§a§o- Tỉ lệ: §l" + chance + "%");
		
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static CheckBack check(int level, int amount, boolean safe, Grade grade) {
		int r = level;
		int remain = amount;
		for (int i = 0 ; i < amount ; i++) {
			if (r >= MAX_LEVEL) return new CheckBack(r, remain);
			if (r >= grade.getMaxEnhance()) new CheckBack(r, remain);
			if (Utils.rate(Configs.getEnhanceRate(r))) r++;
			else if (!safe) r--;
			remain--;
		}
		return new CheckBack(r, remain);
	}
	
	private static final String NAME = "§c§lĐá cường hóa";
	
	public static ItemStack get() {
		ItemStack item = ItemStackUtils.create(Material.IRON_NUGGET, 1);
		ItemStackUtils.setDisplayName(item, NAME);
		ItemStackUtils.addLoreLine(item, "§7§oCó tác dụng tăng cấp cho trang bị");
		return item;
	}
	
	public static boolean is(ItemStack item) {
		if (item == null) return false;
		if (!item.hasItemMeta()) return false;
		if (!item.getItemMeta().hasDisplayName()) return false;
		return ItemStackUtils.getName(item).contains(NAME);
	}
}

class CheckBack {
	
	public int level;
	public int remain;
	
	public CheckBack(int level, int remain) {
		this.level = level;
		this.remain = remain;
	}
	
}
