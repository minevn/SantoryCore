package mk.plugin.santory.listener;

import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.Skins;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;

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


    @EventHandler
    public void onOffHandEquip(InventoryClickEvent e) {
        var inv = e.getClickedInventory();
        if (inv == null || inv.getType() != InventoryType.PLAYER && inv.getType() != InventoryType.CRAFTING) return;

        var p = (Player) e.getWhoClicked();
        var slot = e.getSlot();
        var cursor = e.getCursor();

        if (slot == 39) {
            p.sendMessage("§cKhông thể tương tác, hành động đã được ghi lại");
            p.sendMessage("§c§lNẾU BẠN CỐ Ý BUG/TÌM CÁCH BUG THÌ BẠN SẼ BỊ TRỪNG PHẠT THÍCH ĐÁNG");
            e.setCancelled(true);
            SantoryCore.get().getLogger().warning("Player " + p.getName() + " interacted with helmet slot");
        }
    }

    /*
    Off hand 2
    */
    @EventHandler
    public void onOffHandEquip222(InventoryDragEvent e) {
        var inv = e.getInventory();
        if (inv.getType() != InventoryType.PLAYER && inv.getType() != InventoryType.CRAFTING) return;

        var p = (Player) e.getWhoClicked();
        var slots = e.getInventorySlots();
        var cursor = e.getOldCursor();

        if (slots.contains(39)) {
            p.sendMessage("§cKhông thể tương tác, hành động đã được ghi lại");
            p.sendMessage("§c§lNẾU BẠN CỐ Ý BUG/TÌM CÁCH BUG THÌ BẠN SẼ BỊ TRỪNG PHẠT THÍCH ĐÁNG");
            e.setCancelled(true);
            SantoryCore.get().getLogger().warning("Player " + p.getName() + " interacted with helmet slot");
        }
    }

}
