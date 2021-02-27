package mk.plugin.santory.item;

import mk.plugin.santory.stat.Stat;

public class StatValue {
	
	private Stat stat;
	private int value;
	
	public StatValue(Stat stat, int value) {
		this.stat = stat;
		this.value = value;
	}
	
	public Stat getStat() {
		return this.stat;
	}
	
	public int getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.stat.name() + ":" + this.value;
	}
	
	public static StatValue parse(String s) {
		return new StatValue(Stat.valueOf(s.split(":")[0]), Integer.valueOf(s.split(":")[1]));
	}
	
}
