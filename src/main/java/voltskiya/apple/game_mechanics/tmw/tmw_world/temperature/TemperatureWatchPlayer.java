package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.PlayerTemperatureVisual;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.temperature.TemperatureChecks.ClothingTemperature;

public class TemperatureWatchPlayer implements Runnable {
    private static final long WATCH_PLAYER_INTERVAL = 20;
    private static final double TIME_TO_HEAT_CHANGE = 10;
    private final Player player;
    private final WatchPlayer watchPlayer;
    private static final int SAVE_INTERVAL = 10;
    private PlayerTemperatureVisual playerVisual;
    private final PlayerTemperature playerInfo;
    private double wetness = 0;
    private int saveInterval = SAVE_INTERVAL;

    public TemperatureWatchPlayer(Player player, PlayerTemperatureVisual playerVisual, WatchPlayer watchPlayer) {
        this.player = player;
        this.playerVisual = playerVisual;
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
        if (this.player.getGameMode() == GameMode.SURVIVAL) {
            Location location = this.player.getLocation();
            @Nullable BiomeType currentBiome = watchPlayer.getBiomeWatch().getCurrentGuess();
            double airTemp = currentBiome == null ? 0 : currentBiome.getTypicalTempNow(location.getWorld().getTime());
            double insideness = TemperatureChecks.insideness(location);
            double blockHeatSource = TemperatureChecks.sources(location);
            double wind = TemperatureChecks.wind(currentBiome, location);
            double wetness = TemperatureChecks.wetness(player);

            ClothingTemperature clothing = TemperatureChecks.clothing(player);
            double finalBlockHeatSource = (1 + insideness) * blockHeatSource;
            double finalWind = clothing.resistWind(wind * (1 - insideness));
            double finalWetness = clothing.resistWet(wetness);
            double playerWetness = this.playerInfo.doWetTick(finalWetness);

            double airTemp2 = airTemp + finalBlockHeatSource;

            double fluidFactor = TemperatureChecks.fluidFactor(finalWind, playerWetness);
            double boundaries = 150;
            double airTemp3 = airTemp2 - ((boundaries / (1 + Math.pow(Math.E, (-airTemp2 / boundaries)))) * fluidFactor / 10);

            double feltTemperature = clothing.resistTemp(airTemp3);
            double heatTransferConstant = 0.01;
            double surfaceArea = 7;
            this.playerInfo.temperature += feltTemperature * surfaceArea * heatTransferConstant - surfaceArea * heatTransferConstant * this.playerInfo.temperature;
            this.playerInfo.doTemperatureEffects(this.playerVisual);
            TextComponent msg = new TextComponent();
            msg.setText(String.format("final temp - %.2f, biome - %s", this.playerInfo.temperature, currentBiome == null ? "null" : currentBiome.getName()));
            this.player.sendActionBar(msg);
            if (--this.saveInterval <= 0) {
                this.saveInterval = SAVE_INTERVAL;
                this.playerInfo.saveThreaded();
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), this, WATCH_PLAYER_INTERVAL);
    }
}
