package voltskiya.apple.game_mechanics.decay.config.gui.template.variant;

import apple.utilities.util.Pretty;
import org.bukkit.Material;
import voltskiya.apple.utilities.util.gui.acd.slot.cycle.SlotCycleable;

import static org.bukkit.Material.*;

public enum BlockSizeType implements SlotCycleable<BlockSizeType> {
    TINY(OAK_BUTTON),
    THIN(OAK_PRESSURE_PLATE),
    SLAB(OAK_SLAB),
    FENCE(OAK_FENCE),
    WALL(COBBLESTONE_WALL),
    STAIR(OAK_STAIRS),
    FULL(OAK_PLANKS);

    private final Material material;

    BlockSizeType(Material material) {
        this.material = material;
    }

    @Override
    public BlockSizeType[] valuesList() {
        return values();
    }

    @Override
    public Material itemMaterial() {
        return material;
    }

    @Override
    public String itemName() {
        return String.format("Block type: %s", Pretty.upperCaseFirst(name()));
    }

}
