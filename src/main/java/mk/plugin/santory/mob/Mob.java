package mk.plugin.santory.mob;

import mk.plugin.santory.item.StatValue;
import mk.plugin.santory.stat.Stat;

import java.util.List;
import java.util.UUID;

public class Mob {

	private final int id;
	private final MobType type;
	private final int level;
	private final List<StatValue> stats;
	
	private float damageMulti;

	public Mob(int id, MobType type, int level, List<StatValue> stats) {
		this.id = id;
		this.type = type;
		this.level = level;
		this.stats = stats;
		this.damageMulti = 1;
	}

	public int getID() {
		return this.id;
	}

	public MobType getType() {
		return this.type;
	}

	public int getLevel() {
		return this.level;
	}

	public int getStat(Stat stat) {
		for (StatValue sv : stats) {
			if (sv.getStat() == stat)
				return sv.getValue();
		}
		return 0;
	}

	public float getDamageMulti() {
		return this.damageMulti;
	}
	
	public void setDamageMulti(float multi) {
		this.damageMulti = multi;
	}
	
	public List<StatValue> getStats() {
		return this.stats;
	}

}
