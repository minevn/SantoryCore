package mk.plugin.santory.skin.gui;

import com.google.common.collect.Lists;
import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.SkinType;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.skin.system.PlayerSkin;
import mk.plugin.santory.skin.system.PlayerSkins;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.ItemStackManager;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.ObjectInputFilter;
import java.util.List;
import java.util.Map;

public class SkinGUI {

    private static int HEAD = 2;
    private static List<Integer> HANDS = Lists.newArrayList(3, 4, 5, 6);
    private static List<Integer> SLOTS = Lists.newArrayList(2, 3, 4, 5, 6);

    public static void open(Player p) {
        var inv = Bukkit.createInventory(new SkinGUIHolder(), 9, "§0§lSKIN - TRANG PHỤC");
        p.openInventory(inv);

        Tasks.async(() -> {
            inv.setItem(HEAD, getHead());
            for (Integer slot : HANDS) inv.setItem(slot, getBack());
            var skindata = PlayerSkins.get(p.getName());
            for (int i = 0; i < skindata.getSkins().size(); i++) {
                var item = skindata.getSkins().get(i);
                if (item == null) continue;;
                inv.setItem(SLOTS.get(i), Items.build(p, item));
            }
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof SkinGUIHolder)) return;

        if (e.getClickedInventory()== e.getWhoClicked().getOpenInventory().getTopInventory()) {
            var player = (Player) e.getWhoClicked();
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
            e.setCancelled(true);

            int slot = e.getSlot();
            ItemStack current = e.getCurrentItem();

            boolean isSkin = Skins.read(current) != null;
            int limit = getHandLimit(player);

            if (SLOTS.contains(slot) || isSkin) {
                ItemStack cursor = e.getCursor();
                var cursorSkin = Skins.read(cursor);
                if (cursorSkin != null) {
                    var item = Items.read(cursor);
                    if (hasTheSameIn(e.getClickedInventory(), item, player)) {
                        player.sendMessage("§c§l§oBạn đã có Skin này trong kho rồi!");
                        return;
                    }

                    // Check slot
                    if (cursorSkin.getType() == SkinType.HEAD) {
                        if (slot != HEAD) {
                            player.sendMessage("§c§lĐể không đúng vị trí");
                            return;
                        }
                    }
                    else {
                        if (!HANDS.contains(slot)) {
                            player.sendMessage("§c§lĐể không đúng vị trí");
                            return;
                        }

                        // Count
                        int c = 0;
                        for (Integer slotcheck : HANDS) {
                            var is = e.getInventory().getItem(slotcheck);
                            if (!isBlankSlot(is)) c++;
                        }
                        if (c >= limit) {
                            player.sendMessage("§c§lQuá số lượng cho phép, nâng rank để trang bị nhiều hơn");
                            return;
                        }
                    }

                    if (isBlankSlot(current)) {
                        e.setCursor(null);
                    } else 	e.setCursor(current);
                    e.setCurrentItem(cursor);
                }
                else {
                    if (isSkin) {
                        // Check slot
                        e.setCursor(current);
                        if (slot != HEAD) e.setCurrentItem(getBack());
                        else e.setCurrentItem(getHead());
                    }
                }
            }
        }
    }

    public static void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof SkinGUIHolder)) return;

        List<Item> items = Lists.newArrayList();
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();

        int count = 0;
        int limit = getHandLimit(player);
        for (Integer slot : SLOTS) {
            ItemStack is = inv.getItem(slot);
            if (isBlankSlot(is)) {
                items.add(null);
                continue;
            }

            var item = Items.read(is);
            var skin = Skins.of(item);
            if (skin.getType() == SkinType.OFFHAND) count++;

            if (count > limit) {
                player.getInventory().addItem(is);
            }
            else items.add(Items.read(is));
        }

        var skindata = PlayerSkins.get(player.getName());
        skindata.setSkins(items);
        PlayerSkins.save(skindata);

        player.sendMessage("§aDữ liệu về Skin của bạn đã được lưu!");
        player.sendMessage("§aVượt quá số lượng skin thì sẽ §ctrả về kho đồ§a, nhớ để trống kho đồ");

        PlayerSkins.destroy(player);
        PlayerSkins.equip(player);

        Tasks.async(() -> Travelers.updateState(player));
    }

    private static boolean hasTheSameIn(Inventory inv, Item item, Player player) {
        var t = Travelers.get(player);
        for (Integer i : SLOTS) {
            var is = inv.getItem(i);
            if (Items.is(is)) {
                var art = Items.read(is);
                if (art.getModelID().equals(item.getModelID())) return true;
            }
        }
        return false;
    }

    private static ItemStack getBack() {
        var is = new ItemStack(Material.GLASS_PANE);
        var ism  = new ItemStackManager(SantoryCore.get(), is);
        ism.setModelData(54);
        ism.setName("§a§oÔ để Skin không phải đầu");
        return is;
    }

    private static ItemStack getHead() {
        var is = new ItemStack(Material.GLASS_PANE);
        var ism  = new ItemStackManager(SantoryCore.get(), is);
        ism.setModelData(55);
        ism.setName("§a§oÔ để Skin đầu");
        return is;
    }

    public static boolean isBlankSlot(ItemStack item) {
        if (item == null) return false;
        return !Items.is(item);
    }

    public static int getHandLimit(Player p) {
        if (p.hasPermission("santory.admin")) return 999;
        int limit = Configs.getSkinHandLimitDefault();
        for (Map.Entry<String, Integer> e : Configs.getSkinHandLimits().entrySet()) {
            var perm = e.getKey();
            var v = e.getValue();
            if (p.hasPermission(perm) && v > limit) {
                limit = v;
            }
        }
        return limit;
    }

}

class SkinGUIHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}