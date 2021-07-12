package voltskiya.apple.game_mechanics.tmw;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.deleteme_later.chunks.TemperatureChunk;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ComputedBiomeChunk;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorldBiomes;
import voltskiya.apple.game_mechanics.tmw.tmw_world.mobs.MobRegen;
import voltskiya.apple.utilities.util.data_structures.Pair;

import java.util.*;

@CommandAlias("tmw")
public class TMWCommand extends BaseCommand {
    private static final Map<UUID, InventoryHolder> openMeOnNextCommand = new HashMap<>();
    private static final Object sync = new Object();

    public TMWCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    public static void addOpenMeNext(UUID player, InventoryHolder openMe) {
        synchronized (sync) {
            openMeOnNextCommand.put(player, openMe);
        }
    }

    @Subcommand("gui")
    public void gui(Player player) {
        synchronized (sync) {
            InventoryHolder openMe = openMeOnNextCommand.remove(player.getUniqueId());
            if (openMe != null) {
                player.openInventory(openMe.getInventory());
                return;
            }
        }
        player.openInventory(new TMWGui(player).getInventory());
    }

    @Subcommand("mobs pause")
    public void pause(CommandSender sender) {
        sender.sendMessage("mob spawning is " + (MobRegen.pause() ? "on" : "off"));

    }

    @Subcommand("biome")
    public void biome(Player player) {
        List<Pair<BiomeType, Double>> guess = new ComputedBiomeChunk(BiomeTypeBuilderRegisterBlocks.compute(player.getChunk(), new HashSet<>()
                , player.getLocation().getBlockX() - player.getChunk().getX() * TemperatureChunk.BLOCKS_IN_A_CHUNK
                , player.getLocation().getBlockY()
                , player.getLocation().getBlockZ() - player.getChunk().getZ() * TemperatureChunk.BLOCKS_IN_A_CHUNK
        )).getGuessedBiomes();
        for (Pair<BiomeType, Double> biomes : guess) {
            player.sendMessage(String.format("%s - %.3f", biomes.getKey().getName(), biomes.getValue()));
        }
    }

    @Subcommand("map world please")
    @CommandCompletion("x1 z1 x2 z2")
    public void map(Player player, int x1, int z1, int x2, int z2) {
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage("Please stand on the surface you want to scan in a different gamemode");
            return;
        }
        // scan the entire world ig
        new ScanWorldBiomes(player.getLocation(), x1, z1, x2, z2).scan();
    }
}
