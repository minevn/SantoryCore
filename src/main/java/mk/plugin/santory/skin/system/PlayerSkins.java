package mk.plugin.santory.skin.system;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import mk.plugin.playerdata.storage.PlayerDataAPI;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skin.SkinType;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.traveler.TravelerStorage;
import mk.plugin.santory.utils.ItemStackManager;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.Map;

public class PlayerSkins {

    public static final String KEY = "skin-data";

    private static Map<String, List<Entity>> equips = Maps.newConcurrentMap();

    public static PlayerSkin get(String name) {
        var pd = PlayerDataAPI.get(name, TravelerStorage.HOOK);
        if (pd.hasData(KEY)) return new GsonBuilder().create().fromJson(pd.getValue(KEY), PlayerSkin.class);
        return new PlayerSkin(name, Lists.newArrayList());
    }

    public static void save(PlayerSkin skindata) {
        var pd = PlayerDataAPI.get(skindata.getPlayer(), TravelerStorage.HOOK);
        pd.set(KEY, new GsonBuilder().create().toJson(skindata));
        pd.save();
    }

    public static List<Entity> getEquips(Player p) {
        return equips.getOrDefault(p.getName(), null);
    }

    public static void destroy(Player p) {
        // Head check
        var skindata = get(p.getName());
        var ishead = p.getInventory().getHelmet();
        if (ishead != null) {
            boolean has = false;
            for (Item skin : skindata.getSkins()) {
                if (skin == null) continue;

                var texture = skin.getModel().getTexture();
                if (texture.getMaterial() == ishead.getType() && texture.getData() == ishead.getItemMeta().getCustomModelData()) {
                    has = true;
                    break;
                }
            }
            if (has) p.getInventory().setHelmet(null);
        }


        // Armorstand check
        if (equips.containsKey(p.getName())) {
            for (Entity e : equips.get(p.getName())) {
                e.remove();
            }
            equips.remove(p.getName());
        }
    }

    public static void equip(Player p) {
        var skindata = get(p.getName());

        List<ItemStack> hands = Lists.newArrayList();
        for (Item item : skindata.getSkins()) {
            if (item == null) continue;
            var skin = Skins.of(item);
            var is = Items.build(p, item);
            if (skin.getType() == SkinType.OFFHAND) hands.add(is);
        }

        // Heads
        var ishead = p.getInventory().getHelmet();
        if (Skins.read(ishead) != null) {
            var item = Items.read(ishead);

            boolean has = false;
            for (Item skin : skindata.getSkins()) {
                if (skin == null) continue;
                if (item.getModelID().equals(skin.getModelID())) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                p.getInventory().addItem(ishead);
            }
        }
        p.getInventory().setHelmet(null);
        for (Item item : skindata.getSkins()) {
            if (item == null) continue;
            var skin = Skins.of(item);
            if (skin.getType() == SkinType.HEAD) {
                // Clear data (if has bug, that item useless)
                var is = Items.build(p, item);
                var ism = new ItemStackManager(SantoryCore.get(), is);
                ism.clearTags();

                p.getEquipment().setHelmet(is);
                break;
            }
        }

        List<ArmorStand> alist = Lists.newArrayList();

        int amount = hands.size() % 2 == 0 ? hands.size() / 2 : hands.size() / 2 + 1;
        if (amount != 0) {
            for (int i = 0; i < amount; i++) {
                alist.add(spawn(p));
            }

            // Hands
            for (int i = 0; i < alist.size(); i++) {
                var as = alist.get(i);
                for (int i1 = 0; i1 < 2; i1++) {
                    if (i * 2 + i1 >= hands.size()) break;
                    var is = hands.get(i * 2 + i1);
                    if (is == null) continue;
                    var slot = i1 == 0 ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
                    as.getEquipment().setItem(slot, is);
                }
            }
        }

        // E list
        List<Entity> elist = Lists.newArrayList(alist);

        // Nametag
        if (alist.size() > 0) {
            var sb = (Snowball) p.getWorld().spawnEntity(p.getLocation(), EntityType.SNOWBALL);
            sb.setCustomName(p.getName());
            sb.setCustomNameVisible(true);
            p.addPassenger(sb);

            // Hide packet
            var packet = new PacketPlayOutEntityDestroy(sb.getEntityId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

            elist.add(sb);
        }


        equips.put(p.getName(), elist);
    }

    private static ArmorStand spawn(Player p) {
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
        if (!NPCSkins.isIllegal(as)) return false;
        for (List<Entity> list : equips.values()) {
            if (list.contains(as)) return false;
        }
        return true;
    }

}
