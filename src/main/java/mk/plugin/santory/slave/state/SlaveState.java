package mk.plugin.santory.slave.state;

import com.google.common.collect.Sets;
import mk.plugin.santory.slave.Slaves;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Husk;

import java.util.Set;

public enum SlaveState {

    IDLE(true, 40) {
        @Override
        public Set<SlaveState> getSeparates() { 
            return Sets.newHashSet(TARGET, ATTACK, FOLLOW, KILL, DAMAGED, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {
            husk.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(Slaves.DEFAULT_MOVE_SPEED);
        }
    },
    TARGET(true, 20) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, GREET, FOLLOW, KILL, DAMAGED, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {
            husk.setTarget(null);
        }
    },
    ATTACK(false, 5) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, GREET, FOLLOW, KILL, DAMAGED, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    GREET(false, 30) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(TARGET, ATTACK, FOLLOW, KILL, DAMAGED, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    FOLLOW(true, 40) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, TARGET, ATTACK, GREET, KILL, DAMAGED, DEATH, RUNAWAY);
        }
        @Override
        public void unnset(Husk husk) {

        }
    },
    KILL(false, 30) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, TARGET, ATTACK, GREET, FOLLOW, DAMAGED, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    DAMAGED(false, 3) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, GREET, FOLLOW, KILL, ATTACK, DEATH, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    DEATH(false, 1) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, TARGET, ATTACK, GREET, FOLLOW, DAMAGED, KILL, RUNAWAY);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    RUNAWAY(true, 20) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, TARGET, ATTACK, GREET, FOLLOW, DAMAGED, DEATH, KILL);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    SKILL(false, 1) {
        @Override
        public Set<SlaveState> getSeparates() {
            return Sets.newHashSet(IDLE, ATTACK, GREET, FOLLOW, DAMAGED, KILL, RUNAWAY, DEATH);
        }

        @Override
        public void unnset(Husk husk) {

        }
    },
    ;

    private int soundDelay;
    private boolean isPeriod;

    private SlaveState(boolean isPeriod, int soundDelay) {
        this.isPeriod = isPeriod;
        this.soundDelay = soundDelay;
    }

    public boolean isPeriod() {
        return this.isPeriod;
    }
    public int getSoundDelay() {
        return this.soundDelay;
    }

    public abstract Set<SlaveState> getSeparates();
    public abstract void unnset(Husk husk);


}
