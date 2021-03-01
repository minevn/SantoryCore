package mk.plugin.santory.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigGetter {
	
	private final FileConfiguration config;
	
	private ConfigGetter(FileConfiguration config) {
		this.config = config;
	}
	
	public int getInt(String path, int defaultValue) {
		if (config.contains(path)) return config.getInt(path);
		return defaultValue;
	}
	
	public double getDouble(String path, double defaultValue) {
		if (config.contains(path)) return config.getDouble(path);
		return defaultValue;
	}
	
	public long getLong(String path, long defaultValue) {
		if (config.contains(path)) return config.getLong(path);
		return defaultValue;
	}
	
	public boolean getBoolean(String path, boolean defaultValue) {
		if (config.contains(path)) return config.getBoolean(path);
		return defaultValue;
	}
	
	public String getString(String path, String defaultValue) {
		if (config.contains(path)) return config.getString(path);
		return defaultValue;
	}
	
	public List<String> getStringList(String path, List<String> defaultValue) {
		if (config.contains(path)) return config.getStringList(path);
		return defaultValue;
	}

	public static ConfigGetter from(FileConfiguration config) {
		return new ConfigGetter(config);
	}
	
}
