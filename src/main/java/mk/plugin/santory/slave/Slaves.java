package mk.plugin.santory.slave;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.StatValue;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.animation.SlaveAnimation;
import mk.plugin.santory.slave.item.SlaveFood;
import mk.plugin.santory.slave.item.SlaveStone;
import mk.plugin.santory.slave.master.Master;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.slave.state.SlaveState;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.TravelerOptions;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Slaves {

    public static final float DEFAULT_MOVE_SPEED = 0.25f;
    public static final int DEATH_TIME = 60;
    public static final int EAT_FULL_COOLDOWN = 30;
    
    private static final Map<String, Set<SlaveState>> currentStates = Maps.newHashMap();
    private static final Map<Entity, String> currentSlaves = Maps.newHashMap();
    private static final Map<String, Map<SlaveState, Long>> soundDelay = Maps.newHashMap();
    private static final Map<String, Long> deadSlaves = Maps.newHashMap();
    private static final Map<String, Long> lastEat = Maps.newHashMap();
    private static final Map<String, String> lastSound = Maps.newHashMap();

    private static final Map<String, LivingEntity> targets = Maps.newHashMap();

    // 3 = Full
    private static final Map<String, Integer> hungers = Maps.newHashMap();

    public static void update(String id) {
        Slave slave = getSlave(id);
        Husk h = getSlaveEntity(id);
        if (h == null) return;
        if (slave.getData().getWeapon() == null)  h.getEquipment().setItemInMainHand(null);
        else h.getEquipment().setItemInMainHand(Items.build(null, Item.parse(slave.getData().getWeapon())));
        double maxHealth = Stat.HEALTH.pointsToValue(getStats(id).get(Stat.HEALTH));
        h.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
    }

    public static void summonSlave(Player player, String id) {
        if (isDead(id)) return;
        despawnCurrentSlave(player);

        Master m = Masters.get(player);
        Slave slave = getSlave(id);
        m.setCurrentSlave(slave);

        Location l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(2).setY(0)));
        Husk h = (Husk) player.getWorld().spawnEntity(l, EntityType.HUSK);
        h.setCustomName(slave.getModel().getTier().getColor() + slave.getModel().getName());
        h.setCustomNameVisible(true);
        h.setBaby(true);
        h.getEquipment().setHelmet(Utils.buildSkull(slave.getModel().getHead()));
        h.getEquipment().setChestplate(Utils.buildChestplate(slave.getModel().getChestColor()));
        h.setSilent(true);
        h.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(100);
        h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_MOVE_SPEED);

        currentSlaves.put(h, id);
        update(id);

        lookAt(h, player, 50);
        SlaveAnimation.SUMMONED.play(h, player);

        Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
            stateCall(id, SlaveState.GREET);
            Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
                removeState(id, SlaveState.GREET);
            }, 100);
        }, 20);
    }

    public static void despawnCurrentSlave(Player player) {
        Master m = Masters.get(player);
        Slave slave = m.getCurrentSlave();
        if (slave == null) return;

        Entity e = getSlaveEntity(slave.getID());
        if (e == null) return;
        currentSlaves.remove(e);
        e.remove();
    }

    public static void clearCache(String id) {
        currentStates.remove(id);
        soundDelay.remove(id);
        deadSlaves.remove(id);
        hungers.remove(id);

        for (Entity e : currentSlaves.keySet()) {
            if (currentSlaves.get(e).equalsIgnoreCase(id)) {
                currentSlaves.remove(e);
                e.remove();
                break;
            }
        }
    }


    public static boolean isSlave(Entity e) {
        return currentSlaves.containsKey(e);
    }

    public static Player getMasterPlayer(String id) {
        Slave slave = getSlave(id);
        return Bukkit.getPlayer(slave.getData().getMaster());
    }

    public static List<String> getSlaves() {
        return Lists.newArrayList(currentSlaves.values());
    }

    public static Slave getSlave(String id) {
        String master = id.split("~")[0];
        String modelID = id.split("~")[1];
        Player player = Bukkit.getPlayer(master);
        if (player == null) return null;

        Master m = Masters.get(player);
        return m.getSlave(id);
    }

    public static Slave getSlave(Entity e) {
        return getSlave(currentSlaves.getOrDefault(e, null));
    }

    public static Husk getSlaveEntity(Player player) {
        String id = getSlave(player);
        return getSlaveEntity(id);
    }

    public static Husk getSlaveEntity(String id) {
        for (Entity e : currentSlaves.keySet()) {
            String eid = currentSlaves.get(e);
            if (eid.equalsIgnoreCase(id)) return (Husk) e;
        }
        return null;
    }

    public static boolean hasSlave(Player player) {
        if (Masters.get(player) == null) return false;
        return Masters.get(player).getSlaves().size() > 0;
    }

    public static String getSlave(Player player) {
        return Masters.get(player).getCurrentSlave().getID();
    }

    public static boolean isAlive(String id) {
        if (getSlaveEntity(id) == null) return false;
        return !getSlaveEntity(id).isDead();
    }

    public static boolean isSlaveAlive(Player player) {
        return isAlive(getSlave(player));
    }

    public static boolean isMaster(Player player, Entity e) {
        if (!isSlave(e)) return false;
        Slave slave = getSlave(e);
        return Masters.get(player).getSlaves().contains(slave);
    }

    public static Set<SlaveState> getCurrentStates(String id) {
        return currentStates.getOrDefault(id, Sets.newHashSet());
    }

    public static boolean isInState(String id, SlaveState state) {
        return getCurrentStates(id).contains(state);
    }

    public static void removeState(String id, SlaveState state) {
        Set<SlaveState> states = getCurrentStates(id);
        if (!states.contains(state)) return;
        states.remove(state);
        currentStates.put(id, states);
        state.unnset((Husk) getSlaveEntity(id));
    }

    public static void stateCall(String id, SlaveState state) {
        Slave slave = getSlave(id);
        Set<SlaveState> states = getCurrentStates(id);
        states.add(state);

        // Remove states
        for (SlaveState si : Sets.newHashSet(state.getSeparates())) {
            if (isInState(id, si)) removeState(id, si);
        }

        // Remove target
        if (state.getSeparates().contains(SlaveState.TARGET)) {
            targets.remove(id);
        }

        currentStates.put(id, states);

        // Check state
        if (!state.isPeriod()) removeState(id, state);

        // Sound play
        if (slave.getModel().getSounds().containsKey(state)) {
            if (lastSound.containsKey(id)) getMasterPlayer(id).stopSound(lastSound.get(id));
            String sound = slave.getModel().getSounds().get(state).get(new Random().nextInt(slave.getModel().getSounds().get(state).size()));
            lastSound.put(id, sound);
            playSound(id, state, sound);
        }
    }

    public static boolean isInSameWorld(String id) {
        if (getSlaveEntity(id) == null) return false;
        return getMasterPlayer(id).getWorld() == getSlaveEntity(id).getWorld();
    }

    public static double getDistanceVsMaster(String id) {
        Player player = getMasterPlayer(id);
        Entity sle = getSlaveEntity(id);
        return player.getLocation().distance(sle.getLocation());
    }

    public static void backtoMaster(String id, boolean teleport) {
        Player player = getMasterPlayer(id);
        Husk e = getSlaveEntity(id);

        if (teleport) {
            teleportTo(e, player);
            SlaveAnimation.SUMMONED.play(e, player);
            lookAt(e, player, 20);
            Bukkit.getScheduler().runTaskLater(SantoryCore.get(), () -> {
                stateCall(id, SlaveState.GREET);
            }, 20);
        }
        else {
            e.setTarget(player);
        }
    }

    public static void lookAt(LivingEntity le, Player player, int tick) {
        Location l = le.getLocation();
        Husk h = (Husk) le;

        long start = System.currentTimeMillis();
        long period = tick * 50;

        new BukkitRunnable() {
            @Override
            public void run() {
                h.setTarget(player);
                h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
                if (System.currentTimeMillis() - start >= period) {
                    h.setTarget(null);
                    h.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(DEFAULT_MOVE_SPEED);
                    this.cancel();
                }
            }
        };
    }

    public static boolean isHealthLowerThan(LivingEntity le, double percent) {
        double max = le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double current = le.getHealth();
        return current < max * percent / 100;
    }

    private static void teleportTo(Entity e, Player player) {
        e.teleport(player.getLocation().add(player.getLocation().getDirection().multiply(2).setY(0)));
    }

    public static void playSound(String id, SlaveState state, String sound) {
        Map<SlaveState, Long> states = Maps.newHashMap();
        if (soundDelay.containsKey(id)) {
            states = soundDelay.get(id);
            if (states.containsKey(state) && states.get(state) > System.currentTimeMillis()) return;
        }
        states.put(state, System.currentTimeMillis() + state.getSoundDelay() * 1000);
        soundDelay.put(id, states);
        getMasterPlayer(id).playSound(getSlaveEntity(id).getLocation(), sound, 1, 1);
    }

    public static double getDamage(String id) {
        return Stat.DAMAGE.pointsToValue(getStats(id).get(Stat.DAMAGE));
    }

    public static double getHeal(String id) {
        return Stat.HEAL.pointsToValue(getStats(id).get(Stat.HEAL)) / 3;
    }

    public static Map<Stat, Integer> getStats(String id) {
        Slave slave = getSlave(id);
        if (slave == null) return Maps.newLinkedHashMap();
        int lv = slave.getData().getLevel();

        int h = 10 + 1 * lv;
        int d = 5 + 1 * lv;
        int heal = 5 + 1 * lv;

        // Base stats
        Map<Stat, Integer> stats = Maps.newLinkedHashMap();
        stats.put(Stat.HEALTH, h);
        stats.put(Stat.DAMAGE, d);
        stats.put(Stat.HEAL, heal);

        // Weapon
        String wd = slave.getData().getWeapon();
        if (wd != null) {
            Item i = mk.plugin.santory.item.Item.parse(wd);
            for (StatValue sv : i.getData().getStats()) {
                stats.put(sv.getStat(), stats.getOrDefault(sv.getStat(), 0) + sv.getValue());
            }
        }

        return stats;
    }

    public static void showSlaveInfo(Player player, String id) {
        Slave slave = getSlave(id);
        String name = slave.getModel().getName();
        int lv = slave.getData().getLevel();
        double maxHealth = Stat.HEALTH.pointsToValue(getStats(id).get(Stat.HEALTH));
        double health = Utils.round(Slaves.getSlaveEntity(id).getHealth());
        int exp = Slaves.getSlave(id).getData().getExp();
        double rate = (double) (exp - TravelerOptions.getTotalExpTo(lv)) / (double) TravelerOptions.getExpOf(lv + 1);

        player.sendMessage("");
        player.sendMessage("§aTên: §f" + name);
        player.sendMessage("§aCấp độ: §f" + lv);
        player.sendMessage("§aSát thương: §f" + getDamage(id));
        player.sendMessage("§aMáu: §f" + health + "/" + maxHealth);
        player.sendMessage("§aHồi phục: §f" + getHeal(id) + " HP/s");
        player.sendMessage("§aKinh nghiệm: §f" + exp + " (" + Utils.round(rate * 100) + "%)");
        player.sendMessage("");
    }

    public static void setDeath(String id) {
        deadSlaves.put(id, System.currentTimeMillis() + DEATH_TIME * 1000);
    }

    public static boolean isDead(String id) {
        return deadSlaves.containsKey(id);
    }

    public static boolean canSpawn(String id) {
        if (!deadSlaves.containsKey(id)) return true;
        return deadSlaves.get(id) < System.currentTimeMillis();
    }

    public static void respawn(String id) {
        deadSlaves.remove(id);
        Slave slave = getSlave(id);
        Player player = Bukkit.getPlayer(slave.getData().getMaster());
        summonSlave(player, id);
    }

    public static boolean isFull(String id) {
        return hungers.getOrDefault(id, 0) >= 3;
    }

    public static void notFullCheck(String id) {
        int hunger = hungers.getOrDefault(id, 0);
        if (lastEat.containsKey(id)) {
            if (lastEat.get(id) + EAT_FULL_COOLDOWN * 1000 > System.currentTimeMillis()) return;
            hunger = Math.max(hunger - 1, 0);
        }
        hungers.put(id, hunger);
    }

    public static boolean addExp(String id, SlaveStone stone) {
        Slave slave = getSlave(id);
        Player player = getMasterPlayer(id);
        Entity entity = getSlaveEntity(id);
        int level = slave.getData().getLevel();
        if (level < stone.getMinLevel() || level > stone.getMaxLevel()) return false;
        slave.getData().setExp(slave.getData().getExp() + stone.getExp());

        player.playSound(entity.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        SlaveAnimation.HAPPY.play((LivingEntity) entity, player);

        update(id);

        return true;
    }

    public static boolean feed(String id, SlaveFood food) {
        int hunger = hungers.getOrDefault(id, 0);
        if (lastEat.containsKey(id)) {
            if (hunger == 3) {
                if (lastEat.get(id) + EAT_FULL_COOLDOWN * 1000 > System.currentTimeMillis()) return false;
            }
        }

        Utils.addHealth(getSlaveEntity(id), food.getHeal());
        lastEat.put(id, System.currentTimeMillis());


        Entity entity = getSlaveEntity(id);
        Player player = getMasterPlayer(id);
        player.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
        SlaveAnimation.HAPPY.play((LivingEntity) entity, player);

        return true;
    }

    public static boolean isMasterOnline(String id) {
        String name = id.split("~")[0];
        if (Bukkit.getPlayer(name) == null) return false;
        return true;
    }

    public static void setTarget(String id, LivingEntity target) {
        targets.put(id, target);
    }

    public static LivingEntity getTarget(String id) {
        return targets.getOrDefault(id, null);
    }

}
