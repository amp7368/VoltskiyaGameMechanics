package voltskiya.apple.game_mechanics.temperature;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaModule;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.temperature.player.TemperatureAllPlayers;
import voltskiya.apple.game_mechanics.temperature.player.TemperaturePlayerJoinListener;

public class PluginTemperature extends VoltskiyaModule {
    @Override
    public void enable() {
        new TemperaturePlayerJoinListener();
        for (Player player : Bukkit.getOnlinePlayers()) TemperatureAllPlayers.addPlayer(player);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VoltskiyaPlugin.get(), TemperatureAllPlayers::tickFromBukkit, 0, 20);
    }

    @Override
    public String getName() {
        return "temperature";
    }
}
