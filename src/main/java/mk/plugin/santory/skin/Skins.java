package mk.plugin.santory.skin;

import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Skins {

    public static final String TAG = "santory.skin";

    public static Skin of(Item item) {
        if (item == null) return null;
        return new Skin(SkinType.valueOf(item.getModel().getMetadata().get("skin-type")));
    }

    public static Skin read(ItemStack is) {
        if (is == null) return null;
        var item = Items.read(is);
        if (item == null) return null;
        if (item.getModel().getType() != ItemType.SKIN) return null;
        return new Skin(SkinType.valueOf(item.getModel().getMetadata().get("skin-type")));
    }

    public static int getBuff(Player player) {
        int b = 0;
        // Head
        var is = player.getInventory().getHelmet();
        var skin = read(is);
        if (skin != null) {
            var item = Items.read(is);
            b += Items.getAscentValue(item);
        }

        // Offhand
        is = player.getInventory().getItemInOffHand();
        skin = read(is);
        if (skin != null) {
            var item = Items.read(is);
            b += Items.getAscentValue(item);
        }

        return b;
    }

}
