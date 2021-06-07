package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilder;

public class BiomeType {
    private final BiomeTypeBuilder.BiomeIcon icon;

    public BiomeType(BiomeTypeBuilder.BiomeIcon icon) {
        this.icon = icon;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public BiomeTypeBuilder toBuilder() {
        return new BiomeTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    public BiomeTypeBuilder.BiomeIcon getIcon() {
        return icon;
    }
}
