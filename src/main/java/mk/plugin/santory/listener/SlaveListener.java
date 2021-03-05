package mk.plugin.santory.listener;

import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.*;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.TravelerOptions;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SlaveListener implements Listener {

    private final int HEALTH_MIN = 15;

    /*
    Make slave target as master
     */
    @EventHandler(ignoreCancelled = true)
    public void onMasterDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player == false) return;
        if (e.getEntity() instanceof LivingEntity == false) return;
        Player player = (Player) e.getDamager();
        if (!Slaves.hasSlave(player)) return;
        if (Slaves.isMaster(player, e.getEntity())) return;
        if (!Slaves.isSlaveAlive(player)) return;

        String id = Slaves.getSlave(player);
        Husk se = Slaves.getSlaveEntity(player);

        if (Slaves.isInSameWorld(id) && Slaves.getDistanceVsMaster(id) > 10) return;
        if (Slaves.isHealthLowerThan((LivingEntity) se, HEALTH_MIN)) return;
        se.setTarget((LivingEntity) e.getEntity());
        Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
            Slaves.stateCall(id, SlaveState.TARGET);
        }, 30);

    }

    /*
    Master damaged
    */
    @EventHandler
    public void onMasterDamaged(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player == false) return;
        if (e.getDamager() instanceof LivingEntity == false) return;

        LivingEntity entity = (LivingEntity) e.getDamager();
        Player player = (Player) e.getEntity();
        if (!Slaves.hasSlave(player)) return;
        if (Slaves.isMaster(player, entity)) {
            e.setCancelled(true);
            return;
        }
    }


    /*
    Slave target control
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent e) {
        Entity entity = e.getEntity();
        if (e.getTarget() == null) return;
        if (!Slaves.isSlave(entity)) return;

        String id = Slaves.getSlave(entity).getID();

        // Check if targeting master
        Husk husk = (Husk) entity;
        LivingEntity target = e.getTarget();
        if (target instanceof Player) {
            Player player = (Player) target;
            if (Slaves.isMaster(player, husk)) {
                if (Slaves.isInState(id, SlaveState.FOLLOW)) return;
                else e.setCancelled(true);
                return;
            }
        }
    }

    /*
    Slave damage and kill
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        if (!Slaves.isSlave(entity)) return;

        // Damaged
        String id = Slaves.getSlave(entity).getID();
        Slaves.stateCall(id, SlaveState.ATTACK);
        double damage = Stat.DAMAGE.pointsToValue(Slaves.getStats(id).getOrDefault(Stat.DAMAGE, 1));
        e.setDamage(damage);

        // Death
        Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
            if (!e.getEntity().isDead()) return;
            Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
                Slaves.stateCall(id, SlaveState.KILL);
            }, 40);
        });
    }

    /*
    Health < 15%  -> Run
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamagedByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!Slaves.isSlave(entity)) return;

        // Damaged
        String id = Slaves.getSlave(entity).getID();
        Slaves.stateCall(id, SlaveState.DAMAGED);

        // Run away
        if (Slaves.isHealthLowerThan((LivingEntity) entity, HEALTH_MIN)) {
            Slaves.stateCall(id, SlaveState.RUNAWAY);
            Slaves.backtoMaster(id, false);
        }

        // Master beaten
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            if (Slaves.isMaster(player, entity)) {
                e.setCancelled(true);
                Slaves.lookAt((LivingEntity) entity, player, 2);
                Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
                   SlaveAnimation.ANGERY.play((LivingEntity) entity, player);
                }, 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamagedByBlock(EntityDamageByBlockEvent e) {
        Entity entity = e.getEntity();
        if (!Slaves.isSlave(entity)) return;

        String id = Slaves.getSlave(entity).getID();
        Slaves.stateCall(id, SlaveState.DAMAGED);
    }

    /*
    Death
     */
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        if (!Slaves.isSlave(entity)) return;

        String id = Slaves.getSlave(entity).getID();
        Slaves.stateCall(id, SlaveState.DEATH);
        Slaves.setDeath(id);

        SlaveAnimation.DEATH.play((LivingEntity) entity, Slaves.getMasterPlayer(id));
    }

    /*
    Right click at slave
     */
    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return; // off hand packet, ignore.

        Entity entity = e.getRightClicked();
        if (!Slaves.isSlave(entity)) return;

        Player player = e.getPlayer();
        String id = Slaves.getSlave(entity).getID();

        if (Slaves.isMaster(player, entity)) {
            e.setCancelled(true);

            ItemStack is = player.getInventory().getItemInMainHand();

            // Feed
            if (SlaveFood.is(is)) {
                SlaveFood food = SlaveFood.parse(is);
                if (Slaves.feed(id, food)) {
                    double maxHealth = Stat.HEALTH.pointsToValue(Slaves.getStats(id).get(Stat.HEALTH));
                    double health = Utils.round(Slaves.getSlaveEntity(id).getHealth());

                    player.sendMessage("");
                    player.sendMessage("§aMáu: §c" + health + "/" + maxHealth);
                    player.sendMessage("");

                    is.setAmount(is.getAmount() - 1);
                    player.updateInventory();
                }
                else player.sendMessage("§cBạn đồng hành giờ đang no rồi, một lúc nữa mới cho ăn tiếp nha");
                return;
            }

            // Stone
            if (SlaveStone.is(is)) {
                SlaveStone stone = SlaveStone.parse(is);
                if (Slaves.addExp(id, stone)) {
                    int exp = Slaves.getSlave(id).getData().getExp();
                    int level = Slaves.getSlave(id).getData().getLevel();
                    double rate = (double) (exp - TravelerOptions.getTotalExpTo(level)) / (double) TravelerOptions.getExpOf(level + 1);

                    player.sendMessage("");
                    player.sendMessage("§aKinh nghiệm: §f" + exp + " (" + Utils.round(rate * 100) + "%)");
                    player.sendMessage("");

                    is.setAmount(is.getAmount() - 1);
                    player.updateInventory();
                }
                else player.sendMessage("§cChọn Đá linh hồn phù hợp với cấp độ Bạn đồng hành!");
                return;
            }

            // Info
            Slaves.showSlaveInfo(player, id);
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.3f, 1f);
        }

    }



}
