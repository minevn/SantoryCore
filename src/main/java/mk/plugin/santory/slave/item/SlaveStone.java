package mk.plugin.santory.slave.item;

import com.google.common.collect.Lists;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SlaveStone {

    I(1, 19, 150, -1, Tier.COMMON),
    II(2, 39, 250, -1, Tier.UNCOMMON),
    III(3, 59, 400, -1, Tier.RARE),
    IV(4, 79, 650, -1, Tier.EPIC),
    V(5, 100, 1000, -1, Tier.LEGEND);

    private final Material MATERIAL = Material.BAKED_POTATO;

    private Tier tier;
    private int value;
    private int maxLevel;
    private int exp;
    private int model;

    private SlaveStone(int value, int maxLevel, int exp, int model, Tier tier) {
        this.value = value;
        this.maxLevel = maxLevel;
        this.exp = exp;
        this.model = model;
        this.tier = tier;
    }

    public int getValue() {
        return value;
    }

    public int getMinLevel() {
        if (this == SlaveStone.I) return 0;
        return valueOf(this.getValue() - 1).getMaxLevel() + 1;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getExp() {
        return exp;
    }

    public int getModel() {
        return model;
    }

    public static SlaveStone valueOf(int i) {
        for (SlaveStone t : values()) {
            if (t.getValue() == i) return t;
        }
        return null;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(MATERIAL);
        ItemMeta meta = is.getItemMeta();
        if (this.getModel() != -1) meta.setCustomModelData(this.getModel());
        meta.setDisplayName(this.tier.getColor() + "§lViên đá linh hồn");
        meta.setLore(Lists.newArrayList("§7§oCông dụng tăng exp cho Bạn đồng hành (chuột phải)", "§7§oDùng cho cấp từ " + this.getMinLevel() + " đến " + this.getMaxLevel()));
        is.setItemMeta(meta);
        ItemStackUtils.setTag(is, "slave-stone", this.name());

        return is;
    }

    public static boolean is(ItemStack is) {
        return ItemStackUtils.hasTag(is, "slave-stone");
    }

    public static SlaveStone parse(ItemStack is) {
        String s = ItemStackUtils.getTag(is, "slave-stone");
        return SlaveStone.valueOf(s);
    }

}
