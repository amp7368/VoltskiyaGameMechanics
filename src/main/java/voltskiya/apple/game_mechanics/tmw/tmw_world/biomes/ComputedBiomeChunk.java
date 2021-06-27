package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class ComputedBiomeChunk {
    private final List<Pair<BiomeType, Double>> guessedBiomes;

    public ComputedBiomeChunk(BiomeTypeBuilderRegisterBlocks.BlocksInfo blocksInfo) {
        if (blocksInfo == null) {
            guessedBiomes = null;
            return;
        }
        List<Pair<BiomeType, Double>> guesses = new ArrayList<>();
        for (BiomeType biome : BiomeTypeDatabase.getAll()) {
            guesses.add(
                    new Pair<>(
                            biome,
                            biome.guess(
                                    blocksInfo.getHeightVariation(),
                                    blocksInfo.getAverageHeight(),
                                    blocksInfo.getMaterials(),
                                    blocksInfo.getBiomes()
                            )
                    )
            );
        }
        guesses.sort(Comparator.comparingDouble((ToDoubleFunction<Pair<BiomeType, Double>>) Pair::getValue).reversed());
        this.guessedBiomes = new ArrayList<>(guesses.subList(0, Math.min(5, guesses.size())));
    }

    public List<Pair<BiomeType, Double>> getGuessedBiomes() {
        return guessedBiomes;
    }

}
