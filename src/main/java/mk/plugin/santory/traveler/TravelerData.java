package mk.plugin.santory.traveler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.wish.WishData;

public class TravelerData {
	
	private Grade grade;
	private long exp;
	private List<Item> artifacts;
	private Map<String, WishData> wishes;
	
	public TravelerData() {
		this.grade = Grade.I;
		this.exp = 0;
		this.artifacts = Lists.newArrayList();
		this.wishes = Maps.newHashMap();
	}
	
	public TravelerData(Grade grade, long exp, List<Item> artifacts, Map<String, WishData> wishes) {
		this.grade = grade;
		this.exp = exp;
		this.artifacts = artifacts;
		this.wishes = wishes;
	}

	public Grade getGrade() {
		return this.grade;
	}
	
	public void setGrade(Grade grade) {
		this.grade = grade;
	}
	
	public long getExp() {
		return this.exp;
	}
	
	public void setExp(long exp) {
		this.exp = exp;
	}
	
	public List<Item> getArtifacts() {
		return this.artifacts;
	}
	
	public void setArtifacts(List<Item> artifacts) {
		this.artifacts = artifacts;
	}
	
	public Map<String, WishData> getWishes() {
		return this.wishes;
	}
	
	public WishData getWish(String id) {
		return this.wishes.getOrDefault(id, new WishData(id));
	}
	
	public void setWish(String id, WishData wd) {
		this.wishes.put(id, wd);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject jo = new JSONObject();
		jo.put("grade", this.grade.name());
		jo.put("exp", this.exp);
		wishes.forEach((id, wd) -> {
			jo.put("wish-" + id, wd.toString());
		});
		
		// List
		JSONArray ja = new JSONArray();
		this.artifacts.forEach(item -> ja.add(item.toString()));
		jo.put("artifacts", ja);
		
		return jo.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public static TravelerData read(String s) {
		JSONObject jo = (JSONObject) JSONValue.parse(s);
		Grade g = Grade.valueOf(jo.get("grade").toString());
		long xp = Long.valueOf(jo.get("exp").toString());
		List<Item> art = (List<Item>) ((JSONArray) jo.get("artifacts")).stream().map(o -> Item.parse((String) o)).collect(Collectors.toList());
		Map<String, WishData> wishes = Maps.newHashMap();
		for (String ws : Configs.getWishes().keySet()) {
			wishes.put(ws, new WishData(ws));
		}
		for (Object k : jo.keySet()) {
			String ks = k.toString();
			if (!ks.startsWith("wish-")) continue;
			wishes.put(ks.replace("wish-", ""), WishData.parse(jo.get(k).toString()));
		}
		
		return new TravelerData(g, xp, art, wishes);
	}
	
}
