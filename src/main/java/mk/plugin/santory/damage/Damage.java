package mk.plugin.santory.damage;

public class Damage {
	
	private final double value;
	private final DamageType type;
	
	public Damage(double value, DamageType type) {
		this.value = value;
		this.type = type;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public DamageType getType() {
		return this.type;
	}
	
}
