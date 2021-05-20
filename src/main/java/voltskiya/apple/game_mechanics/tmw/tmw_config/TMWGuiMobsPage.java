package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeBuilder;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeGui;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeInventorySlot;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.game_mechanics.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

public class TMWGuiMobsPage extends InventoryGuiPageScrollable {
    private final TMWGui tmwGui;

    public TMWGuiMobsPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        addMobs();
        setSlots();
    }

    private void addMobs() {
        for (MobType mob : MobTypeDatabase.getAll()) {
            add(new MobTypeInventorySlot(mob, tmwGui));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> e.getWhoClicked().openInventory(new MobTypeGui(tmwGui, new MobTypeBuilder()).getInventory()),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)),4);
    }

    @Override
    public String getName() {
        return "All Mobs";
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    protected int getScrollIncrement() {
        return 8;
    }
}
