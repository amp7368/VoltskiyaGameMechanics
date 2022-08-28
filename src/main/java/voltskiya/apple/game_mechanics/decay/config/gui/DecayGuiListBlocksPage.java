package voltskiya.apple.game_mechanics.decay.config.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.page.ScrollableSectionACD;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.template.DecayGuiGroupSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;

public class DecayGuiListBlocksPage extends InventoryGuiPageScrollableACD<DecayGui> {

    public DecayGuiListBlocksPage(DecayGui decayGui) {
        super(decayGui, false);
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("", 18, size()));
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Back to navigation")), 0);
        setSlot(slotDoNothing(makeItem(Material.DARK_OAK_SAPLING, 1, "To add a block",
            Collections.singletonList("Click a block in your inventory"))), 4);
        setSlot(blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(-1);
        }, makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(1);
        }, makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }

    private void addBlocks() {
        clear();
        for (DecayBlockTemplateGrouping grouping : DecayBlockDatabase.getAll()) {
            add(slotImpl(
                e -> parentAddSubPage(new DecayGuiGroupSettingsPage(parent, grouping.copy())),
                grouping.getIcon().getItem()
            ));
        }
    }

    @Override
    public void onPlayerInventory(@NotNull InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null && currentItem.getType().isBlock()) {
            DecayBlockTemplateGrouping block = DecayBlockDatabase.getGroup(currentItem.getType());
            DecayBlockTemplateGrouping subPage =
                block != null ? block.copy() : new DecayBlockTemplateGrouping(currentItem);
            parentAddSubPage(new DecayGuiGroupSettingsPage(parent, subPage));
        }
    }

    @Override
    public void refreshPageItems() {
        addBlocks();
    }

    @Override
    public String getName() {
        return "Blocks List";
    }

    @Override
    public int size() {
        return 54;
    }
}
