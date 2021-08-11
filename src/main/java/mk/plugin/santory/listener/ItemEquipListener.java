package mk.plugin.santory.listener;

import mk.plugin.santory.event.ArmorEquipEvent;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.armor.ArmorType;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.SkinType;
import mk.plugin.santory.skin.Skins;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemEquipListener implements Listener {

    // No hand hoding skin
    @EventHandler
    public void onHoldSkin(PlayerItemHeldEvent e) {
        var p = e.getPlayer();
        if (p.hasPermission("santory.admin")) return;

        var slot = e.getNewSlot();
        var is = p.getInventory().getItem(slot);
        if (Skins.read(is) != null) {
            e.setCancelled(true);
            p.sendMessage("§cKhông thể cầm skin trên tay!");
        }
    }

    /*
    Off hand
     */
    @EventHandler
    public void onOffHandEquip(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);

        var mainHand = e.getPlayer().getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.AIR) {
            var skin = Skins.read(mainHand);
            if (skin != null && skin.getType() == SkinType.OFFHAND) {
                e.setCancelled(false);
                return;
            }
        } else {
            e.setCancelled(false);
            return;
        }

        e.getPlayer().sendMessage("§cChỉ để trang bị skin");
    }

    /*
    Off hand
     */
    @EventHandler
    public void onOffHandEquip(InventoryClickEvent e) {
        var inv = e.getClickedInventory();
        if (inv == null || inv.getType() != InventoryType.PLAYER) return;

        var p = (Player) e.getWhoClicked();
        var slot = e.getSlot();
        var cursor = e.getCursor();

        if (slot == 39) {
            p.sendMessage("§cKhông thể tương tác, hành động đã được ghi lại");
            p.sendMessage("§c§lNẾU BẠN CỐ Ý BUG/TÌM CÁCH BUG THÌ BẠN SẼ BỊ TRỪNG PHẠT THÍCH ĐÁNG");
            e.setCancelled(true);
            SantoryCore.get().getLogger().warning("Player " + p.getName() + " interacted with helmet slot");
        }

        if (Items.is(cursor) && slot == 40) {
            p.sendMessage("§cKhông thể tương tác tay phụ với trang bị này!");
            e.setCancelled(true);
        }
    }


}
