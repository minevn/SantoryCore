package mk.plugin.santory.event;

import org.bukkit.entity.Player;

public class PlayerItemAscentEvent extends PlayerItemModifyEvent {

    public PlayerItemAscentEvent(Player who, boolean isSuccess) {
        super(who, isSuccess);
    }

}
