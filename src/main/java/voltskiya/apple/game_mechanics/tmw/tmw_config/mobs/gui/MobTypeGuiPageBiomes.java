package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGenericScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.List;

public class MobTypeGuiPageBiomes extends InventoryGuiPageScrollable {
    private MobTypeGui mobTypeGui;
    private MobType.MobTypeBuilder mob;
    private TMWGui tmwGui;

    public MobTypeGuiPageBiomes(MobTypeGui mobTypeGui, MobType.MobTypeBuilder mob, TMWGui tmwGui) {
        super(mobTypeGui);
        this.mobTypeGui = mobTypeGui;
        this.mob = mob;
        this.tmwGui = tmwGui;
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric((e) -> mobTypeGui.nextPage(-1), InventoryUtils.makeItem(Material.RED_TERRACOTTA, 1, "Previous Page", null)
        ), 0);
        addBiomes();
        setSlots();
    }

    @Override
    public void fillInventory() {
        addBiomes();
        super.fillInventory();
    }

    private void addBiomes() {
        clear();
        List<BiomeType> allBiomes = BiomeTypeDatabase.getAll();
        allBiomes.removeIf(b -> {
            for (MobType m : b.getSpawns().keySet()) {
                if (m.getName().equals(mob.getName())) {
                    return false;
                }
            }
            return true;
        });
        for (BiomeType biome : allBiomes) {
            add(new InventoryGuiSlotGenericScrollable(e -> e.getWhoClicked().openInventory(
                    new BiomeTypeGui(tmwGui, biome.toBuilder()).getInventory()
            ), biome.toItem()));
        }
    }


    @Override
    public String getName() {
        return "Biomes this mob spawns in";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }
}
