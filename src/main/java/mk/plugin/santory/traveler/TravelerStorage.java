package mk.plugin.santory.traveler;

import mk.plugin.playerdata.storage.PlayerData;
import mk.plugin.playerdata.storage.PlayerDataAPI;

public class TravelerStorage {
	
	private static final String KEY = "santoryTraveler";
	private static final String HOOK = "santoryPlayers";
	
	public static TravelerData get(String name) {
		PlayerData pb = PlayerDataAPI.get(name, HOOK);
		if (!pb.hasData(KEY)) return new TravelerData();
		return TravelerData.read(pb.getValue(KEY));
	}
	
	public static void save(String name, TravelerData data) {
		PlayerData pb = PlayerDataAPI.get(name, HOOK);
		pb.set(KEY, data.toString());
		pb.save();
	}
	
}
