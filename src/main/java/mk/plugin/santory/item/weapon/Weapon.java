package mk.plugin.santory.item.weapon;

import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.skill.Skill;

public class Weapon {
	
	private final WeaponType type;
	private final Skill skill;
	
	public Weapon(WeaponType type, Skill skill) {
		this.type = type;
		this.skill = skill;
	}
	
	public WeaponType getType() {
		return this.type;
	}
	
	public Skill getSkill() {
		return this.skill;
	}
	
	public static Weapon parse(ItemModel model) {
		String sn = model.getMetadata().get("weapon-skill");
		Skill skill = sn != null ? Skill.valueOf(sn) : null;
		return new Weapon(WeaponType.valueOf(model.getMetadata().get("weapon-type")), skill);
	}
	
}
