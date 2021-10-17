package voltskiya.apple.game_mechanics.decay.config.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.template.DecayGuiGroupSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGrouping;
import voltskiya.apple.utilities.util.gui.acd.page.InventoryGuiPageScrollableACD;
import voltskiya.apple.utilities.util.gui.acd.page.ScrollableSectionACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiButtonTemplate;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotDoNothingACD;
import voltskiya.apple.utilities.util.gui.acd.slot.InventoryGuiSlotImplACD;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.Collections;
import java.util.List;

public class DecayGuiListBlocksPage extends InventoryGuiPageScrollableACD<DecayGui> {
    public DecayGuiListBlocksPage(DecayGui decayGui) {
        super(decayGui, false);
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("", 18, size()));
        setSlot(new InventoryGuiSlotImplACD(e -> parentRemoveSubPage(),
                InventoryUtils.makeItem(Material.RED_TERRACOTTA, "Back to navigation")), 0);
        setSlot(new InventoryGuiSlotDoNothingACD(InventoryUtils.makeItem(Material.DARK_OAK_SAPLING, 1, "To add a block",
                Collections.singletonList("Click a block in your inventory"))), 4);
        setSlot(InventoryGuiButtonTemplate.blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("").scroll(-1);
        }, InventoryUtils.makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(new InventoryGuiSlotImplACD((e) -> {
            this.getSection("").scroll(1);
        }, InventoryUtils.makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }

    private void addBlocks() {
        clear();
        for (DecayBlockTemplateGrouping grouping : DecayBlockDatabase.getAll()) {
            add(new InventoryGuiSlotImplACD(
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
            DecayBlockTemplateGrouping subPage = block != null ? block.copy() : new DecayBlockTemplateGrouping(currentItem);
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
