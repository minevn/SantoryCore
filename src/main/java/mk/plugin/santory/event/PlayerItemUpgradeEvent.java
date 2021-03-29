package mk.plugin.santory.event;

import org.bukkit.entity.Player;

public class PlayerItemUpgradeEvent extends PlayerItemModifyEvent {

    public PlayerItemUpgradeEvent(Player who, boolean isSuccess) {
        super(who, isSuccess);
    }

}
