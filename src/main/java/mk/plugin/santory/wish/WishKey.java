package mk.plugin.santory.wish;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WishKey {

    private List<String> wishes;
    private ItemStack itemStack;

    public WishKey(List<String> wishes, ItemStack itemStack) {
        this.wishes = wishes;
        this.itemStack = itemStack;
    }

    public List<String> getWishes() {
        return wishes;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }
}
