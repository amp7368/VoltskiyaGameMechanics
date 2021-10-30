package voltskiya.apple.game_mechanics.tmw.tmw_world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchPlayerListener implements Listener {
    private static WatchPlayerListener instance;
    private final Map<UUID, WatchPlayer> watches = new HashMap<>();

    public WatchPlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
        instance = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if (!watches.containsKey(playerUUID))
                watches.put(playerUUID, new WatchPlayer(player));
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), this::tick, 1, 1);
    }

    public static WatchPlayerListener get() {
        return instance;
    }

    public synchronized void leave(UUID playerUuidToLeave) {
        watches.remove(playerUuidToLeave);
    }

    @EventHandler
    public synchronized void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID playerUUID = player.getUniqueId();
        if (!watches.containsKey(playerUUID))
            watches.put(playerUUID, new WatchPlayer(player));
    }

    private synchronized void tick() {
        for (WatchPlayer watchPlayer : this.watches.values()) {
            watchPlayer.scheduleTicks();
        }
    }
}

