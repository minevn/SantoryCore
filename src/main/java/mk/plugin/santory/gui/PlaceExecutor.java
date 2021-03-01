package mk.plugin.santory.gui;

import org.bukkit.entity.Player;

public interface PlaceExecutor {
	
	void execute(Player player, int slot, GUIStatus status);
	
}
