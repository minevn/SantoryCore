package mk.plugin.santory.skills;

import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.weapon.Weapon;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class WSHeavyHit implements SkillExecutor  {
	
	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		ItemStack is = player.getInventory().getItemInMainHand();
		if (!Items.is(is)) return;
		Item item = Items.read(is);
		ItemModel model = item.getModel();
		
		if (model.getType() == ItemType.WEAPON) {
			Weapon w = Weapon.parse(model);
			if (w.getType().isShooter()) w.getType().getShooter().shoot(player, new Damage(Travelers.getStatValue(player, Stat.DAMAGE), DamageType.ATTACK), player.getLocation().getDirection().multiply(3), player.getLocation().add(player.getLocation().getDirection().multiply(3)).add(0, 1.5, 0));
			// Attack
			else {
				double range = Utils.getRange(item);
				LivingEntity target = Utils.getTarget(player, range);
				if (target == null) return;
				Damages.damage(player, target, new Damage(damage, DamageType.ATTACK), 0);
			}
		}
		
	}
	
}
