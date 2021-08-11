package mk.plugin.santory.skin.listener;

import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.skin.system.PlayerSkin;
import mk.plugin.santory.skin.system.PlayerSkins;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.EulerAngle;

public class SkinListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        var p = e.getPlayer();
        var equips = PlayerSkins.getEquips(p);
        if (equips == null) return;

        // Rotate armorstand
        Tasks.async(() -> {
            for (Entity entity : equips) {
                if (entity instanceof ArmorStand) {
                    var a = (ArmorStand) entity;
                    a.setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();
        var equips = PlayerSkins.getEquips(p);
        if (equips == null) return;

        PlayerSkins.destroy(p);
        // Re-equip
        Tasks.sync(() -> {
            PlayerSkins.equip(p);
        }, 5);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        var p = e.getPlayer();
        Tasks.sync(() -> {
            PlayerSkins.equip(p);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();

        Tasks.sync(() -> {
            PlayerSkins.equip(p);
        }, 100);
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) {
        var p = e.getEntity();
        var equips = PlayerSkins.getEquips(p);
        if (equips == null) return;

        PlayerSkins.destroy(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var equips = PlayerSkins.getEquips(p);
        if (equips == null) return;

        PlayerSkins.destroy(p);
    }

    private double getPlayerFacingPI(Player p) {
        double deg = 0;
        double fin = 0;
        double x = p.getLocation().getDirection().getX();
        double z = p.getLocation().getDirection().getZ();

        if(x > 0.0 && z >0.0) {
            deg = x;
            fin = (Math.PI/2)*deg;
        }
        if(x> 0.0 && z<0.0) {
            deg = z*(-1);
            fin = ((Math.PI/2)*deg)+(Math.PI/2);
        }
        if(x<0.0 && z<0.0) {
            deg = x*(-1);
            fin = (Math.PI/2)*deg+ Math.PI;
        }
        if(x<0.0 && z>0.0) {
            System.out.println("SW");
            deg = z;
            fin = (Math.PI/2)*deg+(Math.PI/2 *3);
        }
        return fin;
    }

    public static void illegalCheck() {
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand) {
                    var a = (ArmorStand) entity;
                    var is1 = a.getEquipment().getItemInMainHand();
                    var is2 = a.getEquipment().getItemInOffHand();
                    if (Skins.read(is1) != null || Skins.read(is2) != null) {
                        if (PlayerSkins.isIllegal(a)) {
                            count++;
                            Tasks.sync(a::remove);
                        }
                    }
                }
            }
        }
        if (count > 0) {
            SantoryCore.get().getLogger().warning("Removed " + count + " illegal armorstands");
        }
    }

}
