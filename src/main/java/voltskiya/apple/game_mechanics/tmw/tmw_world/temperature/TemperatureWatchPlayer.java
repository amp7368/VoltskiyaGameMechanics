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
    private static final double TIME_TO_HEAT_CHANGE = 10;
    private final Player player;
    private final WatchPlayer watchPlayer;
    private static final int SAVE_INTERVAL = 10;
    private final PlayerTemperature playerInfo;
    private double wetness = 0;
    private int saveInterval = SAVE_INTERVAL;

    public TemperatureWatchPlayer(Player player, WatchPlayer watchPlayer) {
        this.player = player;
        this.playerInfo = watchPlayer.getPlayerInfo();
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
        double wind = TemperatureChecks.wind(currentBiome, location);
        double wetness = TemperatureChecks.wetness(player);

        ClothingTemperature clothing = TemperatureChecks.clothing(player);
        double finalBlockHeatSource = (1 + insideness) * blockHeatSource;
        double finalWind = clothing.resistWind(wind * insideness);
        double finalWetness = clothing.resistWet(wetness);
        double playerWetness = playerInfo.doWetTick(finalWetness);
        double finalAirTemp = airTemp * (1 - insideness) + finalBlockHeatSource;
        double feelsLikeTemp = clothing.resistTemp(finalAirTemp); //todo .5 is arbitrary
        this.playerInfo.temperature += (feelsLikeTemp - this.playerInfo.temperature) / TIME_TO_HEAT_CHANGE * (1 + finalWind);

        TextComponent msg = new TextComponent();
        msg.setText(String.format("final temp - %.2f, biome - %s", this.playerInfo.temperature, currentBiome == null ? "null" : currentBiome.getName()));
        this.player.sendActionBar(msg);
        if (--this.saveInterval <= 0) {
            this.saveInterval = SAVE_INTERVAL;
            this.playerInfo.saveThreaded();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, WATCH_PLAYER_INTERVAL);
    }
}
