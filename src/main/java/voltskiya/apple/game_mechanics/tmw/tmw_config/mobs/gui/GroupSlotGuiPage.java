package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import java.util.Arrays;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType.MobTypeBuilder;

class GroupSlotGuiPage extends InventoryGuiPageScrollableACD<TMWGui> {


    private final MobTypeBuilder mob;

    public GroupSlotGuiPage(TMWGui parent, MobTypeBuilder mob) {
        super(parent);
        this.mob = mob;
    }

    private void addGroups() {
        clear();
        for (int i = 0; i < mob.groups.size(); i++) {
            int finalI = i;
            int group = mob.groups.get(finalI);
            add(slotImpl(e -> {
                if (e.getClick().isShiftClick()) {
                    mob.groups.remove(finalI);
                } else if (e.getClick().isLeftClick()) {
                    mob.groupIncrement(finalI, 1);
                } else {
                    mob.groupIncrement(finalI, -1);
                }
                refresh();
            }, makeItem(Material.MELON_SLICE, group, "Equal chance for " + group + " grouping",
                Arrays.asList("Left click to increment", "Right click to decrement",
                    "Shift click to remove"))));
        }
    }

    @Override
    public void initialize() {
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Go back", null)), 4);
        setSlot(slotImpl(e -> {
            mob.addGroup();
            refresh();
        }, makeItem(Material.MELON_SEEDS, 1, "Add a new group possibility", null)), 0);

    }

    @Override
    public void refreshPageItems() {
        addGroups();
    }

    @Override
    public String getName() {
        return "Mob Groups";
    }

    @Override
    public int size() {
        return 36;
    }
}
