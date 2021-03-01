package mk.plugin.santory.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlaceChecker {
	
	boolean place(Player player, ItemStack is, GUIStatus status);
	
}
