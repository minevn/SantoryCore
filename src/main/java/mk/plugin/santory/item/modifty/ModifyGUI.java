package mk.plugin.santory.item.modifty;

import mk.plugin.santory.gui.GUI;
import mk.plugin.santory.gui.GUIs;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifyGUI {

    private static final int ENHANCE_SLOT = 2;
    private static final int UPGRADE_SLOT = 4;
    private static final int ASCENT_SLOT = 6;

    public static void open(Player player) {
        var inv = Bukkit.createInventory(new Holder(), 9, "§0§lTIỆM RÈN");
        player.openInventory(inv);
        Tasks.async(() -> {
            for (int i = 0 ; i < inv.getSize() ; i++) inv.setItem(i, Utils.getBlackSlot());
            inv.setItem(ENHANCE_SLOT, getEnhanceIcon());
            inv.setItem(UPGRADE_SLOT, getUpgradeIcon());
            inv.setItem(ASCENT_SLOT, getAscentIcon());
        });
    }

    public static void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv == null) return;
        if (inv.getHolder() instanceof Holder == false) return;


        var player = (Player) e.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
        int slot = e.getSlot();
        switch (slot) {
            case ENHANCE_SLOT:
                GUIs.open(player, GUI.ENHANCE);
            case UPGRADE_SLOT:
                GUIs.open(player, GUI.UPGRADE);
            case ASCENT_SLOT:
                GUIs.open(player, GUI.ASCENT);
        }
    }

    private static ItemStack getEnhanceIcon() {
        var is = new ItemStack(Material.STONE);
        return is;
    }

    private static ItemStack getUpgradeIcon() {
        var is = new ItemStack(Material.COAL_BLOCK);
        return is;
    }

    private static ItemStack getAscentIcon() {
        var is = new ItemStack(Material.IRON_BLOCK);
        return is;
    }

}

class Holder implements InventoryHolder {

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}