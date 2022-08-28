package voltskiya.apple.game_mechanics.tmw.commands;

import apple.mc.utilities.player.chat.SendMessage;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.MobConfigPerWorld;
import voltskiya.apple.game_mechanics.tmw.TmwMobConfigDatabase;

@CommandAlias("mobs")
public class CommandMobsTmw extends BaseCommand implements SendMessage {

    public CommandMobsTmw() {
        VoltskiyaPlugin.get().registerCommand(this);
    }

    @Subcommand("regenIntervalSet")
    @CommandCompletion("@range:20-100")
    public void interval(CommandSender sender, int interval) {
        if (interval < 10) {
            red(sender, "Cannot set the interval to less than 10");
            return;
        }
        TmwMobConfigDatabase.get().setRegenInterval(interval);
        green(sender, "Set the interval to %d", interval);
    }

    @Subcommand("regenIntervalGet")
    public void interval(CommandSender sender) {
        green(sender, "The interval is set to %d", TmwMobConfigDatabase.get().getRegenInterval());
    }

    @Subcommand("worldSpawningSet")
    @CommandCompletion("@worlds true|false")
    public void worldSpawning(CommandSender sender, String worldName, boolean isSpawning) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            red(sender, "Failed to find the world '%s'", worldName);
            return;
        }
        TmwMobConfigDatabase.get().setWorldSpawning(world.getUID(), isSpawning);
        green(sender, "World spawning in %s is now %s", world.getName(), isSpawning ? "on" : "off");
    }

    @Subcommand("worldSpawningGet")
    @CommandCompletion("@worlds")
    public void worldSpawning(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            red(sender, "Failed to find the world '%s'", worldName);
            return;
        }
        boolean isSpawning = TmwMobConfigDatabase.get().getWorldSpawning(world.getUID());
        green(sender, "World spawning in %s is %s", world.getName(), isSpawning ? "on" : "off");
    }

    @Subcommand("spawningSet")
    @CommandCompletion("true|false")
    public void spawning(CommandSender sender, boolean isSpawning) {
        TmwMobConfigDatabase.get().setIsSpawningMobs(isSpawning);
        if (isSpawning) {
            green(sender, "Un-paused spawning for all worlds");
        } else {
            green(sender, "Paused spawning for all worlds");
        }
    }

    @Subcommand("spawningGet")
    public void spawning(CommandSender sender) {
        boolean isSpawning = TmwMobConfigDatabase.get().isSpawningMobs();
        if (isSpawning) {
            green(sender, "Spawning for all worlds is not paused");
        } else {
            green(sender, "Spawning for all worlds is paused");
        }
    }

    @Subcommand("info")
    public void info(CommandSender sender) {
        boolean isSpawning = TmwMobConfigDatabase.get().isSpawningMobs();
        aqua(sender, "The interval for mob spawning is %d ticks",
            TmwMobConfigDatabase.get().getRegenInterval());
        aqua(sender, "Spawning for all worlds is %s",
            isSpawning ?
                ChatColor.GREEN + "un-paused" :
                ChatColor.RED + "paused"
        );
        sender.sendMessage(ChatColor.GRAY + "Worlds: ");
        for (Map.Entry<UUID, MobConfigPerWorld> world : TmwMobConfigDatabase.get().getAllWorlds()
            .entrySet()) {
            World bukkitWorld = Bukkit.getWorld(world.getKey());
            if (bukkitWorld == null) {
                continue;
            }
            String spawningString =
                world.getValue().isMobSpawning() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF";
            sender.sendMessage(
                ChatColor.LIGHT_PURPLE + String.format("%s: %sSpawning-%s", bukkitWorld.getName(),
                    ChatColor.GRAY, spawningString));
        }
    }
}
