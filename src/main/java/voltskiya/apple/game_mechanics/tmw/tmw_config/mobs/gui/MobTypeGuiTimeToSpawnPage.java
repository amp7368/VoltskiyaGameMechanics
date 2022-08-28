package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.Collections;
import java.util.function.BooleanSupplier;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType.TimeToSpawn;

public class MobTypeGuiTimeToSpawnPage extends InventoryGuiPageImplACD<TMWGui> {

    private final MobType.TimeToSpawn timeToSpawn;

    public MobTypeGuiTimeToSpawnPage(TMWGui parent, TimeToSpawn timeToSpawn) {
        super(parent);
        this.timeToSpawn = timeToSpawn;
    }

    @Override
    public void initialize() {
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, 1, "Go back", null)), 0);
        setSlot(new ToggleFieldSlot(timeToSpawn::isDay, timeToSpawn::toggleDay, "Day"), 2);
        setSlot(new ToggleFieldSlot(timeToSpawn::isEvening, timeToSpawn::toggleEvening, "Evening"),
            3);
        setSlot(new ToggleFieldSlot(timeToSpawn::isNight, timeToSpawn::toggleNight, "Night"), 4);
        setSlot(new ToggleFieldSlot(timeToSpawn::isMorning, timeToSpawn::toggleMorning, "Morning"),
            5);
    }

    @Override
    public String getName() {
        return "Time To Spawn";
    }

    @Override
    public int size() {
        return 9;
    }

    private class ToggleFieldSlot implements ItemGuiSlotACD {

        private final BooleanSupplier is;
        private final Runnable toggle;
        private final String itemName;

        public ToggleFieldSlot(BooleanSupplier is, Runnable toggle, String itemName) {
            this.is = is;
            this.toggle = toggle;
            this.itemName = itemName;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            toggle.run();
        }

        @Override
        public ItemStack getItem() {
            return makeItem(is.getAsBoolean() ? Material.YELLOW_STAINED_GLASS_PANE
                : Material.BLACK_STAINED_GLASS, 1, itemName, Collections.singletonList(
                "Is spawning now? " + (is.getAsBoolean() ? "yes" : "no")));
        }
    }
}
