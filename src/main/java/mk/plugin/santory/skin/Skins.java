package mk.plugin.santory.skin;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.ItemStackManager;
import org.bukkit.inventory.ItemStack;

public class Skins {

    public static final String TAG = "santory.skin";

    public static Skin read(ItemStack is) {
        if (is == null) return null;
        var ism = new ItemStackManager(SantoryCore.get(), is);
        if (!ism.hasTag(TAG)) return null;
        return Configs.getSkin(ism.getTag(TAG));
    }

    public static ItemStack build(String id) {
        return Configs.getSkin(id).build();
    }

}
