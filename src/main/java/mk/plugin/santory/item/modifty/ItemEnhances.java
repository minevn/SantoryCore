package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
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

public class ItemEnhances {
	
	public static final int MAX_LEVEL = 50;
	
	private static final int ITEM_SLOT = 10;
	private static final int BUTTON_SLOT = 32;
	private static final int MATERIAL_SLOT = 12;
	private static final int AMULET_SLOT = 14;
	private static final int RESULT_SLOT = 25;

	public static Map<Integer, GUISlot> getSlots() {
		Map<Integer, GUISlot> slots = Maps.newHashMap();
		slots.put(MATERIAL_SLOT, new GUISlot("material", GUIs.getItemSlot(DyeColor.GREEN, "§a§oĐặt Đá cường hóa"), getInputExecutor()));
		slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(DyeColor.WHITE, "§a§oĐặt trang bị"), getInputExecutor()));
		slots.put(AMULET_SLOT, new GUISlot("amulet", GUIs.getItemSlot(DyeColor.PURPLE, "§a§oĐặt bùa may"), getInputExecutor()));
		slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(DyeColor.BLUE, "§aKết quả")));
		slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(), getButtonExecutor()));
		
		return slots;
	}
	
	public static AmountChecker getAmountChecker() {
		return new AmountChecker() {
			@Override
			public boolean allowMulti(ItemStack is) {
				return false;
//				return is(is) || Amulet.is(is);
			}
		};
	}
	
	public static PlaceChecker getInputChecker() {
		return new PlaceChecker() {
			@Override
			public boolean place(Player player, ItemStack is, GUIStatus status) {
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
							player.sendMessage("§cPhải là Đá cường hóa!");
							return false;
						}
						
						// Place material
						status.place(player, GUIs.getEmptySlot("material", status), is);
						return true;
					}
				}
			}
		};
	}
	
	public static PlaceExecutor getInputExecutor() {
		return new PlaceExecutor() {	
			@Override
			public void execute(Player player, int slot, GUIStatus status) {
				Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
					if (GUIs.countPlaced("item", status) != 1) return;

					// Create result
					ItemStack r = GUIs.getItem("item", status).clone();
					ItemStack m = GUIs.getItem("material", status);
					if (m == null) return;
					
					Item i = Items.read(r);
					ItemData data = i.getData();
					int lvUp = m.getAmount();
					data.setLevel(Math.min(lvUp, data.getGrade().getMaxEnhance()));
					r = Items.write(player, r, i);
					Items.update(player, r, i);
					
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
					}
					final Amulet amulet = a;
					
					Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
						// Check can do
						if (GUIs.countAmountPlaced("material", status) == GUIs.countAmountPlaced("amulet", status) || GUIs.countPlaced("amulet", status) == 0) {
							status.getInventory().setItem(BUTTON_SLOT, getOkButton(data.getLevel() - lvUp, lvUp, amulet));
							status.setData("canDo", "");
						}
						else {
							status.getInventory().setItem(BUTTON_SLOT, getDefaultButton());
							status.removeData("canDo");
						}
						
					});

				});

			}

		};
	}
	
	public static ClickExecutor getButtonExecutor() {
		return new ClickExecutor() {	
			@Override
			public void execute(Player player, GUIStatus status) {
				// Check can execute
				if (!status.hasData("canDo")) {
					player.sendMessage("§cChưa thể cường hóa");
					return;
				}
				
				int fee = Configs.ENHANCE_FEE;
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
			}
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
		double fee = Configs.ENHANCE_FEE;
		ItemStack is = new ItemStack(Material.LEGACY_CONCRETE);
		is.setDurability(Utils.getColor(DyeColor.RED));
		ItemStackUtils.setDisplayName(is, "§c§lChưa thể cường hóa");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		lore.add("§f§o- Yêu cầu số lượng đá bằng số lượng bùa");
		lore.add("§f§o  hoặc không có bùa");
		
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getOkButton(int level, int up, Amulet a) {
		double fee = Configs.ENHANCE_FEE;
		ItemStack is = new ItemStack(Material.LEGACY_CONCRETE);
		is.setDurability(Utils.getColor(DyeColor.GREEN));
		ItemStackUtils.setDisplayName(is, "§a§lCó thể nâng bậc");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Phí §l" + fee + "$");
		for (int i = level + 1 ; i <= level + up ; i++) {
			double chance = Configs.getEnhanceRate(i);
			if (a != null) chance *= ((double) 100 + a.getBonus()) / 100;
			lore.add("§a§o- Tỉ lệ " + i + ": §l" + chance + "%");
		}
		
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
		ItemStack item = new ItemStack(Material.LEGACY_INK_SACK, 1, (short) 15);
		ItemStackUtils.setDisplayName(item, NAME);
		ItemStackUtils.addLoreLine(item, "§7§oCó tác dụng tăng cấp cho trang bị");
		ItemStackUtils.addEnchantEffect(item);
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
