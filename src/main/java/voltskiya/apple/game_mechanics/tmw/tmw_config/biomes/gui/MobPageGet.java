package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

public class MobPageGet extends InventoryGuiPageScrollable {
    private final BiomeTypeGuiPageMobs giveMobInfo;
    private final BiomeTypeGui biomeTypeGui;

    public MobPageGet(BiomeTypeGuiPageMobs giveMobInfo, BiomeTypeGui biomeTypeGui) {
        super(biomeTypeGui);
        this.giveMobInfo = giveMobInfo;
        this.biomeTypeGui = biomeTypeGui;
        this.addMobs();
        this.setSlots();
    }

    private void addMobs() {
        for (MobType mob : MobTypeDatabase.getAll()) {
            if (!this.giveMobInfo.containsMob(mob))
                add(new MobTypeChooseSlot(mob));
        }
    }

    @Override
    public void setSlots() {
        setSlot(new InventoryGuiSlotGeneric(e -> biomeTypeGui.setTempInventory(null), InventoryUtils.makeItem(Material.SNOW_BLOCK, 1, "Go back", null)), 4);
        super.setSlots();
    }

    @Override
    public String getName() {
        return "Select a mob";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }

    private class MobTypeChooseSlot extends InventoryGuiSlotScrollable {
        private final MobType mob;

        public MobTypeChooseSlot(MobType mob) {
            this.mob = mob;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            giveMobInfo.addMob(mob);
            biomeTypeGui.setTempInventory(null);
        }

        @Override
        public ItemStack getItem() {
            return mob.toItem();
        }
    }
}
