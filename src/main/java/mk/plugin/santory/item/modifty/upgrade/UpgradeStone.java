package mk.plugin.santory.item.modifty.upgrade;

import com.google.common.collect.Lists;
import mk.plugin.santory.element.Element;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.ItemStackManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UpgradeStone {

    FLAME(6),
    WATER(7),
    EARTH(8),
    PLANT(9),

    LIGHT(10),
    DARKNESS(11);

    private final Material m = Material.IRON_NUGGET;
    private final int modelData;

    UpgradeStone(int modelData) {
        this.modelData = modelData;
    }

    public Element getElement() {
        return Element.valueOf(this.name());
    }

    public ItemStack build() {
        var is = new ItemStack(this.m);
        var ism = new ItemStackManager(SantoryCore.get(), is);
        ism.setModelData(this.modelData);
        ism.setName(this.getElement().getColor() + "§lĐá Nguyên tố " + this.getElement().getName());
        ism.setLore(Lists.newArrayList("§f§oDùng để nâng bậc những trang bị", "§f§ocó nguyên tố là " + this.getElement().getName()));
        ism.setTag("upgradestone", this.name());

        return is;
    }

    public static UpgradeStone read(ItemStack is) {
        var ism = new ItemStackManager(SantoryCore.get(), is);
        if (!ism.hasTag("upgradestone")) return null;
        return UpgradeStone.valueOf(ism.getTag("upgradestone"));
    }

}
