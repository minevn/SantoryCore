package mk.plugin.santory.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.stat.Stat;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ItemData {
	
	private final UUID uid;
	private String desc;
	private int exp;
	private int level;
	private int durability;
	private Ascent ascent;
	private List<StatValue> stats;
	
	public ItemData(ItemModel model) {
		this.uid = UUID.randomUUID();
		this.desc = null;
		this.exp = 0;
		this.level = 0;
		this.durability = Configs.MAX_DURABILITY;
		this.ascent = Ascent.I;
		this.stats = Lists.newArrayList();
		model.getBaseStats().forEach((s, v) -> stats.add(new StatValue(s, v)));
	}
	
	public ItemData(String desc, int exp, int level, int durability, Ascent ascent, List<StatValue> stats) {
		this.uid = UUID.randomUUID();
		this.desc = desc;
		this.exp = exp;
		this.level = level;
		this.durability = durability;
		this.ascent = ascent;
		this.stats = stats;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public Grade getGrade() {
		Grade g = Grade.I;
		for (Entry<Grade, Integer> e : Configs.getExpRequires().entrySet()) {
			if (this.exp >= e.getValue() && g.getValue() < e.getKey().getValue()) {
				g = e.getKey();
			}
		}
		return g;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
//	public int getDurability() {
//		return this.durability;
//	}
	
//	public void setDurability(int durability) {
//		this.durability = durability;
//	}
	
	public Ascent getAscent() {
		return this.ascent;
	}
	
	public void setAscent(Ascent ascent) {
		this.ascent = ascent;
	}
	
	public List<StatValue> getStats() {
		return this.stats;
	}
	
	public Map<Stat, Integer> getMapStats() {
		Map<Stat, Integer> m = Maps.newLinkedHashMap();
		for (StatValue sv : stats) {
			m.put(sv.getStat(), m.getOrDefault(sv.getStat(), 0) + sv.getValue());
		}
		return m;
	}
	
	public void setStats(List<StatValue> stats) {
		this.stats = stats;
	}
	
	public int getStat(Stat stat) {
		int v = 0;
		for (StatValue sv : this.stats)
			if (sv.getStat() == stat) v += sv.getValue();
		return v;
	}
	
	private String toStatString() {
		String s = "";
		for (StatValue sv : this.stats) {
			s += sv.toString() + ";";
		}
		return s.substring(0, Math.max(0, s.length() - 1));
	}
	

	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject jo = new JSONObject();
		jo.put("uid", this.uid.toString());
		jo.put("desc", this.desc);
		jo.put("exp", this.exp);
		jo.put("level", this.level);
		jo.put("durability", this.durability);
		jo.put("ascent", this.ascent.toString());
		jo.put("stats", toStatString());
		
		return jo.toJSONString();
	}
	

	@SuppressWarnings("unchecked")
	public static ItemData parse(String s) {
		JSONObject jo = (JSONObject) JSONValue.parse(s);
		String desc = jo.containsKey("desc") && jo.get("desc") != null ? jo.get("desc").toString() : null;
		int gradeExp = Long.valueOf((Long) jo.get("exp")).intValue();
		int level = Long.valueOf((Long) jo.get("level")).intValue();
		int durability = Long.valueOf((Long) jo.get("durability")).intValue();
		Ascent asc = Ascent.valueOf(jo.getOrDefault("ascent", "I").toString());
		List<StatValue> stats = jo.containsKey("stats") ? parseStats(jo.get("stats").toString()) : Lists.newArrayList();
		
		return new ItemData(desc, gradeExp, level, durability, asc, stats);
	}
	
	private static List<StatValue> parseStats(String s) {
		List<StatValue> l = Lists.newArrayList();
		for (String ss : s.split(";")) l.add(StatValue.parse(ss));
		return l;
	}
	
}
