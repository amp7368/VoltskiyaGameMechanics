package voltskiya.apple.game_mechanics.decay.config.template;

import apple.utilities.util.NumberUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.BooleanUtils;
import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockContext;
import voltskiya.apple.game_mechanics.decay.storage.deciders.DecayBlockRequirementAbstract;

public enum DecayBlockTemplateRequiredType implements DecayBlockRequirementAbstract<Boolean> {
    NONE((c, x, y, z) -> true),
    AIR_ABOVE(DecayBlockTemplateRequiredType::isSatasifiedAirAbove),
    BLOCK_BELOW(DecayBlockTemplateRequiredType::isSatasifiedBlockBelow),
    PART_OF_WALL(DecayBlockTemplateRequiredType::isSatasifiedPartOfWall);
    private final DecayBlockRequirementAbstract<Boolean> isSatisfied;

    DecayBlockTemplateRequiredType(DecayBlockRequirementAbstract<Boolean> isSatisfied) {
        this.isSatisfied = isSatisfied;
    }

    private static boolean isSatasifiedPartOfWall(DecayBlockContext context, int x, int y, int z) {
        DecayBlock[][][] blocks = context.getBlocksToDecay();
        for (int focus = 0; focus < 3; focus++) {
            int isWall = 0;
            for (int xi = -1; xi <= 1; xi++) {
                if (focus != 0 && xi != 1) continue;
                for (int yi = -1; yi <= 1; yi++) {
                    if (focus != 1 && yi != 1) continue;
                    for (int zi = -1; zi <= 1; zi++) {
                        if (focus != 2 && zi != 1) continue;
                        int xii = xi + x;
                        int yii = yi + y;
                        int zii = zi + z;
                        if (BooleanUtils.and(NumberUtils.betweenMultiple(0, blocks.length, xii, yii, zii))) {
                            Material estimate = blocks[xii][yii][zii].estimate();
                            if (estimate != null && !estimate.isAir() && estimate.getBlastResistance() != 0) {
                                if (++isWall >= 4) return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSatasifiedBlockBelow(DecayBlockContext context, int x, int y, int z) {
        DecayBlock[][][] blocks = context.getBlocksToDecay();
        int isWall = 0;
        for (int xi = -1; xi <= 1; xi++) {
            for (int zi = -1; zi <= 1; zi++) {
                int xii = xi + x;
                int yii = -1 + y;
                int zii = zi + z;
                if (BooleanUtils.and(NumberUtils.betweenMultiple(0, blocks.length, xii, yii, zii))) {
                    Material estimate = blocks[xii][yii][zii].estimate();
                    if (estimate != null && !estimate.isAir()) {
                        if (++isWall >= 3) return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isSatasifiedAirAbove(DecayBlockContext context, int x, int y, int z) {
        DecayBlock[][][] blocks = context.getBlocksToDecay();
        int isAir = 0;
        for (int xi = -1; xi <= 1; xi++) {
            for (int zi = -1; zi <= 1; zi++) {
                int xii = xi + x;
                int yii = 1 + y;
                int zii = zi + z;
                if (BooleanUtils.and(NumberUtils.betweenMultiple(0, blocks.length, xii, yii, zii))) {
                    Material estimate = blocks[xii][yii][zii].estimate();
                    if (estimate == null || estimate.isAir() || estimate.getBlastResistance() == 0) {
                        if (++isAir >= 4) return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Boolean decide(DecayBlockContext context, int x, int y, int z) {
        return isSatisfied.decide(context, x, y, z);
    }
}
