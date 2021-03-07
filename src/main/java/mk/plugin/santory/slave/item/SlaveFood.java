package mk.plugin.santory.slave.item;

import com.google.common.collect.Lists;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SlaveFood {

    I(Tier.UNCOMMON, -1, 10),
    II(Tier.RARE, -1, 20),
    III(Tier.EPIC,-1, 30);

    private final Material MATERIAL = Material.BAKED_POTATO;

    private Tier tier;
    private int heal;

    // -1 = no model
    private int model;

    private SlaveFood(Tier tier, int model, int heal) {
        this.tier = tier;
        this.model = model;
        this.heal = heal;
    }

    public Tier getTier() {
        return this.tier;
    }

    public int getModel() {
        return this.model;
    }

    public int getHeal() {
        return this.heal;
    }

    public ItemStack build() {
        ItemStack is = new ItemStack(MATERIAL);
        ItemMeta meta = is.getItemMeta();
        if (this.getModel() != -1) meta.setCustomModelData(this.getModel());
        meta.setDisplayName(this.getTier().getColor() + "§lĐồ ăn xịn");
        meta.setLore(Lists.newArrayList("§7Dùng cho bạn đồng hành (chuột phải)"));
        is.setItemMeta(meta);

        return ItemStackUtils.setTag(is, "slave-food", this.name());
    }

    public static boolean is(ItemStack is) {
        return ItemStackUtils.hasTag(is, "slave-food");
    }

    public static SlaveFood parse(ItemStack is) {
        String s = ItemStackUtils.getTag(is, "slave-food");
        return SlaveFood.valueOf(s);
    }

}
