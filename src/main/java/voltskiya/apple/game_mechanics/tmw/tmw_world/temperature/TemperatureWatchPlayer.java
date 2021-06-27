package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
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
        @Nullable BiomeType currentBiome = watchPlayer.getBiomeWatch().getCurrentGuess();
        double airTemp = currentBiome == null ? 0 : currentBiome.getTypicalTempNow(location.getWorld().getTime());
        double insideness = TemperatureChecks.insideness(location);
        double blockHeatSource = TemperatureChecks.sources(location);
        double wind = TemperatureChecks.wind(location);
        ClothingTemperature clothing = TemperatureChecks.clothing(player);
        double finalBlockHeatSource = (1 + insideness) * blockHeatSource;
        double finalWind = clothing.resistWind(wind * insideness);
        double finalAirTemp = airTemp * (1 - insideness) + finalBlockHeatSource;
        double feelsLikeTemp = clothing.resistTemp(finalAirTemp * finalWind);

        TextComponent msg = new TextComponent();
        msg.setText(String.format("final temp - %.2f, biome - %s", feelsLikeTemp, currentBiome == null ? "null" : currentBiome.getName()));
        player.sendActionBar(msg);
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, WATCH_PLAYER_INTERVAL);
    }
}
