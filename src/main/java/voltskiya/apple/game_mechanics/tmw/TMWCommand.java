package voltskiya.apple.game_mechanics.tmw;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
