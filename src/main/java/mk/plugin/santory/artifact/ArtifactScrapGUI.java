package mk.plugin.santory.artifact;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.eco.EcoType;
import mk.plugin.santory.gui.*;
import mk.plugin.santory.history.histories.ArtifactScrapHistory;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemModel;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.utils.Icon;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ArtifactScrapGUI {

    private final static List<Integer> ARTIFACT_SLOTS = Lists.newArrayList(1, 2, 3, 4);
    private final static int RESULT_SLOT = 6;
    private final static int BUTTON_SLOT = 8;

    public static Map<Integer, GUISlot> getSlots() {
        Map<Integer, GUISlot> slots = Maps.newHashMap();
        for (Integer slot : ARTIFACT_SLOTS) {
            slots.put(slot, new GUISlot("material", GUIs.getItemSlot(Icon.ARTIFACT.clone(), "§a§oĐặt Di vật"), getInputExecutor()));
        }
        slots.put(RESULT_SLOT, new GUISlot("result", GUIs.getItemSlot(getResultIcon(null, null, null), "§a§lSản phẩm")));
        slots.put(BUTTON_SLOT, new GUISlot("button", getDefaultButton(), getButtonExecutor()));

        return slots;
    }

    public static AmountChecker getAmountChecker() {
        return new AmountChecker() {
            @Override
            public boolean allowMulti(ItemStack is) {
                return false;
            }
        };
    }

    public static PlaceChecker getInputChecker() {
        return (player, is, status) -> {
            // AMULET
            if (Artifacts.is(is)) {
                var item = Items.read(is);

                if (GUIs.countPlaced("material", status) >= ARTIFACT_SLOTS.size()) {
                    player.sendMessage("§aChỉ có thể đặt §f§l" + ARTIFACT_SLOTS.size() + " §adi vật!");
                    return false;
                }

                // Check same tier
                if (GUIs.countPlaced("material", status) != 0) {
                    var ranArtIs = GUIs.getItem("material", status);
                    var ranArtItem = Items.read(ranArtIs);
                    var tier = ranArtItem.getModel().getTier();
                    if (tier != item.getModel().getTier()) {
                        player.sendMessage("§cPhải là loại §f§l" + tier.getName() + "§c vì bạn đã đặt một số Di vật loại đó từ trước");
                        return false;
                    }
                }
                status.place(player, GUIs.getEmptySlot("material", status), is);
                return true;
            }

            return false;
        };
    }

    public static PlaceExecutor getInputExecutor() {
        return (player, slot, status) -> Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
            if (GUIs.countPlaced("material", status) != ARTIFACT_SLOTS.size()) return;

            String set = null;
            String type = null;

            // Check tier
            var is = GUIs.getItem("material", status);
            var item = Items.read(is);
            var tier = item.getModel().getTier();

            // Check, type set
            boolean sameSet = true;
            boolean sameType = true;
            for (ItemStack checkIs : GUIs.getItems("material", status)) {
                var checkItem = Items.read(checkIs);
                var checkArt = Artifact.parse(checkItem.getModel());

                // First
                if (set == null) {
                    set = checkArt.getSetID();
                    type = checkItem.getModelID();
                    continue;
                }

                // Check
                if (!checkArt.getSetID().equals(set)) sameSet = false;
                if (!checkItem.getModelID().equals(type)) sameType = false;

                // Soon break
                if (!sameSet && !sameType) break;
            }

            // Clear if not same
            if (!sameSet) set = null;
            if (!sameType) type = null;

            // Set result
            status.getInventory().setItem(RESULT_SLOT, getResultIcon(tier, set, type));

            // Generate result
            String resultModel;
            if (type != null) resultModel = type;
            else {
                // Get list
                List<String> availables = Lists.newArrayList();

                // Set
                if (set != null) {
                    for (Map.Entry<String, ItemModel> e : Configs.getModels().entrySet()) {
                        var id = e.getKey();
                        var model = e.getValue();
                        if (model.getMetadata().containsKey("artifact-main-stat")) {
                            var art = Artifact.parse(model);
                            if (art.getSetID().equals(set)) availables.add(id);
                        }
                    }
                }
                // Not set -> check all artfiact
                else {
                    for (Map.Entry<String, ItemModel> e : Configs.getModels().entrySet()) {
                        var id = e.getKey();
                        var model = e.getValue();
                        if (model.getTier() == tier && model.getMetadata().containsKey("artifact-main-stat")) availables.add(id);
                    }
                }

                // Random
                resultModel = availables.get(new Random().nextInt(availables.size()));
            }

            // Set result
            var itemResult = new Item(resultModel, new ItemData(Configs.getModel(resultModel)));
            var resultIs = Items.build(player, itemResult);
            status.setData("result", resultIs);
            status.setData("fee", Configs.getArtScrapFee(tier));

            // Set cando
            status.setData("canDo", "");

            // Set button
            status.getInventory().setItem(BUTTON_SLOT, getCanButton(tier, set, type));
            Tasks.async(() -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            });
        });
    }

    public static ClickExecutor getButtonExecutor() {
        return (player, status) -> {
            // Check inventory empty slot
            if (player.getInventory().firstEmpty() == -1) {
                if (!Configs.FULL_DROP) {
                    player.sendMessage("§c§lCần chỗ trống trong kho để tránh mất đồ!");
                    return;
                }
            }

            // Check can execute
            if (!status.hasData("canDo")) {
                player.sendMessage("§cChưa thể ghép");
                return;
            }

            int fee = (int) status.getData("fee");
            if (!EcoType.MONEY.take(player, fee)) {
                player.sendMessage("§cKhông đủ tiền!");
                return;
            }

            ItemStack resultIs = (ItemStack) status.getData("result");
            player.getInventory().addItem(resultIs);

            var item = Items.read(resultIs);
            player.sendMessage("§a§lThành công, nhận " + item.getModel().getTier().getColor() + "§l" + item.getModel().getName());
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

            // History
            List<String> materials = GUIs.getItems("material", status).stream().map(is -> Items.read(is).getModelID()).collect(Collectors.toList());
            SantoryCore.get().getArtifactScrapHistory().write(player, materials, Items.read(resultIs).getModelID());

            // Clear and reopen
            GUIs.clearItems("material", status);
            GUIs.open(player, status.getGUI());
        };
    }

    public static ItemStack getResultIcon(Tier tier, String set, String type) {
        ItemStack is = Icon.RANDOM_ARTIFACT.clone();
        ItemStackUtils.setDisplayName(is, "§a§lDi vật ngẫu nhiên");
        List<String> lore = Lists.newArrayList();

        if (tier == null) lore.add("§6Loại: §f§l?");
        else lore.add("§6Loại: " + tier.getColor() + "§l" + tier.getName());

        if (set == null) lore.add("§6Bộ: §f§lNgẫu nhiên");
        else {
            lore.add("§6Bộ: §e§l" + Configs.artiSetTrans(set));
        }
        if (type == null) lore.add("§6Sản phẩm: §f§lNgẫu nhiên");
        else lore.add("§6Sản phẩm: §e§l" + Configs.getModel(type).getName());
        ItemStackUtils.setLore(is, lore);

        return is;
    }

    public static ItemStack getDefaultButton() {
        ItemStack is = new ItemStack(Material.RED_CONCRETE);
        ItemStackUtils.setDisplayName(is, "§c§lChưa thể ghép");
        List<String> lore = Lists.newArrayList();
        lore.add("§6Loại: §fKhông rõ");
        lore.add("§6Bộ: §fNgẫu nhiên");
        lore.add("§6Sản phẩm: §fNgẫu nhiên");
        lore.add("");
        lore.add("§cPhí: §f§l~$");
        ItemStackUtils.setLore(is, lore);

        return is;
    }

    public static ItemStack getCanButton(Tier tier, String set, String type) {
        ItemStack is = new ItemStack(Material.LIME_CONCRETE);
        ItemStackUtils.setDisplayName(is, "§a§lGhép Di vật");
        List<String> lore = Lists.newArrayList();
        lore.add("§6Loại: §e§l" + tier.getName());
        if (set == null) lore.add("§6Bộ: §f§lNgẫu nhiên");
        else {
            lore.add("§6Bộ: §e§l" + Configs.artiSetTrans(set));
        }
        if (type == null) lore.add("§6Sản phẩm: §f§lNgẫu nhiên");
        else lore.add("§6Sản phẩm: §e§l" + Configs.getModel(type).getName());
        lore.add("");
        lore.add("§cPhí: §f§l" + Configs.getArtScrapFee(tier) + "$");

        lore.add("");
        lore.add("§a§lCLICK để ghép");
        ItemStackUtils.setLore(is, lore);

        return is;
    }

}
