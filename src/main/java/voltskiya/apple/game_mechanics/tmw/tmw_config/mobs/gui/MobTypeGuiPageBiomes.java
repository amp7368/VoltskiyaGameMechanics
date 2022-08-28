package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import java.util.List;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;

public class MobTypeGuiPageBiomes extends InventoryGuiPageScrollableACD<TMWGui> {

    private final MobType.MobTypeBuilder mob;

    public MobTypeGuiPageBiomes(TMWGui parent, MobType.MobTypeBuilder mob) {
        super(parent);
        this.mob = mob;
    }

    @Override
    public void initialize() {
        setSlot(slotImpl((e) -> parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(slotImpl((e) -> parentPrev(),
            makeItem(Material.RED_TERRACOTTA, 1, "Previous Page", null)), 0);
    }

    @Override
    public void refreshPageItems() {
        addBiomes();
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
            add(slotImpl(e -> parentAddSubPage(new BiomeTypeGui(parent, biome.toBuilder())),
                biome.toItem()));
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
}
