package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.BiomeWatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobWatchPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchPlayerListener implements Listener {
    private static WatchPlayerListener instance;
    private final Map<UUID, BiomeWatchPlayer> biomeWatches = new HashMap<>();
    private final Map<UUID, MobWatchPlayer> mobWatches = new HashMap<>();

    public WatchPlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        instance = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if (!biomeWatches.containsKey(playerUUID))
                biomeWatches.put(playerUUID, new BiomeWatchPlayer(player));
            if (!mobWatches.containsKey(playerUUID))
                mobWatches.put(playerUUID, new MobWatchPlayer(player));
        }
    }

    public static WatchPlayerListener get() {
        return instance;
    }

    public synchronized void leave(UUID playerUuidToLeave) {
        biomeWatches.remove(playerUuidToLeave);
        mobWatches.remove(playerUuidToLeave);
    }

    @EventHandler
    public synchronized void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!biomeWatches.containsKey(playerUUID))
            biomeWatches.put(playerUUID, new BiomeWatchPlayer(player));
        if (!mobWatches.containsKey(playerUUID))
            mobWatches.put(playerUUID, new MobWatchPlayer(player));
    }
}

