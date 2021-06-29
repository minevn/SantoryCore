package mk.plugin.santory.skin;

import com.google.common.collect.Lists;
import mk.plugin.santory.effect.Effect;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.ItemStackManager;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Skin {

    private final String id;

    private final Material m;
    private final int model;

    private final Effect effect;
    private final Tier tier;
    private final SkinType type;
    private final String name;
    private final String desc;

    public Skin(String id, Material m, int model, Effect effect, Tier tier, SkinType type, String name, String desc) {
        this.id = id;
        this.m = m;
        this.model = model;
        this.effect = effect;
        this.tier = tier;
        this.type = type;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return this.id;
    }

    public Material getMaterial() {
        return m;
    }

    public int getModel() {
        return model;
    }

    public Effect getEffect() {
        return effect;
    }

    public Tier getTier() {
        return tier;
    }

    public SkinType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public ItemStack build() {
        var is = new ItemStack(this.m);
        var ism = new ItemStackManager(SantoryCore.get(), is);

        ism.setName(this.tier.getColor() + "§l" + this.name);
        ism.setModelData(this.model);

        List<String> lore = Lists.newArrayList();
        lore.add("§f§oTrang phục (skin)");

        ism.setLore(lore);
        ism.setTag(Skins.TAG, this.id);

        return is;
    }

}
