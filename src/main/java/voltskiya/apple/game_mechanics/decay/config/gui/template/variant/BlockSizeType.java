package voltskiya.apple.game_mechanics.decay.config.gui.template.variant;

import static org.bukkit.Material.COBBLESTONE_WALL;
import static org.bukkit.Material.OAK_BUTTON;
import static org.bukkit.Material.OAK_FENCE;
import static org.bukkit.Material.OAK_PLANKS;
import static org.bukkit.Material.OAK_PRESSURE_PLATE;
import static org.bukkit.Material.OAK_SLAB;
import static org.bukkit.Material.OAK_STAIRS;

import apple.mc.utilities.inventory.gui.acd.slot.ItemGuiSlotCycleable;
import apple.utilities.util.Pretty;
import org.bukkit.Material;

public enum BlockSizeType implements ItemGuiSlotCycleable<BlockSizeType> {
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
