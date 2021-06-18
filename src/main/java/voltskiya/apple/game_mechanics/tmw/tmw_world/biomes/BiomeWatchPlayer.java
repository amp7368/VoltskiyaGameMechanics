package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.BaseBlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.temperature.chunks.TemperatureChunk;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.util.data_structures.Pair;

import java.util.*;
import java.util.function.ToDoubleFunction;

import static voltskiya.apple.game_mechanics.temperature.chunks.TemperatureChunk.BLOCKS_IN_A_CHUNK;

public class BiomeWatchPlayer implements Runnable {
    //TODO change the interval to config
    private static final long WATCH_PLAYER_INTERVAL = 20;
    private static final int PREVIOUS_BIOMES_COUNT = (int) (20 * 60 / WATCH_PLAYER_INTERVAL);
    private final Player player;
    private final List<ComputedBiomeChunk> previousBiomes = new ArrayList<>();

    public BiomeWatchPlayer(Player player) {
        this.player = player;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);
    }

    // load the
    @Override
    public void run() {
        if (!this.player.isOnline()) {
            // player left the game. just let this die and tell the listener that spawned us that we're dead
            BiomeWatchPlayerListener.get().leave(player.getUniqueId());
            return;
        }
        final Location playerLocation = player.getLocation();
        Chunk chunk = playerLocation.getChunk();
        previousBiomes.add(new ComputedBiomeChunk(playerLocation.getChunk(),
                BiomeTypeBuilderRegisterBlocks.compute(
                        playerLocation.getChunk(),
                        new HashSet<>(),
                        playerLocation.getBlockX() - chunk.getX() * TemperatureChunk.BLOCKS_IN_A_CHUNK,
                        playerLocation.getBlockY(),
                        playerLocation.getBlockZ() - chunk.getZ() * TemperatureChunk.BLOCKS_IN_A_CHUNK
                ))
        );
        while (previousBiomes.size() > PREVIOUS_BIOMES_COUNT) previousBiomes.remove(0);
        BiomeType finalBiome = finalizeGuesses();
        TextComponent msg = new TextComponent();
        msg.setText(finalBiome == null ? "null" : finalBiome.getName());
        player.sendActionBar(msg);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, WATCH_PLAYER_INTERVAL);
    }

    @Nullable
    private BiomeType finalizeGuesses() {
        final int size = previousBiomes.size();
        Map<BiomeType, Double> guesses = new HashMap<>();
        final double computedMaxSize = Math.sqrt(Math.sqrt(size));
        for (int i = 0; i < size; i++) {
            final List<Pair<BiomeType, Double>> guessedBiomes = previousBiomes.get(i).guessedBiomes;
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

    private static class ComputedBiomeChunk {
        private final BiomeTypeBuilderRegisterBlocks.BlocksInfo blocksInfo;
        private final int chunkX;
        private final int chunkZ;
        private List<Pair<BiomeType, Double>> guessedBiomes;

        public ComputedBiomeChunk(Chunk chunk, BiomeTypeBuilderRegisterBlocks.BlocksInfo blocksInfo) {
            this.chunkX = chunk.getX();
            this.chunkZ = chunk.getZ();
            this.blocksInfo = blocksInfo;
            this.guessBiomes();
        }

        private void guessBiomes() {
            if (this.blocksInfo == null) {
                guessedBiomes = null;
                return;
            }
            List<Pair<BiomeType, Double>> guesses = new ArrayList<>();
            for (BiomeType biome : BiomeTypeDatabase.getAll()) {
                guesses.add(
                        new Pair<>(
                                biome,
                                biome.guess(
                                        this.blocksInfo.getHeightVariation(),
                                        this.blocksInfo.getAverageHeight(),
                                        this.blocksInfo.getMaterials(),
                                        this.blocksInfo.getBiomes()
                                )
                        )
                );
            }
            guesses.sort(Comparator.comparingDouble((ToDoubleFunction<Pair<BiomeType, Double>>) Pair::getValue).reversed());
            this.guessedBiomes = new ArrayList<>(guesses.subList(0, Math.min(5, guesses.size())));
        }
    }
}
