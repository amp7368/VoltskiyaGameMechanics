package voltskiya.apple.game_mechanics.deleteme_later;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.deleteme_later.player.TemperatureAllPlayers;
import voltskiya.apple.game_mechanics.deleteme_later.player.TemperaturePlayerJoinListener;

public class PluginTemperature extends PluginManagedModule {
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
