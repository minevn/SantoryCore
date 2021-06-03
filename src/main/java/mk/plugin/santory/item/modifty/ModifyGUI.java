package mk.plugin.santory.item.modifty;

import com.google.common.collect.Lists;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.gui.GUI;
import mk.plugin.santory.gui.GUIs;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
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

import java.util.List;

public class ModifyGUI {

    private static final int ENHANCE_SLOT = 2;
    private static final int UPGRADE_SLOT = 4;
    private static final int ASCENT_SLOT = 6;

    public static void open(Player player) {
        var inv = Bukkit.createInventory(new Holder(), 9, "§0§lFAP SƯ SORASKY");
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
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
        e.setCancelled(true);

        var player = (Player) e.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);

        int slot = e.getSlot();
        switch (slot) {
            case ENHANCE_SLOT:
                if (!Configs.checkPermission(player, "enhance")) return;
                GUIs.open(player, GUI.ENHANCE);
                break;
            case UPGRADE_SLOT:
                if (!Configs.checkPermission(player, "upgrade")) return;
                GUIs.open(player, GUI.UPGRADE);
                break;
            case ASCENT_SLOT:
                if (!Configs.checkPermission(player, "ascent")) return;
                GUIs.open(player, GUI.ASCENT);
                break;
        }
    }

    private static ItemStack getEnhanceIcon() {
        var is = Icon.ENHANCE.clone();
        List<String> desc = Lists.newArrayList();
        desc.add("§a§lCường hóa cho trang bị");
        desc.add("§f§o- §a§oTăng chỉ số §f§ocho trang bị");
        desc.add("§f§o- Nguyên liệu bắt buộc: Đá cường hóa");
        desc.add("§f§o- Bùa may mắn giúp không làm giảm cấp khi cường hóa thất bại");
        desc.add("§f§o  đồng thời cũng làm tăng tỷ lệ thành công");
        desc.add("§f§o- Khi §c§obị giới hạn cấp cường hóa§f§o, bạn cần §a§oNâng bậc §f§ocho trang bị");
        desc.add("§f§o  để gia tăng giới hạn cường hóa");

        ItemStackUtils.setDisplayName(is, desc.get(0));
        desc.remove(0);
        ItemStackUtils.setLore(is, desc);

        return is;
    }

    private static ItemStack getUpgradeIcon() {
        var is = Icon.UPGRADE.clone();
        List<String> desc = Lists.newArrayList();
        desc.add("§a§lNâng bậc cho trang bị");
        desc.add("§f§o- Để §a§otăng giới hạn cấp cường hóa §f§ocho trang bị");
        desc.add("§f§o- Nguyên liệu bắt buộc: Đá nâng bậc");
        desc.add("§f§o- Yêu cầu số lượng đá đặt vào bằng số lượng bùa");
        desc.add("§f§o  hoặc không có bùa");

        ItemStackUtils.setDisplayName(is, desc.get(0));
        desc.remove(0);
        ItemStackUtils.setLore(is, desc);

        return is;
    }

    private static ItemStack getAscentIcon() {
        var is = Icon.ASCENT.clone();
        List<String> desc = Lists.newArrayList();
        desc.add("§a§lĐột phá cho trang bị");
        desc.add("§f§o- Để §a§oghép những trang bị giống nhau §f§othành 1");
        desc.add("§f§o- Nguyên liệu bắt buộc: Trang bị cùng loại");
        desc.add("§f§o- Đột phá sẽ tăng cho trang bị chính, vì vậy");
        desc.add("§f§o  §a§oPHẢI ĐẶT TRANG BỊ BẠN MUỐN ĐỘT PHÁT ĐẦU TIÊN");
        desc.add("§f§o  rồi mới đến nguyên liệu đột phát");
        desc.add("§f§o- Đối với §a§oDi vật, mỗi khi đột phá sẽ được");
        desc.add("§f§o  §a§ogia tăng ngẫu nhiên một loại chỉ số");

        ItemStackUtils.setDisplayName(is, desc.get(0));
        desc.remove(0);
        ItemStackUtils.setLore(is, desc);
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