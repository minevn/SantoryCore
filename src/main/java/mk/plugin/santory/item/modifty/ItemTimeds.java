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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemTimeds {

    private static final List<Integer> MATERIAL_SLOTS = Lists.newArrayList(2);
    private static final int ITEM_SLOT = 1;
    private static final int BUTTON_SLOT = 8;
    private static final int RESULT_SLOT = 6;

    public static Map<Integer, GUISlot> getSlots() {
        Map<Integer, GUISlot> slots = Maps.newHashMap();
        MATERIAL_SLOTS.forEach(sl -> {
            slots.put(sl, new GUISlot("material", GUIs.getItemSlot(Icon.SUBITEM.clone(), "§a§oĐặt trang bị vĩnh viễn (Phụ)"), getInputExecutor()));
        });
        slots.put(ITEM_SLOT, new GUISlot("item", GUIs.getItemSlot(Icon.ITEM.clone(), "§a§oĐặt trang bị có hạn (Chính)"), getInputExecutor()));
        slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(Icon.RESULT.clone(), "§aKết quả")));
        slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(), getButtonExecutor()));

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
                // ITEM
                if (GUIs.countPlaced("item", status) == 0) {
                    if (!Items.is(is)) player.sendMessage("§cVật phẩm không hợp lệ!");
                    var item = Items.read(is);
                    if (!item.getData().isTimed()) {
                        player.sendMessage("§cTrang bị chính phải là trang bị có hạn!");
                        return false;
                    }
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
                        if (i.getData().isTimed()) {
                            player.sendMessage("§cNguyên liệu phải là trang bị vĩnh viễn!");
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
                    data.setTimed(0);

                    Items.write(player, r, i);
                    Items.update(player, r, i);

                    // Icon result
                    ItemStack icon = r.clone();
                    ItemStackUtils.setDisplayName(icon, ItemStackUtils.getName(icon) + " §7§o(Sản phẩm)");
                    ItemStackUtils.addLoreLine(icon, "§a§oChuyển thành trang bị vĩnh viễn");
                    Items.write(player, r, i);
                    Items.update(player, r, i);

                    status.getInventory().setItem(RESULT_SLOT, icon);
                    status.setData("result", r);

                    Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                        // Check can do
                        if (GUIs.countPlaced("material", status) == MATERIAL_SLOTS.size()) {
                            status.getInventory().setItem(BUTTON_SLOT, getOkButton());
                            status.setData("canDo", "");
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
                    player.sendMessage("§cChưa thể ghép");
                    return;
                }

                int fee = Configs.TIMED_FEE;

                // Check fee
                if (!EcoType.MONEY.take(player, fee)) {
                    player.sendMessage("§cKhông đủ tiền!");
                    return;
                }

                // Do
                ItemStack r = (ItemStack) status.getData("result");

                // Amulet
                boolean amulet = GUIs.countPlaced("amulet", status) != 0;
                var readR = Items.read(r);

                // Success
                player.sendTitle("§a§lTHÀNH CÔNG UwU", "", 0, 15, 0);
                player.sendMessage("§a§lThành công UwU");
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

                // Give
                if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(r.clone());
                else if (Configs.FULL_DROP) {
                    player.getWorld().dropItemNaturally(player.getLocation(), r.clone());
                }

                // History
                Tasks.async(() -> {
                    SantoryCore.get().getTimedHistory().write(player, readR.getModelID());
                });

                GUIs.clearItems("item", status);
                GUIs.clearItems("material", status);
                player.closeInventory();

                if (!Configs.FAST_TIEMREN) Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
                    GUIs.open(player, GUI.TIMED);
                }, 30);
                else GUIs.open(player, GUI.TIMED);
            }
        };
    }

    public static ItemStack getDefaultButton() {
        double fee = Configs.TIMED_FEE;
        ItemStack is = Icon.BUTTON.clone();
        ItemStackUtils.setDisplayName(is, "§c§lChưa thể ghép");
        List<String> lore = Lists.newArrayList();
        lore.add("§f§o- Trang bị có hạn và trang bị vĩnh viễn phải giống nhau");
        lore.add("§f§o- Phí §l" + fee + "$");
        ItemStackUtils.setLore(is, lore);

        return is;
    }

    public static ItemStack getOkButton() {
        double fee = Configs.TIMED_FEE;
        ItemStack is = Icon.BUTTON.clone();
        ItemStackUtils.setDisplayName(is, "§a§lCó thể đột ghép vĩnh viễn");
        List<String> lore = Lists.newArrayList();
        lore.add("§f§o- Phí §l" + fee + "$");
        ItemStackUtils.setLore(is, lore);

        return is;
    }


}
