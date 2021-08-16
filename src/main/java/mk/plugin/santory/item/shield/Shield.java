package mk.plugin.santory.item.shield;

import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.skill.Skill;

public class Shield {

    private final Skill skill;

    public Shield(Skill skill) {
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    public static Shield parse(ItemModel model) {
        String sn = model.getMetadata().get("shield-skill");
        Skill skill = sn != null ? Skill.valueOf(sn) : null;
        return new Shield(skill);
    }
}
