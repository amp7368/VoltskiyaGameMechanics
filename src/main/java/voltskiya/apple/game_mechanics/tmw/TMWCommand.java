package voltskiya.apple.game_mechanics.tmw;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_world.biomes.ScanWorld;

@CommandAlias("tmw")
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
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage("Please stand on the surface you want to scan in a different gamemode");
            return;
        }
        // scan the entire world ig
        new ScanWorld(player.getWorld(), x1, z1, x2, z2).scanAll();
    }
}
