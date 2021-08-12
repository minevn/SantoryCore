package mk.plugin.santory.skin.listener;

import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.skin.system.PlayerSkins;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;

public class SkinTeleportListener implements Listener {

    private static Map<String, Long> delays;

    public SkinTeleportListener() {
        delays = Maps.newHashMap();
    }

    public static boolean isDelayed(Player p) {
        return delays.getOrDefault(p.getName(), 0L) > System.currentTimeMillis();
    }

    public static void delay(Player p, int tick) {
        delays.put(p.getName(), System.currentTimeMillis() + (tick * 50L));
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        var p = e.getPlayer();
        var cmd = e.getMessage().replace("/", "");

        for (String prefix : Configs.getTeleportCommands()) {
            if (cmd.startsWith(prefix)) {
                // Noti
                p.sendMessage("§aBạn vừa xài lệnh liên quan đến dịch chuyển");
                p.sendMessage("§aTự động §ftắt skin khoảng 10 giây§a, §ckhông thể dịch chuyển §akhi mặc skin");

                // Off skin
                PlayerSkins.doBeforeTeleport(p, 200);

                break;
            }
        }

    }


















}
