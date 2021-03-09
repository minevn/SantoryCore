package mk.plugin.santory.wish;

import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.slave.SlaveModel;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum WishRewardItemType {

    ITEM {
        @Override
        public ItemStack getIcon(String id) {
            return Items.build(null, id);
        }

        @Override
        public void give(String id, Player player) {
            ItemStack is = Items.build(player, id);
            player.getInventory().addItem(is);
        }
    },
    SLAVE {
        @Override
        public ItemStack getIcon(String id) {
            SlaveModel model = Configs.getSlaveModel(id);
            Tier tier = model.getTier();;
            ItemStack is = Utils.buildSkull(model.getHead());
            ItemStackUtils.setDisplayName(is, tier.getColor() + "§l" + model.getName());

            return is;
        }

        @Override
        public void give(String id, Player player) {
            Slave slave = new Slave(id, player.getName());
            Masters.add(player, slave);
            Masters.save(player);
        }
    };


    public abstract ItemStack getIcon(String id);
    public abstract void give(String id, Player player);

}
