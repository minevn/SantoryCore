package mk.plugin.santory.main;

import mk.plugin.santory.command.AdminCommand;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.listener.*;
import mk.plugin.santory.placeholder.SantoryPlaceholder;
import mk.plugin.santory.slave.SlaveTask;
import mk.plugin.santory.task.HealTask;
import mk.plugin.santory.task.TargetTask;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SantoryCore extends JavaPlugin {
	
	@Override
	public void onEnable() {
		// Reload config
		this.reloadConfig();
		
		// Register listeners
		this.registerListeners();
		
		// Register commands
		this.registerCommands();
		
		// Register tasks
		this.registerTasks();
		
		// Register placeholders
		this.registerPlaceholders();
	}
	
	@Override
	public void onDisable() {
		this.saveOninePlayers();
	}
	
	@Override
	public void reloadConfig() {
		this.saveDefaultConfig();
		Configs.reload(this);
	}
	
	public void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
		Bukkit.getPluginManager().registerEvents(new LevelListener(), this);
		Bukkit.getPluginManager().registerEvents(new StateListener(), this);
		Bukkit.getPluginManager().registerEvents(new StatListener(), this);
		Bukkit.getPluginManager().registerEvents(new WeaponListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ArmorListener(), this);
		Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
		Bukkit.getPluginManager().registerEvents(new MobListener(), this);
		Bukkit.getPluginManager().registerEvents(new SlaveListener(), this);
	}
	
	public void registerTasks() {
		new TargetTask().runTaskTimer(this, 0, 2);
		new HealTask().runTaskTimer(this, 0, 20);
		new SlaveTask().runTaskTimer(this, 0, 10);
	}
	
	public void registerPlaceholders() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new SantoryPlaceholder().register();
	}
	
	public void registerCommands() {
		this.getCommand("santory").setExecutor(new AdminCommand());
	}
	
	public void saveOninePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			Travelers.saveAndClearCache(player.getName());
		});
	}
	
	public static SantoryCore get() { 
		return JavaPlugin.getPlugin(SantoryCore.class); 
	}
	
	
}
