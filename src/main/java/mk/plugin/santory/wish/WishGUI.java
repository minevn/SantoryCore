package mk.plugin.santory.wish;

import com.google.common.collect.Lists;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class WishGUI {

    public static void open(Player player, String id) {
        var wish = Configs.getWish(id);
        int amount = 0;
        for (Tier t : Tier.values()) {
            if (wish.getRewards().containsKey(t)) {
                amount += wish.getRewards().get(t).getItems().size();
            }
        }
        int size = amount % 9 == 0 ? amount : (amount / 9 + 1) * 9;
        var inv = Bukkit.createInventory(new WishItemGUIHolder(), size, "§0§lXEM PHẦN THƯỞNG");
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        Tasks.async(() -> {
            List<ItemStack> items = Lists.newArrayList();
            for (Tier t : Tier.values()) {
                if (wish.getRewards().containsKey(t)) {
                    items.addAll(wish.getRewards().get(t).getItems().stream().map(WishRewardItem::getIcon).collect(Collectors.toList()));
                }
            }
            for (int i = 0; i < items.size(); i++) inv.setItem(i, items.get(i));
        });
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof WishItemGUIHolder) e.setCancelled(true);
    }

}

class WishItemGUIHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
