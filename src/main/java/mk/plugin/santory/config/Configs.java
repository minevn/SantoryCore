package mk.plugin.santory.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.element.Element;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.item.ItemTexture;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.weapon.WeaponType;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.mob.MobType;
import mk.plugin.santory.permission.SantoryPermission;
import mk.plugin.santory.skill.Skill;
import mk.plugin.santory.slave.SlaveModel;
import mk.plugin.santory.slave.state.SlaveState;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.ItemStackManager;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.LocationData;
import mk.plugin.santory.utils.Utils;
import mk.plugin.santory.wish.Wish;
import mk.plugin.santory.wish.WishKey;
import mk.plugin.santory.wish.WishReward;
import mk.plugin.santory.wish.WishRewardItem;
import org.apache.commons.io.FileUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Configs {
	
	public static boolean LEVEL_VALLINA_UPDATE = true;
	public static long LEVEL_BASE_EXP = 350;
	public static long LEVEL_PLUS_EXP = 150;
	
	public static int MAX_DURABILITY = 800;
	
	public static double ASCENT_BASE_CHANCE = 50;
	public static int ASCENT_FEE = 10000;
	public static double UPGRADE_BASE_CHANCE = 50;
	public static int UPGRADE_FEE = 10000;
	public static int UPGRADE_EXP = 100;
	public static int ENHANCE_FEE = 10000;
	
	public static int ART_BASE_MAIN_STAT = 15;
	public static int ART_BASE_SUB_STAT = 5;
	public static double ART_STAT_RANGE = 0.25;

	public static long DIE_EXP_LOST_PERCENT = 25;

	private static ItemStack KEEP_STONE;
	private static ItemStack GLOBAL_SPEAKER;
	
	private static final Map<Tier, Double> artTierUps = Maps.newHashMap();
	private static final Map<Integer, Double> artStatSetUp = Maps.newLinkedHashMap();
	
	private static final Map<Grade, Integer> gradeExps = Maps.newHashMap();
	private static final Map<Integer, Double> enhanceRates = Maps.newHashMap();
	
	private static final Map<String, ItemModel> models = Maps.newHashMap();
	private static final Map<String, Wish> wishes = Maps.newHashMap();
	private static final List<String> pvpWorlds = Lists.newArrayList();

	private static final Map<String, Integer> mobLevels = Maps.newHashMap();
	private static final Map<String, MobType> mobTypes = Maps.newHashMap();

	private static final Map<String, SlaveModel> slaves = Maps.newHashMap();

	private static final Map<String, WishKey> wishKeys = Maps.newHashMap();

	private static String chatPrefixDefault;
	private static final Map<String, String> chatPrefixes = Maps.newHashMap();

	private static List<String> xmSuccess = Lists.newArrayList();
	private static Map<String, SantoryPermission> permissions = Maps.newHashMap();

	public static void reload(JavaPlugin plugin) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
		LEVEL_VALLINA_UPDATE = ConfigGetter.from(config).getBoolean("level.vallina-update", LEVEL_VALLINA_UPDATE);
		LEVEL_BASE_EXP = ConfigGetter.from(config).getLong("level.base-exp", LEVEL_BASE_EXP);
		LEVEL_PLUS_EXP = ConfigGetter.from(config).getLong("level.plus-exp", LEVEL_PLUS_EXP);
		MAX_DURABILITY = ConfigGetter.from(config).getInt("item.max-durability", MAX_DURABILITY);
		ASCENT_BASE_CHANCE = ConfigGetter.from(config).getDouble("ascent.base-chance", ASCENT_BASE_CHANCE);
		ASCENT_FEE = ConfigGetter.from(config).getInt("ascent.fee", ASCENT_FEE);
		UPGRADE_BASE_CHANCE = ConfigGetter.from(config).getDouble("upgrade.base-chance", UPGRADE_BASE_CHANCE);
		UPGRADE_FEE = ConfigGetter.from(config).getInt("upgrade.fee", UPGRADE_FEE);
		UPGRADE_EXP = ConfigGetter.from(config).getInt("upgrade.exp-per-material", UPGRADE_EXP);
		ENHANCE_FEE = ConfigGetter.from(config).getInt("enhance.fee", ENHANCE_FEE);
		ART_BASE_MAIN_STAT = ConfigGetter.from(config).getInt("artifact.base-main-stat", ART_BASE_MAIN_STAT);
		ART_BASE_SUB_STAT = ConfigGetter.from(config).getInt("artifact.base-sub-stat", ART_BASE_SUB_STAT);
		ART_STAT_RANGE = ConfigGetter.from(config).getDouble("artifact.stat-range", ART_STAT_RANGE);
		KEEP_STONE = ItemStackUtils.buildItem(Objects.requireNonNull(config.getConfigurationSection("keep-stone")));
		GLOBAL_SPEAKER = ItemStackUtils.buildItem(Objects.requireNonNull(config.getConfigurationSection("global-speaker")));
		DIE_EXP_LOST_PERCENT = config.getLong("die-exp-lost-percent", 25);

		// Art tiers up
		artTierUps.clear();
		ConfigGetter.from(config).getStringList("artifact.tier-up", Lists.newArrayList()).forEach(s -> {
			artTierUps.put(Tier.valueOf(s.split(":")[0]), Double.valueOf(s.split(":")[1]));
		});
		
		// Models
		models.clear();
		File iF = new File(plugin.getDataFolder() + "//items");
		if (!iF.exists()) {
			InputStream is = plugin.getResource("example-item.yml");
			File file = new File(plugin.getDataFolder() + "//items//example-item.yml");
			try {
				FileUtils.copyInputStreamToFile(is, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			iF.mkdirs();
		}
		for (File f : iF.listFiles()) {
			FileConfiguration ic = YamlConfiguration.loadConfiguration(f);
			String id = f.getName().replace(".yml", "");
			models.put(id, readModel(ic));
		}
		
		// Wishes
		wishes.clear();
		iF = new File(plugin.getDataFolder() + "//wishes");
		if (!iF.exists()) {
			InputStream is = plugin.getResource("example-wish.yml");
			File file = new File(plugin.getDataFolder() + "//wishes//example-wish.yml");
			try {
				FileUtils.copyInputStreamToFile(is, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		iF.mkdirs();
		for (File f : iF.listFiles()) {
			FileConfiguration ic = YamlConfiguration.loadConfiguration(f);
			String id = f.getName().replace(".yml", "");
			wishes.put(id, readWish(id, ic));
		}

		// Slaves
		slaves.clear();
		iF = new File(plugin.getDataFolder() + "//slaves");
		if (!iF.exists()) {
			InputStream is = plugin.getResource("example-slave.yml");
			File file = new File(plugin.getDataFolder() + "//slaves//example-slave.yml");
			try {
				FileUtils.copyInputStreamToFile(is, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		iF.mkdirs();
		for (File f : iF.listFiles()) {
			FileConfiguration ic = YamlConfiguration.loadConfiguration(f);
			String id = f.getName().replace(".yml", "");
			slaves.put(id, readSlave(ic));
		}

		// Grade exp
		gradeExps.clear();
		config.getConfigurationSection("item.grade-exp").getKeys(false).forEach(gs -> {
			Grade g = Grade.valueOf(gs);
			int exp = config.getInt("item.grade-exp." + gs);
			gradeExps.put(g, exp);
		});
		
		// Enhance rates
		enhanceRates.clear();
		config.getStringList("enhance.chance").forEach(s -> {
			int min = Integer.valueOf(s.split(":")[0].split("-")[0]);
			int max = Integer.valueOf(s.split(":")[0].split("-")[1]);
			double chance = Double.valueOf(s.split(":")[1]);
			for (int i = min ; i <= max ; i++) enhanceRates.put(i, chance);
		});
		
		// Art-set-stat
		artStatSetUp.clear();
		config.getStringList("artifact.stat-up").forEach(s -> {
			artStatSetUp.put(Integer.valueOf(s.split(":")[0]), Double.valueOf(s.split(":")[1]));
		});
		
		// Mobs
		mobLevels.clear();
		mobTypes.clear();
		config.getConfigurationSection("mob").getKeys(false).forEach(id -> {
			int level = config.getInt("mob." + id + ".level");
			MobType type = MobType.valueOf(config.getString("mob." + id + ".type").toUpperCase());
			mobLevels.put(id, level);
			mobTypes.put(id, type);
		});

		// Wish keys
		wishKeys.clear();
		for (String id : config.getConfigurationSection("wish-key").getKeys(false)) {
			List<String> wishes = config.getStringList("wish-key." + id + ".wishes");
			var is = ItemStackUtils.buildItem(Objects.requireNonNull(config.getConfigurationSection("wish-key." + id + ".item")));
			wishKeys.put(id, new WishKey(wishes, is));
		}

		// Chat Prefix
		chatPrefixes.clear();
		chatPrefixDefault = config.getString("chat-prefix-default");
		for (String s : config.getStringList("chat-prefix")) {
			String permision = s.split(":")[0];
			String prefix = s.split(":")[1];
			chatPrefixes.put(permision, prefix);
		}

		// Permission
		permissions.clear();
		for (String id : config.getConfigurationSection("permissions").getKeys(false)) {
			var perm = config.getString("permissions." + id + ".permission");
			var message = config.getString("permissions." + id + ".message").replace("&", "§");
			permissions.put(id, new SantoryPermission(id, perm, message));
		}

		xmSuccess = config.getStringList("xacminh-success");
	}

	public static WishKey getWishKey(String id) {
		return wishKeys.getOrDefault(id, null);
	}

	public static Map<String, WishKey> getWishKeys() {
		return Maps.newHashMap(wishKeys);
	}
	
	public static boolean isMob(String id) {
		return mobLevels.containsKey(id);
	}
	
	public static int getLevel(String mobID) {
		return mobLevels.get(mobID);
	}
	
	public static MobType getType(String mobID) {
		return mobTypes.get(mobID);
	}

	public static SlaveModel getSlaveModel(String slaveID) {return slaves.get(slaveID);}

	private static SlaveModel readSlave(FileConfiguration config) {
		String name = config.getString("name");
		Tier tier = Tier.valueOf(config.getString("tier").toUpperCase());
		String head = config.getString("head");
		Color color = Color.fromRGB(Integer.valueOf(config.getString("color").split("-")[0]), Integer.valueOf(config.getString("color").split("-")[1]), Integer.valueOf(config.getString("color").split("-")[2]));
		Skill skill = Skill.valueOf(config.getString("skill").toUpperCase());
		WeaponType wt = WeaponType.valueOf(config.getString("weapon"));
		Map<SlaveState, List<String>> sounds = Maps.newHashMap();
		for (String k : config.getConfigurationSection("sounds").getKeys(false)) {
			sounds.put(SlaveState.valueOf(k.toUpperCase()), config.getStringList("sounds." + k));
		}
		List<String> skillDesc = ConfigGetter.from(config).getStringList("skill-desc", Lists.newArrayList()).stream().map(s -> s.replace("&", "§")).collect(Collectors.toList());

		return new SlaveModel(name, head, color, tier, skill, wt, sounds, skillDesc);
	}

	private static ItemModel readModel(FileConfiguration config) {
		String headTexture = null;
		Material m = null;
		int data = 0;
		Color color = null;
		
		if (config.contains("texture.head")) {
			headTexture = ConfigGetter.from(config).getString("texture.head", null);
		}
		else {
			m = Material.valueOf(config.getString("texture.material"));
			data = ConfigGetter.from(config).getInt("texture.data", 0);
			color = Utils.readColor((ConfigGetter.from(config).getString("texture.color", null)));
		}
		
		ItemTexture texture = new ItemTexture(m, data, headTexture, color);
		ItemType it = ItemType.valueOf(config.getString("type"));
		Element element = Element.valueOf(config.getString("element"));
		Tier tier = Tier.valueOf(config.getString("tier"));
		String name = config.getString("name");
		String desc = config.getString("desc");
		Map<Stat, Integer> stats = Maps.newLinkedHashMap();
		config.getStringList("stats").forEach(l -> {
			Stat stat = Stat.valueOf(l.split(" ")[0]);
			int value = Integer.valueOf(l.split(" ")[1]);
			stats.put(stat, value);
		});
		Map<String, String> metadata = Maps.newHashMap();
		config.getConfigurationSection("metadata").getKeys(false).forEach(id -> {
			metadata.put(id, config.getString("metadata." + id));
		});
		return new ItemModel(texture, it, element, name, tier, desc, stats, metadata);
	}
	
	private static Wish readWish(String id, FileConfiguration config) {
		String name = config.getString("name");
		String desc = config.getString("desc");
		Map<Tier, WishReward> rewards = Maps.newHashMap();
		config.getConfigurationSection("rewards").getKeys(false).forEach(ts -> {
			Tier t = Tier.valueOf(ts);
			double chance = config.getDouble("rewards." + ts + ".chance");
			List<WishRewardItem> items = config.getStringList("rewards." + ts + ".items").stream().map(s -> WishRewardItem.parse(t, s)).collect(Collectors.toList());
			WishReward wr = new WishReward(chance, items);
			rewards.put(t, wr);
		});
		Map<Tier, Integer> insures = Maps.newHashMap();
		config.getStringList("insures").forEach(s -> {
			insures.put(Tier.valueOf(s.split(":")[0]), Integer.valueOf(s.split(":")[1]));
		});
		List<LocationData> locations = config.getStringList("locations").stream().map(LocationData::parse).collect(Collectors.toList());
		return new Wish(id, name, desc, locations, rewards, insures);
	}
	
	public static Map<Tier, Double> getArtTierUp() {
		return artTierUps;
	}
	
	public static Wish getWish(String id) {
		return wishes.getOrDefault(id, null);
	}
	
	public static Map<String, Wish> getWishes() {
		return Maps.newHashMap(wishes);
	}
	
	public static ItemModel getModel(String id) {
		return Maps.newHashMap(models).getOrDefault(id, null);
	}
	
	public static Map<String, ItemModel> getModels() {
		return Maps.newHashMap(models);
	}

	
	public static boolean isPvPWorld(World w) {
		return pvpWorlds.contains(w.getName());
	}
	
	public static Map<Grade, Integer> getExpRequires() {
		return gradeExps;
	}

	public static double getDieExpLostPercent() {
		return DIE_EXP_LOST_PERCENT;
	}

	public static double getEnhanceRate(int level) {
		return enhanceRates.getOrDefault(level, 0d);
	}
	
	public static Map<Integer, Double> getArtSetUp() {
		return artStatSetUp;
	}

	public static ItemStack getKeepStone() {
		var is = KEEP_STONE.clone();
		var im = new ItemStackManager(SantoryCore.get(), is);
		return is;
	}

	public static boolean isKeepStone(ItemStack is) {
		return new ItemStackManager(SantoryCore.get(), is).compareSpecial(KEEP_STONE);
	}

	public static ItemStack getGlobalSpeaker() {
		var is = GLOBAL_SPEAKER.clone();
		var im = new ItemStackManager(SantoryCore.get(), is);
		return is;
	}

	public static boolean isGlobalSpeaker(ItemStack is) {
		return new ItemStackManager(SantoryCore.get(), is).compareSpecial(GLOBAL_SPEAKER);
	}

	public static String getChatPrefixDefault() {
		return chatPrefixDefault;
	}

	public static Map<String, String> getChatPrefixes() {
		return chatPrefixes;
	}

	public static List<String> getXmSuccess() {
		return xmSuccess;
	}

	public static boolean checkPermission(Player player, String permID) {
		if (permissions.containsKey(permID)) {
			var sp = permissions.get(permID);
			if (!player.hasPermission(sp.getPermission())) {
				player.sendMessage(sp.getMessage());
				return false;
			}
			return true;
		}
		return true;
	}
}
