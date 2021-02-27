package mk.plugin.santory.gui;

import org.bukkit.inventory.ItemStack;

public interface AmountChecker {
	
	public boolean allowMulti(ItemStack is);
	
}
