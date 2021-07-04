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
			stats.add(new StatValue(Stat.HEALTH, 50 + Double.valueOf(Math.pow(level / 10, 2)).intValue() * 250));
			stats.add(new StatValue(Stat.DAMAGE, 15 + level * 12 / 10));
			stats.add(new StatValue(Stat.DEFENSE, 10 + level * 7 / 10));

			return stats;
		}
	},
	WORLD_BOSS {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 50 + 3 * Double.valueOf(Math.pow(level / 10, 2)).intValue() * 250));
			stats.add(new StatValue(Stat.DAMAGE, 15 + level * 12 / 10));
			stats.add(new StatValue(Stat.DEFENSE, 10 + level * 7 / 10));

			return stats;
		}
	},
	;
	
	public abstract List<StatValue> getStats(int level);
	
}
