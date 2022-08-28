package voltskiya.apple.game_mechanics.tmw.tmw_config;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.slot.base.ItemGuiSlotACD;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeGui;

public class TMWGuiMobsPage extends InventoryGuiPageScrollableACD<TMWGui> {

    private final TMWGui tmwGui;

    public TMWGuiMobsPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
    }

    @Override
    public void initialize() {

        setSlot(slotImpl((e1) -> tmwGui.parentNext(),
            makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)), 8);
        setSlot(
            slotImpl(e -> parentAddSubPage(new MobTypeGui(tmwGui, new MobType.MobTypeBuilder())),
                makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
    }

    @Override
    public void refreshPageItems() {
        clear();
        List<MobType> mobs = MobTypeDatabase.getAll();
        mobs.sort(Comparator.comparing(MobType::getName, String.CASE_INSENSITIVE_ORDER));
        for (MobType mob : mobs) {
            add(new MobTypeInventorySlot(mob, tmwGui));
        }
    }

    @Override
    public String getName() {
        return "All Mobs";
    }

    @Override
    public int size() {
        return 54;
    }

    public static class MobTypeInventorySlot implements ItemGuiSlotACD {

        private final MobType mob;
        private final TMWGui tmwGui;

        public MobTypeInventorySlot(MobType mob, TMWGui tmwGui) {
            this.mob = mob;
            this.tmwGui = tmwGui;
        }

        @Override
        public void dealWithClick(InventoryClickEvent event) {
            if (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.NUMBER_KEY) {
                ItemStack item = mob.toItem();
                item.setAmount(8);
                event.getWhoClicked().getInventory().addItem(item);
                return;
            }
            event.getWhoClicked()
                .openInventory(new MobTypeGui(tmwGui, mob.toBuilder()).getInventory());
        }

        @Override
        public ItemStack getItem() {
            return mob.toItem();
        }
    }
}
