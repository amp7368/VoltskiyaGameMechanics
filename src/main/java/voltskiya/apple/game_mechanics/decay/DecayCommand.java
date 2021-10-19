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
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;
import voltskiya.apple.utilities.util.wand.WandToolList;

@CommandAlias("tmwdecay")
public class DecayCommand extends BaseCommand {
    public DecayCommand() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("gui")
    public void gui(Player player) {
        player.openInventory(new DecayGui().getInventory());
    }

    @Subcommand("wand")
    public class Wand extends BaseCommand {
        @Default
        public void giveWand(Player player) {
            player.getInventory().addItem(InventoryUtils.addDataString(
                    InventoryUtils.makeItem(Material.DEAD_BUSH, 1, "Decay Wand", null),
                    DecayWand.WAND_KEY,
                    ""
            ));
        }

        @Subcommand("give")
        public void giveWand2(Player player) {
            giveWand(player);
        }

        @Subcommand("radius")
        @CommandCompletion("#")
        public void size(Player player, int size) {
            DecayWand wand = WandToolList.getPlayerWand(DecayWand.WAND_KEY, player, DecayWand.class);
            wand.setSize(size);
            player.sendMessage(ChatColor.GREEN + "Set the radius to " + size);
        }

        @Subcommand("force")
        @CommandCompletion("#")
        public void force(Player player, int force) {
            DecayWand wand = WandToolList.getPlayerWand(DecayWand.WAND_KEY, player, DecayWand.class);
            wand.setForce(force);
            player.sendMessage(ChatColor.GREEN + "Set the force to " + force);
        }
    }
}
