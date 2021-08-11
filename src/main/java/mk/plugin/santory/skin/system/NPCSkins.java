package mk.plugin.santory.skin.system;

import com.google.common.collect.Maps;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.skin.SkinType;
import mk.plugin.santory.skin.Skins;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.Map;

public class NPCSkins {

    private static Map<Integer, Entity> equips = Maps.newConcurrentMap();

    public static void destroy(LivingEntity le) {
        if (equips.containsKey(le.getEntityId())) {
            equips.get(le.getEntityId()).remove();
            equips.remove(le.getEntityId());
        }
        le.getEquipment().setHelmet(null);
    }

    public static void equip(LivingEntity le, String skinId) {
        destroy(le);

        var as = spawn(le);
        equips.put(le.getEntityId(), as);

        var is = Items.build(null, skinId);
        var skin = Skins.read(is);

        if (skin.getType() == SkinType.OFFHAND) {
            as.getEquipment().setItemInMainHand(is);
        }
        else if (skin.getType() == SkinType.HEAD) {
            le.getEquipment().setHelmet(is);
        }
    }

    private static ArmorStand spawn(LivingEntity p) {
        var a = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
        a.setInvisible(true);
        a.setMarker(true);
        a.setLeftArmPose(new EulerAngle(0, 0, 0));
        a.setRightArmPose(new EulerAngle(0, 0, 0));
        a.setRotation(p.getLocation().getYaw(), p.getLocation().getPitch());
        p.getPassengers().clear();
        p.addPassenger(a);

        return a;
    }

    public static boolean isIllegal(Entity as) {
        return !equips.containsValue(as);
    }

}
