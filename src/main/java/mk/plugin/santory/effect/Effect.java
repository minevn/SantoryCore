package mk.plugin.santory.effect;

import mk.plugin.santory.main.SantoryCore;
import org.bukkit.entity.Player;

import java.util.Set;

public enum Effect {

    SLOW_I(Set.of(EffectType.ATTACK_TARGET_APPLY)) {
        @Override
        public double pointToValue() {
            return 0;
        }

        @Override
        public void apply(Player player, double value, long duration) {
            SantoryCore.get().getEffectTask().addSpeedChange(player, new EffectData(this, value, duration));
        }
    };

    private Set<EffectType> types;

    Effect(Set<EffectType> types) {
        this.types = types;
    }

    public Set<EffectType> getTypes() {
        return types;
    }

    public abstract double pointToValue();
    public abstract void apply(Player player, double value, long duration);

}
