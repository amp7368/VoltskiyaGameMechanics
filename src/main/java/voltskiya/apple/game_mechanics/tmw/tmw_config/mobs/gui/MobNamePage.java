package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageImplACD;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.TMWGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType.MobTypeBuilder;

class MobNamePage extends InventoryGuiPageImplACD<TMWGui> {

    private final MobTypeBuilder mob;

    public MobNamePage(TMWGui parent, MobTypeBuilder mob) {
        super(parent);
        this.mob = mob;
        setSlot(slotImpl(e -> {
        }, mob.getIconItem()), 0);
        setSlot(slotImpl(e -> {
            parentRemoveSubPage();
        }, new ItemStack(Material.GREEN_TERRACOTTA)), 8);
    }


    @Override
    public String getName() {
        return "Insert item representation";
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null)
            return;

        mob.setIcon(new MobType.MobTypeBuilder.MobIcon(item));

        setSlot(slotImpl(e -> {
        }, mob.getIconItem()), 0);
    }
}
