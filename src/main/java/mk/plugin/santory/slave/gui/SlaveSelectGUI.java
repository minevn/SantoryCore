package mk.plugin.santory.slave.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.slave.SlaveData;
import mk.plugin.santory.slave.SlaveModel;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.slave.master.Master;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlaveSelectGUI {

    private static final Map<Player, Long> lastSummon = Maps.newHashMap();
    private static final int DELAY = 5;

    public static void open(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
        Master m = Masters.get(player);
        List<Slave> slaves = sort(m.getSlaves());
        Inventory inv = Bukkit.createInventory(new Holder(slaves), 18, "§0§lBẠN ĐỒNG HÀNH");
        player.openInventory(inv);

        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
            SlaveSelectGUI.update(m, slaves, inv);
        });
    }

    public static void update(Master m, List<Slave> slaves, Inventory inv) {
        for (int i = 0 ; i < slaves.size() ; i++) {
            Slave slave = slaves.get(i);
            boolean current = m.getCurrentSlave() != null && m.getCurrentSlave().equals(slave);
            ItemStack is = getIcon(slave, current);
            inv.setItem(i, is);
        }
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() instanceof Holder == false) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;

        Holder h = (Holder) e.getInventory().getHolder();
        int slot = e.getSlot();
        if (slot >= h.getSlaves().size()) return;

        Inventory inv = e.getClickedInventory();
        Player player = (Player) e.getWhoClicked();
        ClickType ct = e.getClick();
        List<Slave> slaves = h.getSlaves();
        Slave slave = slaves.get(slot);
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);

        if (ct == ClickType.LEFT) {
            SlaveInfoGUI.open(player, slaves.get(slot));
            return;
        }

        if (lastSummon.containsKey(player)) {
            if (lastSummon.get(player) > System.currentTimeMillis()) {
                player.sendMessage("§aThời gian giãn cách mỗi lần thực thi là " + DELAY + " giây");
                return;
            }
        }
        lastSummon.put(player, System.currentTimeMillis() + DELAY * 1000);
        if (ct == ClickType.RIGHT) {
            Master m = Masters.get(player);
            Slaves.summonSlave(player, slave.getID());
            if (Slaves.isDead(slave.getID())) {
                player.sendMessage("§aBạn đồng hành vừa hi sinh, sẽ tự động hồi sinh sau 1 lúc nữa");
            } else player.sendMessage("§aHoàn thành!");
            Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
                SlaveSelectGUI.update(m, slaves, inv);
            });

        }
        else if (ct == ClickType.SHIFT_RIGHT) {
            Slaves.despawnCurrentSlave(player);
            Master m = Masters.get(player);
            m.setCurrentSlave(null);
            player.sendMessage("§cHoàn thành!");
            Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
                SlaveSelectGUI.update(m, slaves, inv);
            });
        }

    }

    public static void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Holder) e.setCancelled(true);
    }

    public static List<Slave> sort(List<Slave> source) {
        return source.stream().sorted(new Comparator<Slave>() {
            @Override
            public int compare(Slave o1, Slave o2) {
                int n1 = o1.getModel().getTier().getNumber();
                int n2 = o2.getModel().getTier().getNumber();
                if (n1 > n2) return -1;
                if (n1 == n2) return 0;
                return 1;
            }
        }).collect(Collectors.toList());
    }

    public static ItemStack getIcon(Slave slave, boolean current) {
        SlaveModel model = slave.getModel();
        SlaveData data = slave.getData();
        ItemStack is = Utils.buildSkull(slave.getModel().getHead());
        String name = model.getTier().getColor() + "§l" + model.getName();
        if (current) name = "~" + name;
        ItemStackUtils.setDisplayName(is, name);
        List<String> lore = Lists.newArrayList();
        lore.add(Utils.toStars(data.getAscent()));
        lore.add("§f§oClick Trái để xem thông tin");
        lore.add("§f§oClick Phải để triệu hồi");
        lore.add("§f§oClick Shift Phải để ngừng triệu hồi");

        ItemStackUtils.setLore(is, lore);

        if (current) ItemStackUtils.addEnchantEffect(is);

        return is;
    }

}

class Holder implements InventoryHolder {

    private List<Slave> slaves;

    public Holder(List<Slave> slaves) {
        this.slaves = slaves;
    }

    public List<Slave> getSlaves() {
        return slaves;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
