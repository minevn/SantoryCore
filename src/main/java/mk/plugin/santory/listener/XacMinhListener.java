package mk.plugin.santory.listener;

import mk.plugin.santory.traveler.Travelers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import remvn.reanticheatspigot.event.PlayerCheckedEvent;

public class XacMinhListener implements Listener {

    @EventHandler
    public void onXM(PlayerCheckedEvent e) {
        Player player = e.getPlayer();
        Travelers.addHackChecked(player.getName());
        player.sendTitle("§b§l§oĐÃ XÁC MINH", "§fNhận buff và tích xanh", 20, 40, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Travelers.removeHackChecked(player.getName());
    }

}
