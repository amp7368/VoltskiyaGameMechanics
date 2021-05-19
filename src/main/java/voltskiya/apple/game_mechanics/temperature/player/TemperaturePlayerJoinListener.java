package voltskiya.apple.game_mechanics.temperature.player;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import voltskiya.apple.game_mechanics.temperature.chunks.TemperatureLoadedChunks;

public class TemperaturePlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TemperatureAllPlayers.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        TemperatureAllPlayers.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        final Location location = chunk.getBlock(0, 0, 0).getLocation().clone();
        new Thread(() -> TemperatureLoadedChunks.load(location)).start();
    }
}
