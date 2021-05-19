package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotScrollable;

public class MobTypeInventorySlot extends InventoryGuiSlotScrollable {
    private MobType mob;
    private TMWGui tmwGui;

    public MobTypeInventorySlot(MobType mob, TMWGui tmwGui) {
        this.mob = mob;
        this.tmwGui = tmwGui;
    }

    @Override
    public void dealWithClick(InventoryClickEvent event) {
        event.getWhoClicked().openInventory(
                new MobTypeGui(tmwGui,mob.toBuilder()).getInventory()
        );
    }

    @Override
    public ItemStack getItem() {
        return mob.toItem();
    }
}
