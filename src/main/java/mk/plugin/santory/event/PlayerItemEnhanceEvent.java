package mk.plugin.santory.event;

import org.bukkit.entity.Player;

public class PlayerItemEnhanceEvent extends PlayerItemModifyEvent {

    public PlayerItemEnhanceEvent(Player who, boolean isSuccess, int previous, int after) {
        super(who, isSuccess, previous, after);
    }

}
