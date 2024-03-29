package mk.plugin.santory.main;

import mk.plugin.santory.command.AdminCommand;
import mk.plugin.santory.command.PlayerCommand;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.history.histories.*;
import mk.plugin.santory.listener.*;
import mk.plugin.santory.placeholder.SantoryPlaceholder;
import mk.plugin.santory.skin.listener.SkinListener;
import mk.plugin.santory.skin.listener.SkinTeleportListener;
import mk.plugin.santory.skin.system.PlayerSkins;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.slave.task.SlaveTask;
import mk.plugin.santory.task.HealTask;
import mk.plugin.santory.task.TargetTask;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SantoryCore extends JavaPlugin {

	private static SantoryCore plugin;

	private TargetTask targetTask;

	private WeaponWishHistory weaponWishHistory;
	private ArmorWishHistory armorWishHistory;
	private EnhanceHistory enhanceHistory;
	private UpgradeHistory upgradeHistory;
	private AscentHistory ascentHistory;
	private TimedHistory timedHistory;
	private ArtifactScrapHistory artifactScrapHistory;


	@Override
	public void onEnable() {
		plugin = this;
		this.reloadConfig();
		this.registerListeners();
		this.registerCommands();
		this.registerTasks();
		this.registerPlaceholders();
		this.registerChannels();
		this.initHistories();
		this.updateOnlinePlayers();
	}
	
	@Override
	public void onDisable() {
		this.saveOninePlayers();
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerSkins.destroy(player);
			Slaves.despawnCurrentSlave(player);
		}
	}
	
	@Override
	public void reloadConfig() {
		this.saveDefaultConfig();
		Configs.reload(this);
	}
	
	public void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new ItemListener(), this);
		pm.registerEvents(new LevelListener(), this);
		pm.registerEvents(new StateListener(), this);
		pm.registerEvents(new StatListener(), this);
		pm.registerEvents(new WeaponListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new ArmorListener(), this);
		pm.registerEvents(new GUIListener(), this);
		pm.registerEvents(new ItemEquipListener(), this);
		pm.registerEvents(new SlaveListener(), this);
		pm.registerEvents(new SkinListener(), this);
		pm.registerEvents(new SkinTeleportListener(), this);
		pm.registerEvents(new NPCListener(), this);
		
		if (pm.isPluginEnabled("MythicMobs")) {
			pm.registerEvents(new MobListener(), this);
		}

		if (pm.isPluginEnabled("XacMinh")) {
			pm.registerEvents(new XacMinhListener(), this);
		}
	}
	
	public void registerTasks() {
		this.targetTask = new TargetTask();
		this.targetTask.runTaskTimerAsynchronously(this, 0, 1);
		new HealTask().runTaskTimer(this, 0, 30);
		new SlaveTask().runTaskTimer(this, 0, 10);

		// Skin armorstand illegal check
		Tasks.sync(SkinListener::illegalCheck,0, 100);
	}
	
	public void registerPlaceholders() {
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new SantoryPlaceholder().register();
	}

	private void registerChannels() {
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "fs:minestrike");
	}

	public void registerCommands() {
		var adminCmd = new AdminCommand();
		var playerCmd = new PlayerCommand();

		this.getCommand("santory").setExecutor(adminCmd);
		this.getCommand("player").setExecutor(playerCmd);
		this.getCommand("forge").setExecutor(playerCmd);
		this.getCommand("see").setExecutor(playerCmd);
		this.getCommand("artifact").setExecutor(playerCmd);
		this.getCommand("globalspeaker").setExecutor(playerCmd);
		this.getCommand("skinsystem").setExecutor(playerCmd);
	}

	public void initHistories() {
		this.weaponWishHistory = new WeaponWishHistory();
		this.armorWishHistory = new ArmorWishHistory();
		this.enhanceHistory = new EnhanceHistory();
		this.upgradeHistory = new UpgradeHistory();
		this.ascentHistory = new AscentHistory();
		this.timedHistory = new TimedHistory();
		this.artifactScrapHistory = new ArtifactScrapHistory();
	}

	public void saveOninePlayers() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			Travelers.saveAndClearCache(player.getName());
			Masters.saveAndClearCache(player);
		});
	}

	public void updateOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Travelers.updateState(p);
		}
	}

	public TargetTask getTargetTask() {
		return targetTask;
	}

	public WeaponWishHistory getWeaponWishHistory() {
		return weaponWishHistory;
	}

	public ArmorWishHistory getArmorWishHistory() {
		return armorWishHistory;
	}

	public EnhanceHistory getEnhanceHistory() {
		return enhanceHistory;
	}

	public UpgradeHistory getUpgradeHistory() {
		return upgradeHistory;
	}

	public AscentHistory getAscentHistory() {
		return ascentHistory;
	}

	public TimedHistory getTimedHistory() {
		return timedHistory;
	}

	public ArtifactScrapHistory getArtifactScrapHistory() {
		return artifactScrapHistory;
	}

	public static SantoryCore getPlugin() {
		return plugin;
	}

	public static SantoryCore get() {
		return plugin;
	}

}
