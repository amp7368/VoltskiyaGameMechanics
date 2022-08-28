package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;

public class MobTypeGuiPageSettings extends InventoryGuiPageImplACD<TMWGui> {

    private final MobType.MobTypeBuilder mob;

    public MobTypeGuiPageSettings(TMWGui parent, MobType.MobTypeBuilder mob) {
        super(parent);
        this.mob = mob;
        setSlot(new MobNameSlot(), 0);
        setSlot(new SaveSlot(), 4);
        setSlot(slotImpl((e) -> parent.parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl((e) -> mob.togglePersistent(),
            makeItem(Material.COBWEB, 1, "Is persistent?",
                Collections.singletonList(mob.isPersistent() ? "Yes" : "No"))), 36);
        setSlot(new DespawnsAfterSlot(), 37);
        setSlot(new IsSpawnWithLineOfSight(), 45);
        setSlot(new TimeToSpawnSlot(), 49);
        setSlot(new HighestYValue(), 44);
        setSlot(new LowestYValue(), 53);
        setSlot(new GroupSlot(), 46);

        setSlot(slotImpl((e1) -> parentRemoveSubPage(),
            makeItem(Material.LIGHT_BLUE_TERRACOTTA, 1, "Back", null)), 6);
    }


    @Override
    public String getName() {
        return "Mob settings";
    }

    @Override
    public int size() {
        return 54;
    }

    private class MobNameSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            parentAddSubPage(new MobNamePage(parent, mob));
        }

        @Override
        public ItemStack getItem() {
            return mob.getIconItem();
        }

    }

    private class SaveSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (mob.isDone()) {
                MobTypeDatabase.addMob(mob.build());
                parentRemoveSubPage();
            } else {
                event.getWhoClicked().sendMessage("Make sure the mob has a name on the egg");
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.LIME_CONCRETE, 1, "Save",
                Collections.singletonList("list of things needed before saving"));
        }
    }

    private class IsPersistentSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            mob.togglePersistent();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.COBWEB, 1, "Is persistent?",
                Collections.singletonList(mob.isPersistent() ? "Yes" : "No"));
        }
    }

    private class DespawnsAfterSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                mob.changeDespawnsAfterHours(event.getClick().isLeftClick() ? .1 : -.1);
            } else {
                mob.changeDespawnsAfterHours(event.getClick().isLeftClick() ? 1 : -1);
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.WITHER_ROSE, 1,
                String.format("Despawns after %.1f real life hours", mob.getDespawnsAfterHours()),
                Arrays.asList("Shift left click: +0.1", "Normal left click: +1",
                    "Shift right click: -0.1", "Normal right click: -1"));
        }
    }

    private class IsSpawnWithLineOfSight implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            mob.toggleSpawnWithLineOfSight();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.END_ROD, 1, "Is spawn with line of sight?",
                Collections.singletonList(mob.isSpawnWithLineOfSight() ? "Yes" : "No"));
        }
    }


    private class TimeToSpawnSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            parentAddSubPage(new MobTypeGuiTimeToSpawnPage(parent, mob.getTimeToSpawn()));
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "Time to spawn", null);
        }
    }

    private class HighestYValue implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    mob.changeHighestYLevel(5);
                } else {
                    mob.changeHighestYLevel(-5);
                }
            } else {
                if (event.getClick().isLeftClick()) {
                    mob.changeHighestYLevel(1);
                } else {
                    mob.changeHighestYLevel(-1);
                }
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.GRASS_BLOCK, 1,
                String.format("Highest Y Level: %d", mob.getHighestYLevel()),
                Arrays.asList("Shift left click: +5", "Normal left click: +1",
                    "Shift right click: -5", "Normal right click: -1"));
        }
    }

    private class LowestYValue implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick().isShiftClick()) {
                if (event.getClick().isLeftClick()) {
                    mob.changeLowestYLevel(5);
                } else {
                    mob.changeLowestYLevel(-5);
                }
            } else {
                if (event.getClick().isLeftClick()) {
                    mob.changeLowestYLevel(1);
                } else {
                    mob.changeLowestYLevel(-1);
                }
            }
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.BEDROCK, 1,
                String.format("Lowest Y Level: %d", mob.getLowestYLevel()),
                Arrays.asList("Shift left click: +5", "Normal left click: +1",
                    "Shift right click: -5", "Normal right click: -1"));
        }
    }

    private class GroupSlot implements ItemGuiSlotACD {

        @Override
        public void dealWithClick(InventoryClickEvent inventoryClickEvent) {
            parentAddSubPage(new GroupSlotGuiPage(parent, mob));
        }

        @Override
        public ItemStack getItem() {
            return makeItem(Material.MELON_SEEDS, 1, "Groups", null);
        }
    }
}
