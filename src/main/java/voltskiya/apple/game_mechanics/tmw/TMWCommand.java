package voltskiya.apple.game_mechanics.tmw;

import apple.nms.decoding.world.DecodeBiome;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.BiomeTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorld;

@CommandAlias("tmw")
@CommandPermission("voltskiya.tmw")
public class TMWCommand extends BaseCommand {

    public TMWCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        player.openInventory(new TMWGui(player).getInventory());
    }

    @Subcommand("map world please")
    @CommandCompletion("x1 z1 x2 z2")
    public void map(Player player, int x1, int z1, int x2, int z2) {
        // scan the entire world ig
        new ScanWorld(player.getWorld(), x1, z1, x2, z2).scanAll();
    }

    @Subcommand("guess")
    public void guess(Player player) {
        long a = System.currentTimeMillis();
        final Location location = player.getLocation();
        @Nullable ResourceLocation minecraft = DecodeBiome.getBiomeKey(location.getWorld(),
            location.getBlockX(), location.getBlockY(), location.getBlockZ()).location();

        long b = System.currentTimeMillis();
        @Nullable BiomeType currentGuess;
        if (minecraft != null) {
            currentGuess = BiomeTypeDatabase.getBiome(minecraft);
        }
        long c = System.currentTimeMillis();
        player.sendMessage((b - a) + " getBiomeKey");
        player.sendMessage((c - b) + " getBiome");
        player.sendMessage(minecraft.toString());
    }
}
