package mk.plugin.santory.slave;

import mk.plugin.santory.config.Configs;

public class Slave {

    private String model;
    private SlaveData data;

    public Slave(String model, String master) {
        this.model = model;
        this.data = new SlaveData(master);
    }

    public Slave(String model, SlaveData data) {
        this.model = model;
        this.data = data;
    }

    public SlaveModel getModel() {
        return Configs.getSlaveModel(this.model);
    }

    public String getModelID() {
        return model;
    }

    public SlaveData getData() {
        return data;
    }

    public String getID() {
        return this.getData().getMaster() + "~" + this.getModelID();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Slave == false) return false;
        Slave so = (Slave) o;
        return so.getID().equalsIgnoreCase(this.getID());
    }

}
