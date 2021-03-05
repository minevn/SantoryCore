package mk.plugin.santory.slave;

import java.util.List;

public class Master {

    private Slave currentSlave;

    // Data
    private String name;
    private List<Slave> slaves;

    public Master(String name, List<Slave> slaves) {
        this.name = name;
        this.slaves = slaves;
        this.currentSlave = null;
    }

    public String getName() {
        return name;
    }

    public Slave getSlaveFromModel(String id){
        for (Slave slave : slaves) {
            if (slave.getModelID().equalsIgnoreCase(id)) return slave;
        }
        return null;
    }

    public Slave getSlave(String id) {
        for (Slave slave : slaves) {
            if (slave.getID().equalsIgnoreCase(id)) return slave;
        }
        return null;
    }

    public List<Slave> getSlaves() {
        return slaves;
    }

    public void remove(String id) {
        this.slaves.removeIf(sl -> sl.getID().equalsIgnoreCase(id));
    }

    public Slave getCurrentSlave() {
        return currentSlave;
    }

    public void setCurrentSlave(Slave currentSlave) {
        this.currentSlave = currentSlave;
    }
}
