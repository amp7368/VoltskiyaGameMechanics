package voltskiya.apple.game_mechanics.electricity;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import voltskiya.apple.game_mechanics.VoltskiyaPlugin;
import voltskiya.apple.game_mechanics.electricity.placement.PlayerInteractElectric;

@CommandAlias("electric")
public class CommandElectricity extends BaseCommand {
    public CommandElectricity() {
        VoltskiyaPlugin.get().getCommandManager().registerCommand(this);
    }

    @Subcommand("wrench")
    public void wrench(Player player) {
        ItemStack wrench = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = wrench.getItemMeta();
        itemMeta.getPersistentDataContainer().set(PlayerInteractElectric.WRENCH_KEY, PersistentDataType.BYTE, (byte) 0);
        wrench.setItemMeta(itemMeta);
        player.getInventory().addItem(wrench);
    }

}
