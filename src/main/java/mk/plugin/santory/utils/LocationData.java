package mk.plugin.santory.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationData {

    private String world;
    private double x;
    private double y;
    private double z;

    public LocationData(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static LocationData parse(String s) {
        String[] a = s.split(";");
        return new LocationData(a[0], Double.parseDouble(a[1]), Double.parseDouble(a[2]), Double.parseDouble(a[3]));
    }

}
