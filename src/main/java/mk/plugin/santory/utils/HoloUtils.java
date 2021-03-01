package mk.plugin.santory.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import mk.plugin.santory.main.SantoryCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class HoloUtils {

    public static void hologram(String text, Location l, int tick) {
        Hologram h = HologramsAPI.createHologram(SantoryCore.get(), l);
        h.appendTextLine(text.replace("&", "§"));
        Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
            h.delete();
        }, tick);
    }

    public static void hologram(List<String> texts, Location l, int tick) {
        Hologram h = HologramsAPI.createHologram(SantoryCore.get(), l);
        for (String text : texts) {
            h.appendTextLine(text.replace("&", "§"));
        }
        Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
            h.delete();
        }, tick);
    }

}
