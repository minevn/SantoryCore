package mk.plugin.santory.slave;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.traveler.TravelerOptions;

public class SlaveData {

    public static final int MAX_LEVEL = 100;

    private String master;
    private int exp;
    private Ascent ascent;
    private String weapon;

    public SlaveData(String master) {
        this.master = master;
        this.exp = 0;
        this.ascent = Ascent.I;
        this.weapon = null;
    }

    public String getMaster() {
        return master;
    }

    public int getExp() {
        return exp;
    }

    public int getLevel() {
        int i = 1;
        while (TravelerOptions.getTotalExpTo(i) <= this.getExp()) i++;
        return Math.min(i-1, MAX_LEVEL);
    }

    public Ascent getAscent() {
        return ascent;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setLevel(int level) {
        this.exp = TravelerOptions.getTotalExpTo(level);
    }

    public void setAscent(Ascent ascent) {
        this.ascent = ascent;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }



    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static SlaveData parse(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, SlaveData.class);
    }

}
