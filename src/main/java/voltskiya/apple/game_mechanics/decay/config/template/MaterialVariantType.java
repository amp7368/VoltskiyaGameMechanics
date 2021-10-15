package voltskiya.apple.game_mechanics.decay.config.template;

import apple.utilities.util.Pretty;
import org.bukkit.Material;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.SlotCycleable;

import static org.bukkit.Material.*;

public enum MaterialVariantType implements SlotCycleable<MaterialVariantType> {
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
