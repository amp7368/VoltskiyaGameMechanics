package voltskiya.apple.game_mechanics.tmw.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.MobConfigPerWorld;
import voltskiya.apple.game_mechanics.tmw.TmwMobConfigDatabase;
import voltskiya.apple.utilities.util.message.SendMessage;

import java.util.Map;
import java.util.UUID;

@CommandAlias("mobs")
public class CommandMobsTmw extends BaseCommand {
    public CommandMobsTmw() {
        VoltskiyaPlugin.get().registerCommand(this);
    }

    @Subcommand("regenIntervalSet")
    @CommandCompletion("@range:20-100")
    public void interval(CommandSender sender, int interval) {
        if (interval < 10) {
            SendMessage.sendMessageRed(sender, "Cannot set the interval to less than 10");
            return;
        }
        TmwMobConfigDatabase.get().setRegenInterval(interval);
        SendMessage.sendMessageGreen(sender, "Set the interval to %d", interval);
    }

    @Subcommand("regenIntervalGet")
    public void interval(CommandSender sender) {
        SendMessage.sendMessageGreen(sender, "The interval is set to %d", TmwMobConfigDatabase.get().getRegenInterval());
    }

    @Subcommand("worldSpawningSet")
    @CommandCompletion("@worlds true|false")
    public void worldSpawning(CommandSender sender, String worldName, boolean isSpawning) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            SendMessage.sendMessageRed(sender, "Failed to find the world '%s'", worldName);
            return;
        }
        TmwMobConfigDatabase.get().setWorldSpawning(world.getUID(), isSpawning);
        SendMessage.sendMessageGreen(sender, "World spawning in %s is now %s", world.getName(), isSpawning ? "on" : "off");
    }

    @Subcommand("worldSpawningGet")
    @CommandCompletion("@worlds")
    public void worldSpawning(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            SendMessage.sendMessageRed(sender, "Failed to find the world '%s'", worldName);
            return;
        }
        boolean isSpawning = TmwMobConfigDatabase.get().getWorldSpawning(world.getUID());
        SendMessage.sendMessageGreen(sender, "World spawning in %s is %s", world.getName(), isSpawning ? "on" : "off");
    }

    @Subcommand("spawningSet")
    @CommandCompletion("true|false")
    public void spawning(CommandSender sender, boolean isSpawning) {
        TmwMobConfigDatabase.get().setIsSpawningMobs(isSpawning);
        if (isSpawning) {
            SendMessage.sendMessageGreen(sender, "Un-paused spawning for all worlds");
        } else {
            SendMessage.sendMessageGreen(sender, "Paused spawning for all worlds");
        }
    }

    @Subcommand("spawningGet")
    public void spawning(CommandSender sender) {
        boolean isSpawning = TmwMobConfigDatabase.get().isSpawningMobs();
        if (isSpawning) {
            SendMessage.sendMessageGreen(sender, "Spawning for all worlds is not paused");
        } else {
            SendMessage.sendMessageGreen(sender, "Spawning for all worlds is paused");
        }
    }

    @Subcommand("info")
    public void info(CommandSender sender) {
        boolean isSpawning = TmwMobConfigDatabase.get().isSpawningMobs();
        SendMessage.sendMessageAqua(sender, "The interval for mob spawning is %d ticks", TmwMobConfigDatabase.get().getRegenInterval());
        SendMessage.sendMessageAqua(sender, "Spawning for all worlds is %s",
                isSpawning ?
                        ChatColor.GREEN + "un-paused" :
                        ChatColor.RED + "paused"
        );
        SendMessage.sendMessage(sender, ChatColor.GRAY, "Worlds: ");
        for (Map.Entry<UUID, MobConfigPerWorld> world : TmwMobConfigDatabase.get().getAllWorlds().entrySet()) {
            World bukkitWorld = Bukkit.getWorld(world.getKey());
            if (bukkitWorld == null) continue;
            String spawningString = world.getValue().isMobSpawning() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF";
            SendMessage.sendMessage(sender, ChatColor.LIGHT_PURPLE, "%s: %sSpawning-%s", bukkitWorld.getName(), ChatColor.GRAY, spawningString);
        }
    }
}
