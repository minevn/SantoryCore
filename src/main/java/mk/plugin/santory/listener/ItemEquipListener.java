package mk.plugin.santory.listener;

import mk.plugin.santory.event.ArmorEquipEvent;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.armor.ArmorType;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemEquipListener implements Listener {

    /*
    Skin equip
     */
    @EventHandler
    public void onSkinEquip(PlayerInteractEvent e) {
        var is = e.getItem();
        var skin = Skins.read(is);
        if (skin == null) return;

        e.setCancelled(true);

        if (e.getHand() != EquipmentSlot.HAND || !e.getAction().name().contains("RIGHT")) return;

        var player = e.getPlayer();
        var inv = player.getInventory();
        switch (skin.getType()) {
            case OFFHAND:
                inv.setItemInMainHand(inv.getItem(40));
                inv.setItem(40, is);
                break;
            case HEAD:
                inv.setItemInMainHand(inv.getHelmet());
                inv.setHelmet(is);
                break;
        }

        player.sendMessage("§aTrang bị skin thành công!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    /*
    Armor
     */
    @EventHandler
    public void onArmor(ArmorEquipEvent e) {
        ItemStack ni = e.getNewArmorPiece();

        // Take item away
        if (ni == null || ni.getType() == Material.AIR) return;

        // Cancel
        e.setCancelled(true);

        // Armor
        if (Items.isType(ni, ItemType.ARMOR) && e.getType() == ArmorType.CHESTPLATE) {
            e.setCancelled(false);
        }

        // Skin
        var skin = Skins.read(ni);
        if (skin != null && e.getType() == ArmorType.HELMET && skin.getType() == SkinType.HEAD) {
            e.setCancelled(false);
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
        var action = e.getAction();
        var slot = e.getSlot();
        var cursor = e.getCursor();
        if (action.name().contains("PLACE") || action == InventoryAction.SWAP_WITH_CURSOR) {

            var skin = Skins.read(cursor);
            if (skin == null) return;

            // Skin offhand
            if (slot == 40) {
                if (skin.getType() != SkinType.OFFHAND) e.setCancelled(true);
            }

            // Skin hat
            if (slot == 39) {
                if (skin.getType() != SkinType.HEAD) e.setCancelled(true);
            }
        }
    }


}
