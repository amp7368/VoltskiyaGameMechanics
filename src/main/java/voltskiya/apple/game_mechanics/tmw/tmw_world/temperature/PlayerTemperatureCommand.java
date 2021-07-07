package voltskiya.apple.game_mechanics.tmw.tmw_world.temperature;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;

@CommandAlias("temperatureReal")
public class PlayerTemperatureCommand extends BaseCommand {
    private final static Object sync = new Object();
    private static boolean temperature = true;

    public PlayerTemperatureCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    public static boolean getTemperature() {
        synchronized (sync) {
            return temperature;
        }
    }

    @Subcommand("off")
    public void off(Player commandSender) {
        synchronized (sync) {
            temperature = false;
            Bukkit.broadcastMessage(ChatColor.AQUA + "Temperature is now" + ChatColor.RED + " off");
        }
    }

    @Subcommand("on")
    public void on(CommandSender commandSender) {
        synchronized (sync) {
            temperature = true;
            Bukkit.broadcastMessage(ChatColor.AQUA + "Temperature is now" + ChatColor.GREEN + " on");
        }
    }
}
