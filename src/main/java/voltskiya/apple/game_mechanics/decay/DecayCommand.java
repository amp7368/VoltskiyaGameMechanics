package voltskiya.apple.game_mechanics.decay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.decay.config.DecayGui;

@CommandAlias("tmwdecay")
public class DecayCommand extends BaseCommand {
    public DecayCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        player.openInventory(new DecayGui(player).getInventory());
    }
}
