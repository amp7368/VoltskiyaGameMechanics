package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui;

import apple.mc.utilities.inventory.gui.acd.InventoryGuiACD;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;

public class BiomeTypeGui extends InventoryGuiACD {

    public BiomeTypeGui(TMWGui parent, BiomeType.BiomeTypeBuilder biome) {
        addPage(
            new BiomeTypeGuiPageSettings(parent, biome),
            new BiomeTypeGuiPageBiomes(parent, biome),
            new BiomeTypeGuiPageMobs(parent, biome)
        );
    }
}
