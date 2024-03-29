package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.TmwWatchConfig;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_world.PlayerTemperatureVisual;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayer;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchPlayerListener;
import voltskiya.apple.game_mechanics.tmw.tmw_world.WatchTickable;

public class TemperatureWatchPlayer implements WatchTickable {

    private static final double TIME_TO_HEAT_CHANGE = 10;
    private static final int SAVE_INTERVAL = 10;
    private final Player player;
    private final WatchPlayer watchPlayer;
    private final PlayerTemperatureVisual playerVisual;
    private final PlayerTemperature playerInfo;
    private final double wetness = 0;
    private int saveInterval = SAVE_INTERVAL;
    private int tickCount;

    public TemperatureWatchPlayer(Player player, PlayerTemperatureVisual playerVisual,
        WatchPlayer watchPlayer) {
        this.player = player;
        this.playerVisual = playerVisual;
        this.playerInfo = watchPlayer.getPlayerInfo();
        this.watchPlayer = watchPlayer;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            // player left the game. just let this die and tell the listener that spawned us that we're dead
            WatchPlayerListener.get().leave(this.player.getUniqueId());
            return;
        }
        if (this.player.getGameMode() == GameMode.SURVIVAL
            && PlayerTemperatureCommand.getTemperature()) {
            Location location = this.player.getLocation();
            @Nullable BiomeType currentBiome = watchPlayer.getBiomeWatch().getCurrentGuess();
            double airTemp = currentBiome == null ? 0
                : currentBiome.getTypicalTempNow(location.getWorld().getTime());
            double insideness = TemperatureChecks.insideness(location);
            double blockHeatSource = TemperatureChecks.sources(location);
            double wind = TemperatureChecks.wind(currentBiome, location);
            double wetness = TemperatureChecks.wetness(player);

            TemperatureChecks.ClothingTemperature clothing = TemperatureChecks.clothing(player);
            double finalBlockHeatSource = (1 + insideness) * blockHeatSource;
            double finalWind = clothing.resistWind(wind * (1 - insideness));
            double finalWetness = clothing.resistWet(wetness);
            double playerWetness = this.playerInfo.doWetTick(finalWetness);

            double airTemp2 = airTemp + finalBlockHeatSource;

            double fluidFactor = TemperatureChecks.fluidFactor(finalWind, playerWetness);
            double boundaries = 150;
            double airTemp3 = airTemp2 - (
                (boundaries / (1 + Math.pow(Math.E, (-airTemp2 / boundaries)))) * fluidFactor / 10);

            double feltTemperature = clothing.resistTemp(airTemp3);
            this.playerInfo.temperature += (feltTemperature - this.playerInfo.temperature)
                * TmwWatchConfig.getCheckInterval().heatTransferConstant;
            this.playerInfo.doTemperatureEffects(this.playerVisual);
            TextComponent msg = new TextComponent();
            msg.setText(String.format("final temp - %.2f, biome - %s", this.playerInfo.temperature,
                currentBiome == null ? "null" : currentBiome.getName()));
            this.player.sendActionBar(msg);
            if (--this.saveInterval <= 0) {
                this.saveInterval = SAVE_INTERVAL;
                this.playerInfo.saveThreaded();
            }
        }
    }

    @Override
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public void setTickCount(int i) {
        this.tickCount = i;
    }

    @Override
    public int getTicksPerRun() {
        return TmwWatchConfig.getCheckInterval().temperatureWatchPlayer;
    }
}
