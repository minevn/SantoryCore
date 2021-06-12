package mk.plugin.santory.traveler;

import com.google.common.collect.Lists;
import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.modifty.ModifyGUI;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.gui.SlaveSelectGUI;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TravelerInfoGUI {

    private static final int STAT_SLOT = 0;
    private static final int DATA_SLOT = 1;
    private static final int ARMOR_SLOT = 2;
    private static final List<Integer> ARTIFACT_SLOTS = Lists.newArrayList(3, 4, 5, 6, 7);

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(new TIGHolder(player), 9, "§0§lTHÔNG TIN");
        player.openInventory(inv);
        player.playSound(player .getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);

        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
           var datais = getDataIcon(player);
           datais.setType(Material.PAPER);
           var statis = getStatIcon(player);
           statis.setType(Material.APPLE);
           inv.setItem(0, datais);
           inv.setItem(1, statis);
           inv.setItem(3, getArtifactIcon());
           inv.setItem(4, getSlaveIcon());
           inv.setItem(8, getFapsuIcon());
        });
    }

    public static void open(Player viewer, Player target) {
        if (target == null) {
            viewer.sendMessage("§cTên người chơi không đúng!");
            return;
        }
        boolean isOne = viewer == target;
        int bonus = isOne ? 0 : 45;
        int size = bonus + 9;
        Inventory inv = Bukkit.createInventory(new TIGHolder(), size, "§0§lTHÔNG TIN " + target.getName().toUpperCase());
        viewer.openInventory(inv);
        viewer.playSound(viewer.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);

        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
            // Storage
            if (!isOne) {
                inv.setStorageContents(target.getInventory().getStorageContents());
            }

            //
            inv.setItem(bonus + STAT_SLOT, getStatIcon(target));
            inv.setItem(bonus + DATA_SLOT, getDataIcon(target));

            // Armor
            ItemStack armor = target.getInventory().getChestplate();
            if (armor == null) armor = Utils.getBlackSlot();
            else armor = armor.clone();
            inv.setItem(bonus + ARMOR_SLOT, armor);

            // Artifacts
            for (Integer slot : ARTIFACT_SLOTS) {
                inv.setItem(slot + bonus, getEmptyArtifactIcon());
            }
            Traveler t = Travelers.get(target);
            List<Item> artifacts = t.getData().getArtifacts();
            int i = -1;
            for (Item art : artifacts) {
                if (art != null) {
                    i++;
                    ItemStack is = Items.build(target, art);
                    inv.setItem(bonus + ARTIFACT_SLOTS.get(i), is);
                }
            }

        });
    }

    public static ItemStack getEmptyArtifactIcon() {
        var is = Icon.ARTIFACT.clone();
        var meta = is.getItemMeta();
        meta.setDisplayName("§a§lDi vật trống");
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack getArtifactIcon() {
        var is = new ItemStack(Material.LAPIS_LAZULI);
        var meta = is.getItemMeta();
        meta.setCustomModelData(34);
        meta.setDisplayName("§a§lDi vật");
        meta.setLore(List.of("§f§oClick để mở menu"));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack getSlaveIcon() {
        var is = new ItemStack(Material.SKELETON_SKULL);
        var meta = is.getItemMeta();
        meta.setDisplayName("§a§lBạn đồng hành");
        meta.setLore(List.of("§f§oClick để mở menu"));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack getFapsuIcon() {
        var is = new ItemStack(Material.ENCHANTING_TABLE);
        var meta = is.getItemMeta();
        meta.setDisplayName("§a§lFap sư");
        meta.setLore(List.of("§f§oClick để mở menu"));
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack getStatIcon(Player player) {
        Traveler t = Travelers.get(player);
        List<String> lore = Lists.newArrayList();
        for (Stat stat : Stat.values()) {
            int point = t.getState().getStat(player, stat);
            double value = Utils.round(Travelers.getStatValue(player, stat));
            lore.add("§c" + stat.getName() + ": §f" + point + " (" + value + ")");
        }

        ItemStack is = Icon.INFO.clone();
        ItemStackUtils.setDisplayName(is, "§6§lChỉ số");
        ItemStackUtils.setLore(is, lore);
        return is;
    }

    public static ItemStack getDataIcon(Player player) {
        Traveler t = Travelers.get(player);
        TravelerData td = t.getData();
        List<String> lore = Lists.newArrayList();
        lore.add("§eLực chiến: §f" + Utils.calPower(player));
        lore.add("§eCấp độ: §f" + player.getLevel());
        lore.add("§eKinh nghiệm: §f" + td.getExp() + " (" + Utils.round(player.getExp()) + "%)");
        lore.add("§eCấp bậc: §f" + td.getGrade());

        ItemStack is = Icon.SKILL.clone();
        ItemStackUtils.setDisplayName(is, "§6§lThông tin");
        ItemStackUtils.setLore(is, lore);
        return is;
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof TIGHolder)) return;
        e.setCancelled(true);
        var holder = (TIGHolder) e.getInventory().getHolder();
        var player = (Player) e.getWhoClicked();
        if (holder.getOwner() != null) {
            int slot = e.getSlot();
            if (slot == 3) {
                // Artifact
                ArtifactGUI.open(player);
            }
            else if (slot == 4) {
                // Slave
                SlaveSelectGUI.open(player);
            }
            else if (slot == 8) {
                // Fapsu
                ModifyGUI.open(player);
            }
        }

    }

    public static void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof TIGHolder) e.setCancelled(true);
    }

}

class TIGHolder implements InventoryHolder {

    private Player owner;

    public TIGHolder() {}

    public TIGHolder(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }


}
