package voltskiya.apple.game_mechanics.decay;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.decay.config.gui.DecayGui;

@CommandAlias("decay")
public class DecayCommand extends BaseCommand {

    public DecayCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        player.openInventory(new DecayGui().getInventory());
    }

    @Subcommand("wand")
    public class DecayWandCommand extends BaseCommand {

        @Default
        public void giveWand(Player player) {
            player.getInventory()
                .addItem(PluginDecay.DECAY_WAND.createItem(Material.DEAD_BUSH, "Decay Wand"));
        }

        @Subcommand("give")
        public void giveWand2(Player player) {
            giveWand(player);
        }

        @Subcommand("radius")
        @CommandCompletion("#")
        public void size(Player player, int size) {
            DecayWand wand = PluginDecay.DECAY_WAND.getWand(player);
            wand.setSize(size);
            player.sendMessage(ChatColor.GREEN + "Set the radius to " + size);
        }

        @Subcommand("force")
        @CommandCompletion("#")
        public void force(Player player, int force) {
            DecayWand wand = PluginDecay.DECAY_WAND.getWand(player);
            wand.setForce(force);
            player.sendMessage(ChatColor.GREEN + "Set the force to " + force);
        }
    }
}
