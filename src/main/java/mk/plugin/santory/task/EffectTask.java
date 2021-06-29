package mk.plugin.santory.task;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mk.plugin.santory.effect.EffectData;
import mk.plugin.santory.main.SantoryCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class EffectTask extends BukkitRunnable {

    public static EffectTask start() {
        var et = new EffectTask();
        et.runTaskTimer(SantoryCore.get(), 0, 10);
        return et;
    }

    // Move speed
    private Map<String, EffectData> speedPendings;
    private Map<String, Float> originalSpeed;
    private Map<String, Long> speedChanging;

    public EffectTask() {
        this.speedPendings = Maps.newHashMap();
        this.originalSpeed = Maps.newHashMap();
        this.speedChanging = Maps.newHashMap();
    }

    public void addSpeedChange(Player p, EffectData ed) {
        speedPendings.put(p.getName(), ed);
    }

    @Override
    public void run() {
        /*
        Move speed
         */
        // Check changing
        for (String pn : Sets.newHashSet(speedChanging.keySet())) {
            var l = speedChanging.get(pn);
            if (l <= System.currentTimeMillis()) {
                speedChanging.remove(pn);
                var p = Bukkit.getPlayer(pn);
                if (p != null) {
                    p.setWalkSpeed(originalSpeed.get(pn));
                    originalSpeed.remove(pn);
                }
            }
        }

        // Check pending
        for (String pn : Sets.newHashSet(speedPendings.keySet())) {
            var p = Bukkit.getPlayer(pn);
            if (p == null) {
                speedChanging.remove(pn);
                continue;
            }
            if (speedChanging.containsKey(pn)) continue;

            // Change
            var ed = speedPendings.get(pn);
            originalSpeed.put(pn, p.getWalkSpeed());
            speedChanging.put(pn, System.currentTimeMillis() + ed.getDuration());

            p.setWalkSpeed(Double.valueOf(ed.getValue()).floatValue());
        }

        /*
        Things
         */
    }
}
