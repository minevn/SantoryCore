package mk.plugin.santory.slave.task;

import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.slave.state.SlaveState;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class SlaveTask extends BukkitRunnable {

    private final double DISTANCE = 1.5;
    private final double TELEPORT_DISTANCE = 30;

    @Override
    public void run() {
        for (String id : Slaves.getSlaves()) {
            Slave slave = Slaves.getSlave(id);

            // Master offline
            if (!Slaves.isMasterOnline(id)) {
                Slaves.clearCache(id);
                continue;
            }

            // If dead
            if (Slaves.isDead(id)) {
                if (Slaves.canSpawn(id)) Slaves.respawn(id);
                continue;
            }

            // Food check
            Slaves.notFullCheck(id);

            // Check if diffirent world
            if (!Slaves.isInSameWorld(id)) {
                if (Slaves.isAlive(id)) Slaves.backtoMaster(id, true);
                else continue;
            }


            // Get state distance required
            double dr = getDistance(id);
            if (dr == -1) continue;

            // Check distance
            double d = Slaves.getDistanceVsMaster(id);

            // Check follow
            if (!Slaves.isInState(id, SlaveState.GREET) && Slaves.isInState(id, SlaveState.FOLLOW)) {
                if (d > TELEPORT_DISTANCE) Slaves.backtoMaster(id, true);
                else {
                    Slaves.backtoMaster(id, false);
                }
            }

            // Run follow
            if (d >= dr && !Slaves.isInState(id, SlaveState.GREET) &&  !Slaves.isInState(id, SlaveState.FOLLOW)) {
                Slaves.stateCall(id, SlaveState.FOLLOW);
            }

            // Call idle state
            if (!Slaves.isInState(id, SlaveState.GREET) && !Slaves.isInState(id, SlaveState.IDLE) && d < DISTANCE && !Slaves.isInState(id, SlaveState.TARGET)) {
                Slaves.stateCall(id, SlaveState.IDLE);
                Husk husk = Slaves.getSlaveEntity(id);
                husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
                husk.setTarget(null);
            }

            // Target
            if (Slaves.isInState(id, SlaveState.TARGET)) {
                LivingEntity le = Slaves.getTarget(id);
                if (le == null) return;
                Husk husk = Slaves.getSlaveEntity(id);
                husk.setTarget(le);
            }
        }
    }

    private double getDistance(String id) {
        for (SlaveState state : Slaves.getCurrentStates(id)) {
            switch (state) {
                case TARGET: return 20;
                case RUNAWAY: return -1;
                case IDLE: return 5;
            }
        }
        return 5;
    }

}
