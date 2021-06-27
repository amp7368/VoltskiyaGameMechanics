package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.*;

public class BiomeWatchPlayer implements Runnable {
    //TODO change the interval to config
    private static final long WATCH_PLAYER_INTERVAL = 20;
    private static final int PREVIOUS_BIOMES_COUNT = (int) (20 * 60 / WATCH_PLAYER_INTERVAL);
    private final Player player;
    private final List<ComputedBiomeChunk> previousBiomes = new ArrayList<>();
    private BiomeType currentGuess;

    public BiomeWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    @Override
    public void run() {
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
        TextComponent msg = new TextComponent();
        msg.setText(currentGuess == null ? "null" : currentGuess.getName());
        player.sendActionBar(msg);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, WATCH_PLAYER_INTERVAL);
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

    public BiomeType getCurrentGuess() {
        return this.currentGuess;
    }
}
