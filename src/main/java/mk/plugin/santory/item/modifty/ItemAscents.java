package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.event.PlayerItemAscentEvent;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemAscents {
	
	private static final List<Integer> MATERIAL_SLOTS = Lists.newArrayList(2);
	private static final int ITEM_SLOT = 1;
	private static final int BUTTON_SLOT = 8;
	private static final int AMULET_SLOT = 3;
	private static final int RESULT_SLOT = 6;
	
	public static Map<Integer, GUISlot> getSlots() {
		Map<Integer, GUISlot> slots = Maps.newHashMap();
		MATERIAL_SLOTS.forEach(sl -> {
			slots.put(sl, new GUISlot("material", GUIs.getItemSlot(Icon.SUBITEM.clone(), "§a§oĐặt nguyên liệu trang bị (Phụ)"), getInputExecutor()));
		});
		slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(Icon.ITEM.clone(), "§a§oĐặt trang bị (Chính)"), getInputExecutor()));
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
		return new PlaceChecker() {
			@Override
			public boolean place(Player player, ItemStack is, GUIStatus status) {
				// AMULET
				if (Amulet.is(is)) {
					if (GUIs.countPlaced("amulet", status) != 0) return false; 
					status.place(player, GUIs.getEmptySlot("amulet", status), is);
					return true;
				}

				// ITEM
				if (!Items.is(is)) {
					player.sendMessage("§cVật phẩm không hợp lệ!");
					return false;
				}
				else {
					var item = Items.read(is);
					if (item.getData().isTimed()) {
						player.sendMessage("§cKhông thể đột phá đồ có hạn");
						return false;
					}
				}
				
				// Check base item if placed
				if (GUIs.countPlaced("item", status) == 0) {
					status.place(player, GUIs.getEmptySlot("item", status), is);
					return true;
				}
				
				// Base item placed
				else {
					// Check if full material
					if (GUIs.countPlaced("material", status) == MATERIAL_SLOTS.size()) {
						player.sendMessage("§cĐã đặt đủ trang bị và nguyên liệu rồi!");
						return false;
					}
					// Can place material
					else {
						// Check if right material
						ItemStack isPlaced = GUIs.getItem("item", status);
						Item iPlaced = Items.read(isPlaced);
						Item i = Items.read(is);
						if (!i.getModelID().equals(iPlaced.getModelID())) {
							player.sendMessage("§cNguyên liệu không đúng loại với trang bị!");
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
					Item i = Items.read(r);
					ItemData data = i.getData();
					data.setAscent(Ascent.from(data.getAscent().getValue() + 1));
					Items.write(player, r, i);
					Items.update(player, r, i);

					boolean isArt = i.getModel().getType() == ItemType.ARTIFACT;

					// Icon result
					ItemStack icon = r.clone();
					ItemStackUtils.setDisplayName(icon, ItemStackUtils.getName(icon) + " §7§o(Sản phẩm)");
					if (isArt) {
						ItemStackUtils.addLoreLine(icon, "§a§oTăng ngẫu nhiên một chỉ số khi đột phá");
						Artifact art = Artifact.parse(i.getModel());
						Artifacts.check(i, art);
					}
					Items.write(player, r, i);
					Items.update(player, r, i);

					status.getInventory().setItem(RESULT_SLOT, icon);
					status.setData("result", r);
					
					Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
						// Check can do
						if (GUIs.countPlaced("material", status) == MATERIAL_SLOTS.size()) {
							double chance = getChance(status);
							status.getInventory().setItem(BUTTON_SLOT, getOkButton(chance, Configs.getAscentFee(i.getData().getAscent())));
							status.setData("canDo", "");
							Tasks.async(() -> {
								player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
							});
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
				// Check inventory empty slot
				if (player.getInventory().firstEmpty() == -1) {
					if (!Configs.FULL_DROP) {
						player.sendMessage("§c§lCần chỗ trống trong kho để tránh mất đồ!");
						return;
					}
				}

				// Check can execute
				if (!status.hasData("canDo")) {
					player.sendMessage("§cChưa thể đột phá");
					return;
				}

				// Do
				ItemStack is = GUIs.getItem("item", status);
				ItemStack r = (ItemStack) status.getData("result");

				// Amulet
				boolean amulet = GUIs.countPlaced("amulet", status) != 0;
				var readR = Items.read(r);

				int fee = Configs.getAscentFee(readR.getData().getAscent());
				double chance = getChance(status);
				
				// Check fee
				if (!EcoType.MONEY.take(player, fee)) {
					player.sendMessage("§cKhông đủ tiền!");
					return;
				}

				// Get
				int previous = Items.read(is).getData().getAscent().getValue();
				int after = Items.read(r).getData().getAscent().getValue();

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
					Bukkit.getPluginManager().callEvent(new PlayerItemAscentEvent(player, true, previous, after));
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
					Bukkit.getPluginManager().callEvent(new PlayerItemAscentEvent(player, false, previous, previous));
				}

				// History
				boolean finalSuccess = success;
				Tasks.async(() -> {
					SantoryCore.get().getAscentHistory().write(player, readR.getModelID(), readR.getData().getAscent(), finalSuccess, amulet);
				});

				GUIs.clearItems("item", status);
				GUIs.clearItems("amulet", status);
				GUIs.clearItems("material", status);

				player.closeInventory();

				if (!Configs.FAST_TIEMREN) Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
					GUIs.open(player, GUI.ASCENT);
				}, 30);
				else GUIs.open(player, GUI.ASCENT);

			}
		};
	}
	
	private static double getChance(GUIStatus status) {
		double chance = Configs.ASCENT_BASE_CHANCE;

		// Cal chance
		if (GUIs.countPlaced("amulet", status) != 0) {
			ItemStack isa = GUIs.getItem("amulet", status);
			chance *= ((double) Amulet.read(isa).getBonus() + 100) / 100;
		}
		
		return chance;
	}
	
	public static ItemStack getDefaultButton(double fee) {
		ItemStack is = new ItemStack(Material.RED_CONCRETE);
		ItemStackUtils.setDisplayName(is, "§c§lChưa thể đột phá");
		List<String> lore = Lists.newArrayList();
		lore.add("§f§o- Nguyên liệu phải cùng loại với trang bị");
		lore.add("§f§o- Phí §l" + fee + "$");
		ItemStackUtils.setLore(is, lore);
		
		return is;
	}
	
	public static ItemStack getOkButton(double chance, double fee) {
		ItemStack is = new ItemStack(Material.LIME_CONCRETE);
		ItemStackUtils.setDisplayName(is, "§a§lCó thể đột phá");
		List<String> lore = Lists.newArrayList();
		lore.add("§a§o- Tỉ lệ §f§l" + chance + "%");
		lore.add("§a§o- Phí §f§l" + fee + "$");
		lore.add("");
		lore.add("§a§lCLICK để đột phá");

		ItemStackUtils.setLore(is, lore);
		ItemStackUtils.addEnchantEffect(is);
		
		return is;
	}
	
	
}
