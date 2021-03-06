package voltskiya.apple.game_mechanics.tmw.tmw_config;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobTypeDatabase;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.gui.MobTypeGui;
import voltskiya.apple.utilities.util.gui.InventoryGuiPageScrollable;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotGeneric;
import voltskiya.apple.utilities.util.gui.InventoryGuiSlotScrollable;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Comparator;
import java.util.List;

public class TMWGuiMobsPage extends InventoryGuiPageScrollable {
    private final TMWGui tmwGui;

    public TMWGuiMobsPage(TMWGui tmwGui) {
        super(tmwGui);
        this.tmwGui = tmwGui;
        addMobs();
        setSlots();
    }

    private void addMobs() {
        clear();
        List<MobType> mobs = MobTypeDatabase.getAll();
        mobs.sort(Comparator.comparing(MobType::getName, String.CASE_INSENSITIVE_ORDER));
        for (MobType mob : mobs) {
            add(new MobTypeInventorySlot(mob, tmwGui));
        }
    }

    @Override
    public void setSlots() {
        super.setSlots();
        setSlot(new InventoryGuiSlotGeneric((e1) -> tmwGui.nextPage(1), InventoryUtils.makeItem(Material.GREEN_TERRACOTTA, 1, "Next Page", null)
        ), 8);
        setSlot(new InventoryGuiSlotGeneric(e -> e.getWhoClicked().openInventory(new MobTypeGui(tmwGui, new MobType.MobTypeBuilder()).getInventory()),
                InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "Add a mob", null)), 4);
    }

    @Override
    public void fillInventory() {
        addMobs();
        super.fillInventory();
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

    public static class MobTypeInventorySlot extends InventoryGuiSlotScrollable {
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
            event.getWhoClicked().openInventory(
                    new MobTypeGui(tmwGui, mob.toBuilder()).getInventory()
            );
        }

        @Override
        public ItemStack getItem() {
            return mob.toItem();
        }
    }
}
