package mk.plugin.santory.slave;

import mk.plugin.santory.item.weapon.WeaponType;
import mk.plugin.santory.skill.Skill;
import mk.plugin.santory.slave.state.SlaveState;
import mk.plugin.santory.tier.Tier;
import org.bukkit.Color;

import java.util.List;
import java.util.Map;

public class SlaveModel {

    private String name;
    private String head;
    private Color chestColor;
    private Tier tier;
    private Skill skill;
    private WeaponType weaponType;
    private List<String> skillDesc;
    private Map<SlaveState, List<String>> sounds;

    public SlaveModel(String name, String head, Color chestColor, Tier tier, Skill skill, WeaponType weaponType, Map<SlaveState, List<String>> sounds, List<String> skillDesc) {
        this.name = name;
        this.head = head;
        this.chestColor = chestColor;
        this.tier = tier;
        this.skill = skill;
        this.weaponType = weaponType;
        this.sounds = sounds;
        this.skillDesc = skillDesc;
    }

    public String getName() {
        return name;
    }

    public String getHead() {
        return head;
    }

    public Color getChestColor() {
        return chestColor;
    }

    public Tier getTier() {
        return tier;
    }

    public Skill getSkill() {
        return skill;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public Map<SlaveState, List<String>> getSounds() {
        return sounds;
    }

    public List<String> getSkillDesc() {
        return skillDesc;
    }
}
