package voltskiya.apple.game_mechanics.decay.config.gui;

import apple.mc.utilities.inventory.gui.acd.page.InventoryGuiPageScrollableACD;
import apple.mc.utilities.inventory.gui.acd.page.ScrollableSectionACD;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.decay.config.database.DecayBlockSettingsDatabase;
import voltskiya.apple.game_mechanics.decay.config.gui.settings.DecayGuiBlockSettingsPage;
import voltskiya.apple.game_mechanics.decay.config.template.DecayBlockTemplateGroupingSettings;

public class DecayGuiListBlockSettingsPage extends InventoryGuiPageScrollableACD<DecayGui> {

    public DecayGuiListBlockSettingsPage(DecayGui parent) {
        super(parent);
    }

    @Override
    public void initialize() {
        addSection(new ScrollableSectionACD("", 18, size()));
        setSlot(slotDoNothing(
                makeItem(Material.OAK_SAPLING, 1, "To make a new settings",
                    List.of("Click an item in your inventory"))),
            4);
        setSlot(slotImpl(e -> parentRemoveSubPage(),
            makeItem(Material.RED_TERRACOTTA, "Back to navigation")), 0);
        setSlot(blackGlassDoNothing(), 9, 10, 11, 12, 13, 14, 15);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(-1);
        }, makeItem(Material.REDSTONE_TORCH, 1, "Up", List.of("Scroll up"))), 17);
        this.setSlot(slotImpl((e) -> {
            this.getSection("").scroll(1);
        }, makeItem(Material.LEVER, 1, "Down", List.of("Scroll down"))), 16);
    }

    @Override
    public void refreshPageItems() {
        clear();
        for (DecayBlockTemplateGroupingSettings settings : DecayBlockSettingsDatabase.getAll()) {
            this.add(slotImpl((e) -> {
                parentAddSubPage(new DecayGuiBlockSettingsPage(parent, settings.copy()));
            }, settings.getIcon()));
        }
    }

    @Override
    public void onPlayerInventory(InventoryClickEvent event) {
        @Nullable ItemStack item = event.getCurrentItem();
        if (item != null) {
            parentAddSubPage(new DecayGuiBlockSettingsPage(parent,
                DecayBlockTemplateGroupingSettings.createDefault(item)));
        }
    }

    @Override
    public String getName() {
        return "List Settings Page";
    }

    @Override
    public int size() {
        return 54;
    }
}
