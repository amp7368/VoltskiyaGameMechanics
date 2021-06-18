package voltskiya.apple.game_mechanics.tmw.tmw_world.biomes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeWatchPlayerListener implements Listener {
    private final Map<UUID, BiomeWatchPlayer> watches = new HashMap<>();
    private static BiomeWatchPlayerListener instance;

    public BiomeWatchPlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        instance = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if (!watches.containsKey(playerUUID))
                watches.put(playerUUID, new BiomeWatchPlayer(player));
        }
    }

    public synchronized void leave(UUID playerUuidToLeave) {
        watches.remove(playerUuidToLeave);
    }

    public static BiomeWatchPlayerListener get() {
        return instance;
    }

    @EventHandler
    public synchronized void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!watches.containsKey(playerUUID))
            watches.put(playerUUID, new BiomeWatchPlayer(player));
    }
}

