package voltskiya.apple.game_mechanics.decay.world;

import voltskiya.apple.game_mechanics.decay.storage.DecayBlock;

import java.util.function.BiFunction;

public enum DecayErosionType {
    // overgrowth
    JUNGLE_OVERGROWTH(null),
    FOREST_OVERGROWTH(null),
    WATER_MOLD(null),

    // collapse
    STRUCTURE_COLLAPSE(null),
    CAVE_IN(null),

    // erosion
    EXPLOSION(DecayAlgorithm::explosion),
    WIND(null),
    WATER_EROSION(null),

    // build-up
    SAND_BUILDUP(null),
    SNOW_BUILDUP(null);

    private BiFunction<DecayBlock[][][], Integer, float[][][]> decayFunction;

    DecayErosionType(BiFunction<DecayBlock[][][], Integer, float[][][]> decayFunction) {
        this.decayFunction = decayFunction;
    }

    public float[][][] doDecay(DecayBlock[][][] blocksToDecay, int force) {
        return this.decayFunction.apply(blocksToDecay, force);
    }
}
