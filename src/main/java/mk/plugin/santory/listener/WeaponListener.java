package mk.plugin.santory.listener;

import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.event.ItemToggleEvent;
import mk.plugin.santory.event.PlayerSkillExecuteEvent;
import mk.plugin.santory.item.*;
import mk.plugin.santory.item.weapon.Weapon;
import mk.plugin.santory.skill.Skill;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponListener implements Listener {
	
	private static final Map<String, Long> cooldownAttack = new HashMap<String, Long> ();
	
	public static boolean isCooldownAttack(Player player) {
		if (!cooldownAttack.containsKey(player.getName())) return false;
        return cooldownAttack.get(player.getName()) >= System.currentTimeMillis();
    }
	
	public static void setCooldownAttack(Player player, long timeMilis, ItemStack item) {
		if (item != null) player.setCooldown(item.getType(), Math.max(1, Long.valueOf(timeMilis / 50).intValue()));
		cooldownAttack.put(player.getName(), System.currentTimeMillis() + timeMilis);	
	}
	
	public static void removeCooldownAttack(Player player) {
		cooldownAttack.remove(player.getName());
	}
	
	private static final Map<Player, Long> cooldownSkill = Maps.newHashMap();
	
	@EventHandler
	public void onWeaponSkill(ItemToggleEvent e) {
		Player player = e.getPlayer();
		if (!player.isSneaking()) return;
		
		Item item = e.getItem();
		ItemModel model = Configs.getModel(item.getModelID());
		
		if (model.getType() == ItemType.WEAPON) {
			Weapon w = Weapon.parse(model);
			Skill skill = w.getSkill();
			if (skill == null) return;
			
			// Check delay
			if (cooldownSkill.containsKey(player)) {
				if (cooldownSkill.get(player) > System.currentTimeMillis()) {
					player.sendMessage("§cKỹ năng chưa hồi xong");
					player.sendMessage("§cCòn §6" + ((cooldownSkill.get(player) - System.currentTimeMillis()) / 1000 + 1) + "s"); 
					return;
				}
			}
			cooldownSkill.put(player, System.currentTimeMillis() + skill.getCooldown() * 1000L);
			
			// Execute
			player.sendTitle("§c§l「" + skill.getName() + "」", "§7§oThực thi kỹ năng", 0, 20, 0);
			skill.getExecutor().start(getComponents(player, item));

			// Event
			Bukkit.getPluginManager().callEvent(new PlayerSkillExecuteEvent(player, skill));
		}
	}
	
	private Map<String, Object> getComponents(Player player, Item item) {
		Map<String, Object> m = Maps.newHashMap();
		m.put("player", player);
		List<Integer> l = Items.skillValues(item.getModel().getDesc());
		if (l.size() == 5) {
			m.put("scale", Double.valueOf(l.get(item.getData().getGrade().getValue() - 1)));
		}
		
		return m;
	}
	
	@EventHandler
	public void onToggle(ItemToggleEvent e) {
		Player player = e.getPlayer();
		ItemStack is = e.getItemStack();
		Item item = e.getItem();
		ItemModel model = Configs.getModel(item.getModelID());
		if (player.isSneaking()) return;
		
		if (model.getType() == ItemType.WEAPON) {
			
			// Check durability
			ItemData data = item.getData();
			if (data.getDurability() <= 0) {
				player.sendMessage("§cVũ khí có độ bền bằng 0, cần phải sữa chữa");
				return;
			}

			// Shoot
			if (model.getType() == ItemType.WEAPON) {
				Weapon w = Weapon.parse(model);
				if (w.getType().isShooter()) {
					// Check cool-down
					if (isCooldownAttack(player)) return;
					setCooldownAttack(player, Double.valueOf(Travelers.getStatValue(player, Stat.ATTACK_SPEED) * 1000).longValue(), is);
					w.getType().getShooter().shoot(player, new Damage(Travelers.getStatValue(player, Stat.DAMAGE), DamageType.ATTACK), player.getLocation().getDirection().multiply(3), player.getLocation().add(player.getLocation().getDirection().multiply(3)).add(0, 1.5, 0));
				}
				// Attack
				else {
					double range = Utils.getRange(item);
					LivingEntity target = Utils.getTarget(player, range);
					if (target == null) return;
					// Check cool-down
					if (isCooldownAttack(player)) return;
					setCooldownAttack(player, Double.valueOf(Travelers.getStatValue(player, Stat.ATTACK_SPEED) * 1000).longValue(), is);
					double dv = Travelers.getStatValue(player, Stat.DAMAGE);
					Damages.damage(player, target, new Damage(dv, DamageType.ATTACK), 0);
				}
			}
			
			
			// Minus durability
//			if (Configs.isPvPWorld(player.getWorld())) return;
//			data.setDurability(Math.max(data.getDurability() - 1, 0));
//			Items.write(player, is, item);
//			player.updateInventory();
		}
	}
	
	
}
