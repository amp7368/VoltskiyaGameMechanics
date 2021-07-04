package voltskiya.apple.game_mechanics.util;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class GetChunksRequest {
    private final CountDownLatch chunkCountDown;
    private Map<Pair<Integer, Integer>, ChunkSnapshot> chunks;
    private final Consumer<Map<Pair<Integer, Integer>, ChunkSnapshot>> onDone;

    public GetChunksRequest(Map<Pair<Integer, Integer>, ChunkSnapshot> chunksToLoad, Location locationToGetBiome, Consumer<Map<Pair<Integer, Integer>, ChunkSnapshot>> onDone) {
        this.chunks = chunksToLoad;
        this.onDone = onDone;
        int count = 0;
        for (ChunkSnapshot addMe : chunks.values()) if (addMe == null) count++;
        chunkCountDown = new CountDownLatch(count);
        for (Map.Entry<Pair<Integer, Integer>, ChunkSnapshot> addMe : chunks.entrySet()) {
            if (addMe.getValue() == null) {
                int x = addMe.getKey().getKey();
                int z = addMe.getKey().getValue();
                locationToGetBiome.getWorld().getChunkAtAsync(x, z, false, (chunk) -> {
                    synchronized (this) {
                        chunks.put(new Pair<>(x, z), chunk.getChunkSnapshot(true, true, true));
                        chunkCountDown.countDown();
                        if (chunkCountDown.getCount() == 0) {
                            this.onDone.accept(chunks);
                        }
                    }
                });
            }
        }
    }
}
