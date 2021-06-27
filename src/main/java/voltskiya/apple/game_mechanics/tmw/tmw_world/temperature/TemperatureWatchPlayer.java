package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.TemperatureChecks.ClothingTemperature;

public class TemperatureWatchPlayer implements Runnable {
    private static final long WATCH_PLAYER_INTERVAL = 20;
    private final Player player;
    private final WatchPlayer watchPlayer;

    public TemperatureWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
        this.watchPlayer = watchPlayer;
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this);

    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            // player left the game. just let this die and tell the listener that spawned us that we're dead
            WatchPlayerListener.get().leave(this.player.getUniqueId());
            return;
        }
        Location location = this.player.getLocation();
        BiomeType currentBiome = watchPlayer.getBiomeWatch().getCurrentGuess();

        BiomeType airTemp = watchPlayer.getBiomeWatch().getCurrentGuess();
        double insideness = TemperatureChecks.insideness(location);
        double blockHeatSource = TemperatureChecks.sources(location);
        double wind = TemperatureChecks.wind(location);
        ClothingTemperature clothing = TemperatureChecks.clothing(player);

    }
}
