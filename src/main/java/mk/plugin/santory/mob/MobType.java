package mk.plugin.santory.mob;

import com.google.common.collect.Lists;
import mk.plugin.santory.item.StatValue;
import mk.plugin.santory.stat.Stat;

import java.util.List;

public enum MobType {
	
	MINION {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 10 + level * 10));
			stats.add(new StatValue(Stat.DAMAGE, 5 + level / 2));
			stats.add(new StatValue(Stat.DEFENSE, level / 3));
			
			return stats;
		}
	},
	GREAT_MINION {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 15 + level * 5));
			stats.add(new StatValue(Stat.DAMAGE, 10 + level * 7 / 10));
			stats.add(new StatValue(Stat.DEFENSE, 5 + level / 2));
			
			return stats;
		}
	},
	BOSS {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 20 + level * 15));
			stats.add(new StatValue(Stat.DAMAGE, 15 + level * 12 / 10));
			stats.add(new StatValue(Stat.DEFENSE, 10 + level * 7 / 10));

			if (level >= 20 && level <= 35) {
				for (int i = 0; i < stats.size(); i++) {
					stats.set(i, new StatValue(stats.get(i).getStat(), stats.get(i).getValue() * 11 / 10));
				}
			}
			if (level > 35 && level <= 50) {
				for (int i = 0; i < stats.size(); i++) {
					stats.set(i, new StatValue(stats.get(i).getStat(), stats.get(i).getValue() * 11 / 10));
				}
			}
			if (level > 50 && level <= 65) {
				for (int i = 0; i < stats.size(); i++) {
					stats.set(i, new StatValue(stats.get(i).getStat(), stats.get(i).getValue() * 11 / 10));
				}
			}
			if (level > 65 && level <= 80) {
				for (int i = 0; i < stats.size(); i++) {
					stats.set(i, new StatValue(stats.get(i).getStat(), stats.get(i).getValue() * 11 / 10));
				}
			}
			if (level > 80 && level <= 100) {
				for (int i = 0; i < stats.size(); i++) {
					stats.set(i, new StatValue(stats.get(i).getStat(), stats.get(i).getValue() * 11 / 10));
				}
			}
			
			return stats;
		}
	};
	
	public abstract List<StatValue> getStats(int level);
	
}
