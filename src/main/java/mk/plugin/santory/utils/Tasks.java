package mk.plugin.santory.utils;

import mk.plugin.santory.main.SantoryCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Tasks {

    public static void sync(Runnable r) {
        Bukkit.getScheduler().runTask(SantoryCore.get(), r);
    }

    public static void sync(Runnable r, int later) {
        Bukkit.getScheduler().runTaskLater(SantoryCore.get(), r, later);
    }

    public static void sync(Runnable r, int later, int interval) {
        Bukkit.getScheduler().runTaskTimer(SantoryCore.get(), r, later, interval);
    }

    public static void sync(Runnable r, int later, int interval, int times) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if (i > times) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimer(SantoryCore.get(), later, interval);
    }

    public static void sync(Runnable r, int later, int interval, long period) {
        long start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= period) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimer(SantoryCore.get(), later, interval);
    }

    public static void async(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), r);
    }

    public static void async(Runnable r, int later) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SantoryCore.get(), r, later);
    }

    public static void async(Runnable r, int later, int interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SantoryCore.get(), r, later, interval);
    }

    public static void async(Runnable r, int later, int interval, int times) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if (i > times) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), later, interval);
    }

    public static void async(Runnable r, int later, int interval, long period) {
        long start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= period) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), later, interval);
    }

}
