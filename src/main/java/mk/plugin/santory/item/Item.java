package mk.plugin.santory.item;

import mk.plugin.santory.config.Configs;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Item {
	
	private final String model;
	private ItemData data;
	
	public Item(String model, ItemData data) {
		this.model = model;
		this.data = data;
	}
	
	public String getModelID() {
		return this.model;
	}
	
	public ItemModel getModel() {
		return Configs.getModel(this.model);
	}
	
	public ItemData getData() {
		return this.data;
	}
	
	public void setData(ItemData data) {
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject jo = new JSONObject();
		jo.put("model", this.model);
		jo.put("data", this.data.toString());
		
		return jo.toJSONString();
	}
	
	public static Item parse(String s) {
		JSONObject jo = (JSONObject) JSONValue.parse(s);
		String model = jo.get("model").toString();
		ItemData data = ItemData.parse(jo.get("data").toString());
		ItemMeta meta = null;

		return new Item(model, data);
	}
	
}
