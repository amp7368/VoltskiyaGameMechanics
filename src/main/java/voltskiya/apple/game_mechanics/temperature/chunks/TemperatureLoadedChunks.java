package voltskiya.apple.game_mechanics.temperature.chunks;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import voltskiya.apple.game_mechanics.util.GetChunksRequest;
import voltskiya.apple.game_mechanics.util.data_structures.Pair;
import voltskiya.apple.game_mechanics.util.data_structures.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TemperatureLoadedChunks {
    private static final Map<Triple<UUID, Integer, Integer>, TemperatureChunk> chunks = new HashMap<>();

    /**
     * lazily loads the temperatureified chunk
     * SHOULD NOT BE CALLED FROM THE MAIN THREAD
     *
     * @param locationToLoad the location of a block in the chunk to load
     */
    public synchronized static void load(Location locationToLoad) {
        final Triple<UUID, Integer, Integer> chunkKey = new Triple<>(locationToLoad.getWorld().getUID(), locationToLoad.getBlockX() / 16, locationToLoad.getBlockZ() / 16);
        if (chunkExists(chunkKey)) {
            Map<Pair<Integer, Integer>, ChunkSnapshot> chunksToLoad = new HashMap<>() {{
                for (int xi = -1; xi <= 1; xi++)
                    for (int zi = -1; zi <= 1; zi++)
                        put(new Pair<>(chunkKey.getY() + xi, chunkKey.getZ() + zi), null);
            }};
            new GetChunksRequest(chunksToLoad, locationToLoad, (chunksLoaded) -> {
                synchronized (chunks) {
                    chunks.put(chunkKey, new TemperatureChunk(locationToLoad, chunksLoaded));
                }
            });
        }
    }

    private static boolean chunkExists(Triple<UUID, Integer, Integer> chunkKey) {
        return !chunks.containsKey(chunkKey);
    }
}
