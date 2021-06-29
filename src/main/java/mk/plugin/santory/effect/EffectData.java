package mk.plugin.santory.effect;

public class EffectData {

    private final Effect effect;
    private final double value;
    private final long duration;

    public EffectData(Effect effect, double value, long duration) {
        this.effect = effect;
        this.value = value;
        this.duration = duration;
    }

    public Effect getEffect() {
        return effect;
    }

    public double getValue() {
        return value;
    }

    public long getDuration() {
        return duration;
    }
}
