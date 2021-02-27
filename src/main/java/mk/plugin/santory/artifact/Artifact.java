package mk.plugin.santory.artifact;

import java.util.Map;

import com.google.common.collect.Maps;

import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.stat.Stat;

public class Artifact {
	
	//          Stat  Chance
	private Map<Stat, Double> mainStats;
	private Map<Stat, Double> subStats;
	
	private String setID;
	private Stat setStat;
	
	public Artifact(Map<Stat, Double> mainStats, Map<Stat, Double> subStats, String setID, Stat setStat) {
		this.mainStats = mainStats;
		this.subStats = subStats;
		this.setID = setID;
		this.setStat = setStat;
	}
	
	public Map<Stat, Double> getMainStats() {
		return this.mainStats;
	}
	
	public Map<Stat, Double> getSubStats() {
		return this.subStats;
	}
	
	public String getSetID() {
		return this.setID;
	}
	
	public Stat getSetStat() {
		return this.setStat;
	}
	
	public static Artifact parse(ItemModel model) {
		String ms = model.getMetadata().get("artifact-main-stat");
		Map<Stat, Double> m = Maps.newHashMap();
		for (String s : ms.split(";")) {
			m.put(Stat.valueOf(s.split(":")[0]), Double.valueOf(s.split(":")[1]));
		}
		
		String ss = model.getMetadata().get("artifact-sub-stat");
		Map<Stat, Double> m2 = Maps.newHashMap();
		for (String s : ss.split(";")) {
			m2.put(Stat.valueOf(s.split(":")[0]), Double.valueOf(s.split(":")[1]));
		}
		
		String setID = model.getMetadata().get("artifact-set-id");
		Stat stat = Stat.valueOf(model.getMetadata().get("artifact-set-stat"));
		
		return new Artifact(m, m2, setID, stat);
	}
	
}
