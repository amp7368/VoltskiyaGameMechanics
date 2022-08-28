package voltskiya.apple.game_mechanics.decay.config.template;

import static org.bukkit.Material.BRICKS;
import static org.bukkit.Material.POWDER_SNOW_BUCKET;
import static org.bukkit.Material.VINE;

import apple.mc.utilities.inventory.gui.acd.slot.ItemGuiSlotCycleable;
import apple.utilities.util.Pretty;
import org.bukkit.Material;

public enum MaterialVariantType implements ItemGuiSlotCycleable<MaterialVariantType> {
    NORMAL(BRICKS),
    GROWTH(VINE),
    SNOW(POWDER_SNOW_BUCKET);

    private final Material material;

    MaterialVariantType(Material material) {
        this.material = material;
    }

    @Override
    public MaterialVariantType[] valuesList() {
        return values();
    }

    @Override
    public Material itemMaterial() {
        return material;
    }

    @Override
    public String itemName() {
        return String.format("Material Type: %s", Pretty.upperCaseFirst(name()));
    }
}
