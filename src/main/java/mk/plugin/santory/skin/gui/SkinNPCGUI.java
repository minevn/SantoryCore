package mk.plugin.santory.skin.gui;

import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.skin.system.NPCSkins;
import mk.plugin.santory.utils.Tasks;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SkinNPCGUI {

    public static Map<Integer, String> currentSkins = Maps.newConcurrentMap();

    public static void open(Player p, int npc) {
        List<String> skins = Configs.getSkins();
        int size = skins.size() % 9 == 0 ? skins.size() : (skins.size() / 9 + 1) * 9;
        var inv = Bukkit.createInventory(new SkinNPCHolder(npc), size, "§0§lCHỌN SKIN");
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        Tasks.async(() -> {
            var current = getCurrentSkin(npc);
            for (int i = 0; i < skins.size(); i++) {
                var skin = skins.get(i);
                var is = Items.build(p, skin);
                if (skin.equals(current)) {
                    var meta = is.getItemMeta();
                    meta.addEnchant(Enchantment.DURABILITY, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    is.setItemMeta(meta);
                }
                inv.setItem(i, is);
            }
        });
    }

    public static void onNPCInteract(NPCClickEvent e) {
        var npc = e.getNPC().getId();
        if (!Configs.getSkinNPCs().contains(npc)) return;

        var p = e.getClicker();
        open(p, npc);
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof SkinNPCHolder)) return;
        e.setCancelled(true);
        if (e.getWhoClicked().getOpenInventory().getTopInventory() != e.getClickedInventory()) return;

        int slot = e.getSlot();
        var p = e.getWhoClicked();
        var holder = (SkinNPCHolder) e.getInventory().getHolder();
        var skins = Configs.getSkins();
        if (slot < skins.size()) {
            var skin = skins.get(slot);
            var le = (LivingEntity) CitizensAPI.getNPCRegistry().getById(holder.getNPC()).getEntity();
            NPCSkins.equip(le, skin);

            p.closeInventory();;
            p.sendMessage("§aThay đổi skin thành công!");
            ((Player) p).playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }
    }

    public static String getCurrentSkin(int npc) {
        return currentSkins.getOrDefault(npc, null);
    }

}

class SkinNPCHolder implements InventoryHolder {

    private int npc;

    public SkinNPCHolder(int npc) {
        this.npc = npc;
    }

    public int getNPC() {
        return npc;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}