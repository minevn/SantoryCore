package mk.plugin.santory.mob;

import java.util.List;

import com.google.common.collect.Lists;

import mk.plugin.santory.item.StatValue;
import mk.plugin.santory.stat.Stat;

public enum MobType {
	
	MINION {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 10 + 20 * level / 10));
			stats.add(new StatValue(Stat.DAMAGE, 5 + 5 * level / 10));
			stats.add(new StatValue(Stat.DEFENSE, 10 + 2 * level / 10));
			
			return stats;
		}
	},
	SUB_BOSS {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 30 + 50 * level / 10));
			stats.add(new StatValue(Stat.DAMAGE, 10 + 10 * level / 10));
			stats.add(new StatValue(Stat.DEFENSE, 15 + 5 * level / 10));
			
			return stats;
		}
	},
	MAIN_BOSS {
		@Override
		public List<StatValue> getStats(int level) {
			List<StatValue> stats = Lists.newArrayList();
			stats.add(new StatValue(Stat.HEALTH, 100 + 100 * level / 10));
			stats.add(new StatValue(Stat.DAMAGE, 20 + 20 * level / 10));
			stats.add(new StatValue(Stat.DEFENSE, 20 + 10 * level / 10));
			
			return stats;
		}
	};
	
	public abstract List<StatValue> getStats(int level);
	
}
