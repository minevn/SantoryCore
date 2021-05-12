package mk.plugin.santory.item.weapon;

import mk.plugin.santory.item.shooter.Shooter;

public enum WeaponType {
	
	LIGHT("Vũ khí ngắn", 3),
	HEAVY("Vũ khí nặng", 3),
	SHOOTER("Vũ khí tầm xa", Shooter.BOW),
	RANGED("Vũ khí dài", 5);
	
	private final String name;
	private final double range;
	private final boolean isShooter;
	private final Shooter shooter;
	
	WeaponType(String name, Shooter shooter) {
		this.name = name;
		this.isShooter = true;
		this.shooter = shooter;
		this.range = 50;
	}
	
	WeaponType(String name, double range) {
		this.name = name;
		this.range = range;
		this.isShooter = false;
		this.shooter = null;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getRange() {
		return this.range;
	}
	
	public boolean isShooter() {
		return this.isShooter;
	}
	
	public Shooter getShooter() {
		return this.shooter;
	}
	
}
