package voltskiya.apple.game_mechanics.tmw;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;

@CommandAlias("tmw")
public class TMWCommand extends BaseCommand {
    public TMWCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        player.openInventory(new TMWGui().getInventory());
    }
}
