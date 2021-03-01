package mk.plugin.santory.gui;

import org.bukkit.inventory.ItemStack;

public interface AmountChecker {
	
	boolean allowMulti(ItemStack is);
	
}
