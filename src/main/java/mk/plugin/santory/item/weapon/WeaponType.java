package mk.plugin.santory.item.weapon;

import mk.plugin.santory.item.shooter.Shooter;

public enum WeaponType {
	
	LIGHT("Nhẹ", 2.5),
	HEAVY("Nặng", 2.5),
	SHOOTER("Tầm xa", Shooter.BOW),
	RANGED("Dài", 3.5);
	
	private String name;
	private double range;
	private boolean isShooter;
	private Shooter shooter;
	
	private WeaponType(String name, Shooter shooter) {
		this.name = name;
		this.isShooter = true;
		this.shooter = shooter;
		this.range = 50;
	}
	
	private WeaponType(String name, double range) {
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
