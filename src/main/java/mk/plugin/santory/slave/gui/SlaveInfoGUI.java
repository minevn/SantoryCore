package mk.plugin.santory.slave.gui;

import com.google.common.collect.Lists;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.weapon.Weapon;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.slave.SlaveData;
import mk.plugin.santory.slave.SlaveModel;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.traveler.TravelerOptions;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
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

import java.util.List;

public class SlaveInfoGUI {

    private static final int INFO_SLOT = 2;
    private static final int SKILL_SLOT = 4;
    private static final int WEAPON_SLOT = 6;

    public static void open(Player player, Slave slave) {
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
        boolean hasWeapon = slave.getData().getWeapon() != null;
        Inventory inv = Bukkit.createInventory(new HolderB(hasWeapon, slave), 9, "§0§lBẠN ĐỒNG HÀNH");
        player.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
            for (int i = 0 ; i < inv.getSize() ; i++) inv.setItem(i, Utils.getBlackSlot());
            inv.setItem(INFO_SLOT, getInfoIcon(slave));
            inv.setItem(SKILL_SLOT, getSkillIcon(slave));
            if (!hasWeapon) inv.setItem(WEAPON_SLOT, getEmptyWeaponIcon(slave));
            else {
               ItemStack is = Items.build(player, Item.parse(slave.getData().getWeapon()));
               inv.setItem(WEAPON_SLOT, is);
           }
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() instanceof HolderB == false) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;

        int slot = e.getSlot();
        if (slot != WEAPON_SLOT) return;

        ItemStack is = e.getCursor();
        Inventory inv = e.getClickedInventory();
        ItemStack current = e.getCurrentItem();
        HolderB hb = (HolderB) inv.getHolder();
        Player player = (Player) e.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);

        if (!Items.is(is)) {
            if (!Items.is(current)) return;
            inv.setItem(WEAPON_SLOT, getEmptyWeaponIcon(hb.getSlave()));
            e.setCursor(current);
        }
        else {
            Item item = Items.read(is);
            if (item.getModel().getType() != ItemType.WEAPON) return;
            if (Weapon.parse(item.getModel()).getType() != hb.getSlave().getModel().getWeaponType()) {
                e.getWhoClicked().sendMessage("§cKhông đúng loại vũ khí");
                return;
            }
            inv.setItem(WEAPON_SLOT, is);
            if (hb.hasWeapon()) e.setCursor(current);
            else e.setCursor(null);
        }

    }

    public static void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof HolderB == false) return;
        ItemStack is = e.getInventory().getItem(WEAPON_SLOT);
        HolderB hb = (HolderB) e.getInventory().getHolder();

        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
            if (Items.is(is)) {
                Item item = Items.read(is);
                hb.getSlave().getData().setWeapon(item.toString());
            }
            else {
                hb.getSlave().getData().setWeapon(null);
            }
            Masters.save((Player) e.getPlayer());
            Slaves.update(hb.getSlave().getID());
        });

        Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
           SlaveSelectGUI.open((Player) e.getPlayer());
        });
    }

    public static ItemStack getInfoIcon(Slave slave) {
        SlaveData data = slave.getData();
        SlaveModel model = slave.getModel();
        ItemStack is = Icon.INFO.clone();
        int exp = data.getExp();
        double rate = (double) (exp - TravelerOptions.getTotalExpTo(data.getLevel())) / (double) TravelerOptions.getExpOf(data.getLevel() + 1);

        ItemStackUtils.setDisplayName(is, "§a§lThông tin");
        List<String> lore = Lists.newArrayList();
        lore.add("§aTên: §f" + model.getName());
        lore.add("§aHạng: §f" + model.getTier().getName());
        lore.add("§aĐột phá: §f" + data.getAscent().name());
        lore.add("§aCấp độ: §f" + data.getLevel());
        lore.add("§aK.nghiệm: §f" + exp + " (" + Utils.round(rate * 100) + "%)");

        ItemStackUtils.setLore(is, lore);

        return is;
    }

    public static ItemStack getSkillIcon(Slave slave) {
        SlaveModel model = slave.getModel();
        ItemStack is = Icon.SKILL.clone();
        ItemStackUtils.setDisplayName(is, "§a§lKỹ năng: §c§l" + model.getSkill().getName());
        ItemStackUtils.setLore(is, model.getSkillDesc());

        return is;
    }

    public static ItemStack getEmptyWeaponIcon(Slave slave) {
        ItemStack is = Icon.ITEM.clone();
        ItemStackUtils.setDisplayName(is, "§f§l§oVũ khí trống (" + slave.getModel().getWeaponType().getName() + ")");
        ItemStackUtils.setLore(is, Lists.newArrayList("§7§oĐặt vũ khí vào đây"));

        return is;
    }
}

class HolderB implements InventoryHolder {

    private Slave slave;
    private boolean hasWeapon;

    public HolderB(boolean hasWeapon, Slave slave) {
        this.hasWeapon = hasWeapon;
        this.slave = slave;
    }

    public Slave getSlave() {
        return slave;
    }

    public boolean hasWeapon() {
        return hasWeapon;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}