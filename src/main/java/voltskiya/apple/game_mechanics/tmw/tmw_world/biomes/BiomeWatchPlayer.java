package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchTickable;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.*;

public class BiomeWatchPlayer implements WatchTickable {
    private static final int WATCH_PLAYER_INTERVAL = 20;
    private static final int PREVIOUS_BIOMES_COUNT = 20 * 60 / WATCH_PLAYER_INTERVAL;
    private final Player player;
    private final List<ComputedBiomeChunk> previousBiomes = new ArrayList<>();
    @Nullable
    private BiomeType currentGuess;
    private int tickCount;

    public BiomeWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.previousBiomes.clear();
            WatchPlayerListener.get().leave(this.player.getUniqueId());
            return;
        }
        final Location playerLocation = player.getLocation();
        Chunk chunk = playerLocation.getChunk();
        previousBiomes.add(new ComputedBiomeChunk(BiomeTypeBuilderRegisterBlocks.compute(
                        playerLocation.getChunk(),
                        new HashSet<>(),
                        playerLocation.getBlockX() - chunk.getX() * TemperatureChunk.BLOCKS_IN_A_CHUNK,
                        playerLocation.getBlockY(),
                        playerLocation.getBlockZ() - chunk.getZ() * TemperatureChunk.BLOCKS_IN_A_CHUNK
                ))
        );
        while (previousBiomes.size() > PREVIOUS_BIOMES_COUNT) previousBiomes.remove(0);
        this.currentGuess = finalizeGuesses();
    }

    @Nullable
    private BiomeType finalizeGuesses() {
        final int size = previousBiomes.size();
        Map<BiomeType, Double> guesses = new HashMap<>();
        final double computedMaxSize = Math.sqrt(Math.sqrt(size));
        for (int i = 0; i < size; i++) {
            final List<Pair<BiomeType, Double>> guessedBiomes = previousBiomes.get(i).getGuessedBiomes();
            if (guessedBiomes == null) continue;
            for (Pair<BiomeType, Double> guess : guessedBiomes) {
                final double scoreToAdd = (computedMaxSize - Math.sqrt(Math.sqrt(size - i))) * guess.getValue();
                guesses.compute(guess.getKey(), (b, score) -> score == null ? scoreToAdd : score + scoreToAdd);
            }
        }
        BiomeType bestBiome = null;
        double bestScore = -1;
        for (Map.Entry<BiomeType, Double> guess : guesses.entrySet()) {
            if (guess.getValue() > bestScore) {
                bestBiome = guess.getKey();
                bestScore = guess.getValue();
            }
        }
        return bestBiome;
    }

    @Nullable
    public BiomeType getCurrentGuess() {
        return this.currentGuess;
    }

    @Override
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public void setTickCount(int i) {
        this.tickCount = i;
    }

    @Override
    public int getTicksPerRun() {
        return WATCH_PLAYER_INTERVAL;
    }
}
