package voltskiya.apple.game_mechanics.util.minecraft;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.util.data_structures.BinaryTree;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.bukkit.Material.*;
import static org.bukkit.Material.STRIPPED_SPRUCE_LOG;

public class MaterialUtils {
    private final static Collection<Material> ARROWS = Arrays.asList(
            ARROW,
            SPECTRAL_ARROW,
            TIPPED_ARROW
    );
    private final static Collection<Material> BUTTONS = new HashSet<>(Arrays.asList(
            BIRCH_BUTTON,
            ACACIA_BUTTON,
            DARK_OAK_BUTTON,
            JUNGLE_BUTTON,
            SPRUCE_BUTTON,
            OAK_BUTTON,
            WARPED_BUTTON,
            CRIMSON_BUTTON,
            POLISHED_BLACKSTONE_BUTTON,
            STONE_BUTTON
    ));
    private final static Collection<Material> TRAP_DOORS = new HashSet<>(Arrays.asList(
            BIRCH_TRAPDOOR,
            ACACIA_TRAPDOOR,
            DARK_OAK_TRAPDOOR,
            JUNGLE_TRAPDOOR,
            SPRUCE_TRAPDOOR,
            OAK_TRAPDOOR,
            WARPED_TRAPDOOR,
            CRIMSON_TRAPDOOR,
            IRON_TRAPDOOR
    ));
    private final static Collection<Material> CARPETS = new HashSet<>(Arrays.asList(
            WHITE_CARPET,
            ORANGE_CARPET,
            MAGENTA_CARPET,
            LIGHT_BLUE_CARPET,
            YELLOW_CARPET,
            LIME_CARPET,
            PINK_CARPET,
            GRAY_CARPET,
            LIGHT_GRAY_CARPET,
            CYAN_CARPET,
            PURPLE_CARPET,
            BLUE_CARPET,
            BROWN_CARPET,
            GREEN_CARPET,
            RED_CARPET,
            BLACK_CARPET
    ));
    private static final Collection<Material> walkThroughable = new HashSet<>(Arrays.asList(
            SNOW,
            GRASS,
            FERN,
            FLOWER_POT,
            TALL_GRASS,
            LARGE_FERN,
            REDSTONE,
            STRING,
            FIRE,
            RAIL,
            POWERED_RAIL,
            DETECTOR_RAIL,
            SOUL_FIRE
    )) {{
        addAll(TRAP_DOORS);
        addAll(BUTTONS);
        addAll(CARPETS);
    }};
    private static final Collection<Material> TREE_WOOD = new HashSet<>(Arrays.asList(
            BIRCH_WOOD,
            ACACIA_WOOD,
            DARK_OAK_WOOD,
            JUNGLE_WOOD,
            SPRUCE_WOOD,
            OAK_WOOD,
            BIRCH_LOG,
            ACACIA_LOG,
            DARK_OAK_LOG,
            JUNGLE_LOG,
            SPRUCE_LOG,
            OAK_LOG,
            STRIPPED_BIRCH_WOOD,
            STRIPPED_ACACIA_WOOD,
            STRIPPED_DARK_OAK_WOOD,
            STRIPPED_JUNGLE_WOOD,
            STRIPPED_SPRUCE_WOOD,
            STRIPPED_OAK_WOOD,
            STRIPPED_BIRCH_LOG,
            STRIPPED_ACACIA_LOG,
            STRIPPED_DARK_OAK_LOG,
            STRIPPED_JUNGLE_LOG,
            STRIPPED_SPRUCE_LOG,
            STRIPPED_OAK_LOG
    ));
    private static final Collection<Material> LEAVES = Arrays.asList(BIRCH_WOOD,
            ACACIA_LEAVES,
            DARK_OAK_LEAVES,
            JUNGLE_LEAVES,
            SPRUCE_LEAVES,
            OAK_LEAVES
    );


    public static boolean isArrow(Material m) {
        return ARROWS.contains(m);
    }

    public static boolean isBowLike(Material m) {
        return BOW == m || CROSSBOW == m;
    }

    public static boolean isPassable(Material m) {
        return m.isAir() || m == SNOW;
    }

    public static boolean isWalkThroughable(Material m) {
        return m.isAir() || walkThroughable.contains(m);
    }

    public static boolean isTree(Material m) {
        return LEAVES.contains(m) || TREE_WOOD.contains(m);
    }
}
