package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType.BiomeTypeBuilder;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;

public class MobPageGet extends InventoryGuiPageScrollableACD<TMWGui> {

    private BiomeTypeBuilder biome;

    public MobPageGet(TMWGui parent, BiomeTypeBuilder biome) {
        super(parent);
        this.biome = biome;
    }

    private void addMobs() {
        for (MobType mob : MobTypeDatabase.getAll()) {
            if (this.biome.getMob(mob) != null) {
                add(slotImpl((e) -> {
                        this.biome.addMob(mob);
                        parentRemoveSubPage();
                    }, mob.toItem()
                ));
            }
        }
    }

    @Override
    public void refreshPageItems() {
        setSlot(
            slotImpl(e -> parentRemoveSubPage(), makeItem(Material.SNOW_BLOCK, 1, "Go back", null)),
            4);
    }

    @Override
    public String getName() {
        return "Select a mob";
    }

    @Override
    public int size() {
        return 54;
    }
}
