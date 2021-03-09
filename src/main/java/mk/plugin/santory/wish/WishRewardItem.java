package mk.plugin.santory.wish;

import mk.plugin.santory.tier.Tier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WishRewardItem {

    private Tier tier;
    private WishRewardItemType type;
    private String value;

    public WishRewardItem(Tier tier, WishRewardItemType type, String value) {
        this.tier = tier;
        this.type = type;
        this.value = value;
    }

    public Tier getTier() {
        return tier;
    }

    public WishRewardItemType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public ItemStack getIcon() {
        return this.type.getIcon(this.value);
    }

    public void give(Player player) {
        this.type.give(value, player);
    }

    public static WishRewardItem parse(Tier tier, String s) {
        String ts = s.split(" ")[0].toUpperCase();
        String value = s.split(" ")[1];
        return new WishRewardItem(tier, WishRewardItemType.valueOf(ts), value);
    }

}
