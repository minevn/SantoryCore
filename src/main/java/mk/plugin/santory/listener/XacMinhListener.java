package mk.plugin.santory.listener;

import mk.plugin.santory.main.SantoryCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import remvn.reanticheatspigot.event.PlayerCheckedEvent;

public class XacMinhListener implements Listener {

    @EventHandler
    public void onXM(PlayerCheckedEvent e) {
        Player player = e.getPlayer();
        player.setMetadata("santory-xacminh", new FixedMetadataValue(SantoryCore.get(), ""));
        player.sendTitle("§b§l§oĐÃ XÁC MINH", "§fNhận buff và tích xanh", 20, 40, 20);

    }

}
