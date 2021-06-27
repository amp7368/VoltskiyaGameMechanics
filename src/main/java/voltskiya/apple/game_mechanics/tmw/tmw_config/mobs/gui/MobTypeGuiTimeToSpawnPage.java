package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.gui.InventoryGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageSimple;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Collections;
import java.util.function.BooleanSupplier;

public class MobTypeGuiTimeToSpawnPage extends InventoryGuiPageSimple {
    private final MobTypeGui mobTypeGui;
    private final MobType.TimeToSpawn timeToSpawn;

    public MobTypeGuiTimeToSpawnPage(MobTypeGui mobTypeGui, MobType.TimeToSpawn timeToSpawn) {
        super(mobTypeGui);
        this.mobTypeGui = mobTypeGui;
        this.timeToSpawn = timeToSpawn;
        setSlots();
    }

    private void setSlots() {
        setSlot(new InventoryGuiSlotGeneric(e -> mobTypeGui.setTempInventory(null), InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Go back", null)), 0);
        setSlot(new ToggleFieldSlot(timeToSpawn::isDay, timeToSpawn::toggleDay, "Day"), 2);
        setSlot(new ToggleFieldSlot(timeToSpawn::isEvening, timeToSpawn::toggleEvening, "Evening"), 3);
        setSlot(new ToggleFieldSlot(timeToSpawn::isNight, timeToSpawn::toggleNight, "Night"), 4);
        setSlot(new ToggleFieldSlot(timeToSpawn::isMorning, timeToSpawn::toggleMorning, "Morning"), 5);
    }

    @Override
    public String getName() {
        return "Time To Spawn";
    }

    @Override
    public int size() {
        return 9;
    }

    private class ToggleFieldSlot implements InventoryGui.InventoryGuiSlot {
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
            update();
        }

        @Override
        public ItemStack getItem() {
            return InventoryUtils.makeItem(
                    is.getAsBoolean() ? Material.YELLOW_STAINED_GLASS_PANE : Material.BLACK_STAINED_GLASS,
                    1,
                    itemName,
                    Collections.singletonList("Is spawning now? " + (is.getAsBoolean() ? "yes" : "no")));
        }
    }
}
